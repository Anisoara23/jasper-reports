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
import org.example.generator.ReportGenerator;
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

import static org.example.utils.ReportUtils.DATE_FORMAT_STRING;
import static org.example.utils.ReportUtils.GENERATED_REPORT_PATH;
import static org.example.utils.ReportUtils.HOLIDAYS_REPORT_JRXML;
import static org.example.utils.ReportUtils.HOLIDAYS_XML;

public class JRMapCollectionDataSourceReport implements ReportGenerator {

    private static final Logger LOGGER = LogManager.getLogger(JRMapCollectionDataSourceReport.class);

    private static final List<Map<String, ?>> HOLIDAYS_MAP = new ArrayList<>();

    private final String reportName;

    public JRMapCollectionDataSourceReport(String reportName) {
        this.reportName = reportName;
    }

    public void generateReport() {
        try {
            FileInputStream fileInputStream = new FileInputStream(HOLIDAYS_REPORT_JRXML);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

            LOGGER.info("Compiling report... ");
            JasperReport report = JasperCompileManager.compileReport(bufferedInputStream);
            LOGGER.info("Done compiling... ");

            generateDataSource();

            JRMapCollectionDataSource dataSource = new JRMapCollectionDataSource(HOLIDAYS_MAP);

            LOGGER.info("Filling report... ");
            JasperPrint print = JasperFillManager.fillReport(report, null, dataSource);
            LOGGER.info("Done filling the report... ");

            LOGGER.info("Exporting report to pdf... ");
            JasperExportManager.exportReportToPdfFile(print, GENERATED_REPORT_PATH + reportName);
            LOGGER.info("Done exporting to pdf file... ");

            JasperViewer.viewReport(print, false);
        } catch (ParserConfigurationException | IOException | SAXException | ParseException | JRException e) {
            throw new RuntimeException(e);
        }
    }

    private static void generateDataSource() throws ParserConfigurationException, SAXException, IOException, ParseException {
        File file = new File(HOLIDAYS_XML);

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
                    String nodeName = current.getNodeName().toLowerCase();
                    String content = current.getTextContent();
                    if (nodeName.equals("date")) {
                        Date date = new SimpleDateFormat(DATE_FORMAT_STRING).parse(content);
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
