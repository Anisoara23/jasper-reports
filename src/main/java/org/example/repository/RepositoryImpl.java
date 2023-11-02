package org.example.repository;

import org.example.parser.DateParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;

import static org.example.utils.ReportUtils.CREATE_TABLE_QUERY;
import static org.example.utils.ReportUtils.DATE_FORMAT;
import static org.example.utils.ReportUtils.INSERT_QUERY;
import static org.example.utils.ReportUtils.IT;
import static org.example.utils.ReportUtils.ITALIA;
import static org.example.utils.ReportUtils.MD;
import static org.example.utils.ReportUtils.PASSWORD;
import static org.example.utils.ReportUtils.QUERY;
import static org.example.utils.ReportUtils.URL;
import static org.example.utils.ReportUtils.USER;

public class RepositoryImpl implements Repository {

    private static final Connection connection;

    private static final Statement statement;

    static {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            statement = connection.createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void prepareDatabase(File file) {
        try {
            createTable();
            insertData(file);
        } catch (ParserConfigurationException | SAXException | IOException | SQLException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResultSet getResultSet() {
        try {
            return statement.executeQuery(QUERY);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void insertData(File file) throws ParserConfigurationException, SAXException, IOException, SQLException, ParseException {

        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = documentBuilder.parse(file);

        NodeList holidaysList = document.getElementsByTagName("holydays");

        for (int i = 0; i < holidaysList.getLength(); i++) {
            Node holidayNode = holidaysList.item(i);
            PreparedStatement preparedStatement = parseHolidayNode(holidayNode);
            preparedStatement.execute();
        }
    }

    private static PreparedStatement parseHolidayNode(Node holidayNode) throws SQLException, ParseException {
        PreparedStatement preparedStatement = connection.prepareStatement(INSERT_QUERY);

        NodeList childNodes = holidayNode.getChildNodes();

        for (int j = 0; j < childNodes.getLength(); j++) {
            Node current = childNodes.item(j);
            if (current.getNodeType() == Node.ELEMENT_NODE) {
                String nodeName = current.getNodeName().toLowerCase();
                String content = current.getTextContent();

                if (nodeName.equalsIgnoreCase("country")) {
                    preparedStatement.setString(1,
                            content.equalsIgnoreCase(ITALIA) ? IT : MD);
                } else if (nodeName.equalsIgnoreCase("date")) {
                    Date date = parseDate(content, DATE_FORMAT);
                    preparedStatement.setDate(2, date);
                } else {
                    preparedStatement.setString(3, content);
                }
            }
        }

        return preparedStatement;
    }

    private static Date parseDate(String content, String dateFormat) throws ParseException {
        return new Date(DateParser.parseDate(content, dateFormat).getTime());
    }

    private static void createTable() throws SQLException {
        statement.executeUpdate(CREATE_TABLE_QUERY);
    }
}
