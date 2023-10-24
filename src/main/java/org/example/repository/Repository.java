package org.example.repository;

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
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.example.utils.ReportUtils.DATE_FORMAT_STRING;
import static org.example.utils.ReportUtils.HOLIDAYS_XML;
import static org.example.utils.ReportUtils.IT;
import static org.example.utils.ReportUtils.ITALIA;
import static org.example.utils.ReportUtils.MD;
import static org.example.utils.ReportUtils.PASSWORD;
import static org.example.utils.ReportUtils.URL;
import static org.example.utils.ReportUtils.USER;

public class Repository {

    public void prepareDatabase() {
        try (
                Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
                Statement statement = connection.createStatement();
        ) {
            statement.executeUpdate(createTable());
            insertData(connection);
        } catch (SQLException | ParserConfigurationException | IOException | SAXException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private static void insertData(Connection connection) throws ParserConfigurationException, SAXException, IOException, SQLException, ParseException {
        File file = new File(HOLIDAYS_XML);
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(file);

        String insertQuery = "INSERT INTO " +
                "holidays(country, \"date\", \"name\")" +
                "VALUES(?, ?, ?);";

        PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);

        NodeList holydaysList = document.getElementsByTagName("holydays");
        int holydaysListSize = holydaysList.getLength();

        for (int i = 0; i < holydaysListSize; i++) {
            NodeList holydayNode = holydaysList.item(i).getChildNodes();

            int holydayNodeLength = holydayNode.getLength();
            Node current;

            for (int j = 0; j < holydayNodeLength; j++) {
                current = holydayNode.item(j);
                if (current.getNodeType() == Node.ELEMENT_NODE) {
                    String nodeName = current.getNodeName().toLowerCase();
                    String content = current.getTextContent();
                    if (nodeName.equalsIgnoreCase("country")) {
                        preparedStatement.setString(1, content.equalsIgnoreCase(ITALIA)?IT:MD);
                    } else if (nodeName.equalsIgnoreCase("date")) {
                        Date date = new Date(new SimpleDateFormat(DATE_FORMAT_STRING).parse(content).getTime());
                        preparedStatement.setDate(2, date);
                    } else {
                        preparedStatement.setString(3, content);
                    }
                }
            }
            preparedStatement.execute();
        }
    }

    private static String createTable() {
        return "DROP TABLE IF EXISTS holidays; " +
                "CREATE TABLE public.holidays (" +
                "country varchar NOT NULL, " +
                "\"date\" date NOT NULL, " +
                "\"name\" varchar NOT NULL" +
                ");";
    }
}
