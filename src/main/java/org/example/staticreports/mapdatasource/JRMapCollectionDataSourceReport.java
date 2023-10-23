package org.example.staticreports.mapdatasource;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.staticreports.JRResultSetDataSourceReport;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JRMapCollectionDataSourceReport {

    private static final Logger LOGGER = LogManager.getLogger(JRResultSetDataSourceReport.class);

    public static final List<Map<String, ?>> HOLIDAYS_MAP = new ArrayList<>();


    public static void generateReport() {
        try {
            FileInputStream fileInputStream = new FileInputStream("src/main/resources/Holidays_Report.jrxml");
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

            JasperReport report = JasperCompileManager.compileReport(bufferedInputStream);
            LOGGER.info("Done compiling... ");

            generateDataSource();

            JRMapCollectionDataSource dataSource = new JRMapCollectionDataSource(HOLIDAYS_MAP);

            JasperPrint print = JasperFillManager.fillReport(report, null, dataSource);
            LOGGER.info("Done filling the report... ");

            JasperExportManager.exportReportToPdfFile(print, "src/main/resources/report.pdf");
            LOGGER.info("Done exporting to pdf file... ");

            JasperViewer.viewReport(print, false);
        } catch (ParserConfigurationException | IOException | SAXException | ParseException | JRException e) {
            throw new RuntimeException(e);
        }
    }

    private static void generateDataSource() throws ParserConfigurationException, SAXException, IOException, ParseException {
        File file = new File("src/main/resources/Holidays.xml");

        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = documentBuilder.parse(file);
        document.getDocumentElement().normalize();

        NodeList holydaysList = document.getElementsByTagName("holydays");
        int holydaysListSize = holydaysList.getLength();

        for (int i = 0; i < holydaysListSize; i++) {
            NodeList holydayNode = holydaysList.item(i).getChildNodes();

            int holydayNodeLength = holydayNode.getLength();
            Node current;

            Map<String, Object> holydayMap = new HashMap<>();

            for (int j = 0; j < holydayNodeLength; j++) {
                current = holydayNode.item(j);
                if (current.getNodeType() == Node.ELEMENT_NODE) {
                    String nodeName = current.getNodeName();
                    String content = current.getTextContent();
                    if (nodeName.equals("DATE")) {
                        Date date = new SimpleDateFormat("dd/MM/yyyy").parse(content);
                        holydayMap.put(nodeName, date);
                    } else {
                        holydayMap.put(nodeName, content);
                    }
                }
            }
            HOLIDAYS_MAP.add(holydayMap);
        }
    }
}
