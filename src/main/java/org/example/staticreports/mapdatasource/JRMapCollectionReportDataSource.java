package org.example.staticreports.mapdatasource;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import org.example.parser.DateParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.example.utils.ReportUtils.DATE_FORMAT;
import static org.example.utils.ReportUtils.IT;
import static org.example.utils.ReportUtils.ITALIA;
import static org.example.utils.ReportUtils.MD;

public class JRMapCollectionReportDataSource {

    public static JRDataSource getDataSource(File file) {
        try {
            final List<Map<String, ?>> holidaysMap = parseDocument(file);
            return new JRMapCollectionDataSource(holidaysMap);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static List<Map<String, ?>> parseDocument(File file) throws Exception {
        List<Map<String, ?>> holidaysMap = new ArrayList<>();

        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = documentBuilder.parse(file);

        NodeList holidaysList = document.getElementsByTagName("holydays");

        for (int i = 0; i < holidaysList.getLength(); i++) {
            Node holidayNode = holidaysList.item(i);
            Map<String, Object> holidayMap = parseHolidayNode(holidayNode);
            holidaysMap.add(holidayMap);
        }

        return holidaysMap;
    }

    private static Map<String, Object> parseHolidayNode(Node holidayNode) throws Exception {
        Map<String, Object> holidayMap = new HashMap<>();

        NodeList childNodes = holidayNode.getChildNodes();

        for (int j = 0; j < childNodes.getLength(); j++) {
            Node current = childNodes.item(j);

            if (current.getNodeType() == Node.ELEMENT_NODE) {
                String nodeName = current.getNodeName().toLowerCase();
                String content = current.getTextContent();

                if (nodeName.equalsIgnoreCase("country")) {
                    holidayMap.put(nodeName,
                            content.equalsIgnoreCase(ITALIA) ? IT : MD);
                } else if (nodeName.equalsIgnoreCase("date")) {
                    Date date = DateParser.parseDate(content, DATE_FORMAT);
                    holidayMap.put(nodeName, date);
                } else {
                    holidayMap.put(nodeName, content);
                }
            }
        }

        return holidayMap;
    }


}


