package org.example.staticreports;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.generator.ReportGenerator;
import org.example.staticreports.mapdatasource.JRMapCollectionReportDataSource;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import static org.example.utils.ReportUtils.GENERATED_REPORT_PATH;
import static org.example.utils.ReportUtils.HOLIDAYS_REPORT_JRXML;

public class CommonJRDataSourceReportGenerator implements ReportGenerator {

    private static final Logger logger = LogManager.getLogger(JRMapCollectionReportDataSource.class);

    private final String reportName;

    private final JRDataSource jrDataSource;

    public CommonJRDataSourceReportGenerator(String reportName, JRDataSource jrDataSource) {
        this.reportName = reportName;
        this.jrDataSource = jrDataSource;
    }

    @Override
    public void generateReport() {
        try {
            FileInputStream fileInputStream = new FileInputStream(HOLIDAYS_REPORT_JRXML);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

            logger.info("Compiling report... ");
            JasperReport report = JasperCompileManager.compileReport(bufferedInputStream);
            logger.info("Done compiling... ");

            logger.info("Filling report... ");
            JasperPrint print = JasperFillManager.fillReport(report, null, jrDataSource);
            logger.info("Done filling the report... ");

            logger.info("Exporting report to pdf... ");
            JasperExportManager.exportReportToPdfFile(print, GENERATED_REPORT_PATH + reportName);
            logger.info("Done exporting to pdf file... ");

            JasperViewer.viewReport(print, false);
        } catch (IOException | JRException e) {
            throw new RuntimeException(e);
        }
    }
}
