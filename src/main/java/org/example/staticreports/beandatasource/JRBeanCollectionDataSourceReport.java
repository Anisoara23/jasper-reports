package org.example.staticreports.beandatasource;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.staticreports.beandatasource.bean.Holiday;
import org.example.staticreports.beandatasource.bean.Holidays;
import org.example.staticreports.generator.ReportGenerator;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import static org.example.utils.ReportUtils.GENERATED_REPORT_PATH;
import static org.example.utils.ReportUtils.HOLIDAYS_REPORT_BEAN_JRXML;
import static org.example.utils.ReportUtils.HOLIDAYS_XML;

public class JRBeanCollectionDataSourceReport implements ReportGenerator {

    private static final Logger LOGGER = LogManager.getLogger(JRBeanCollectionDataSourceReport.class);

    private final String reportName;

    public JRBeanCollectionDataSourceReport(String reportName) {
        this.reportName = reportName;
    }

    public void generateReport() {
        try {
            FileInputStream fileInputStream = new FileInputStream(HOLIDAYS_REPORT_BEAN_JRXML);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

            JasperReport report = JasperCompileManager.compileReport(bufferedInputStream);
            LOGGER.info("Done compiling... ");

            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(getDataSource(), false);

            JasperPrint print = JasperFillManager.fillReport(report, null, dataSource);
            LOGGER.info("Done filling the report... ");

            JasperExportManager.exportReportToPdfFile(print, GENERATED_REPORT_PATH + reportName);
            LOGGER.info("Done exporting to pdf file... ");

            JasperViewer.viewReport(print, false);

        } catch (JAXBException | JRException | FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<Holiday> getDataSource() throws JAXBException {
        File file = new File(HOLIDAYS_XML);
        JAXBContext jaxbContext = JAXBContext.newInstance(Holidays.class);

        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        Holidays holidays = (Holidays) unmarshaller.unmarshal(file);
        return holidays.getHolidays();
    }
}
