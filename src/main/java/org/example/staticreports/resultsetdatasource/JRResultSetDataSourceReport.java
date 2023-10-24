package org.example.staticreports.resultsetdatasource;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.generator.ReportGenerator;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.example.utils.ReportUtils.GENERATED_REPORT_PATH;
import static org.example.utils.ReportUtils.HOLIDAYS_REPORT_JRXML;
import static org.example.utils.ReportUtils.PASSWORD;
import static org.example.utils.ReportUtils.URL;
import static org.example.utils.ReportUtils.USER;

public class JRResultSetDataSourceReport implements ReportGenerator {

    private static final Logger LOGGER = LogManager.getLogger(JRResultSetDataSourceReport.class);

    private final String reportName;

    public JRResultSetDataSourceReport(String reportName) {
        this.reportName = reportName;
    }

    @Override
    public void generateReport() {
        try (
                Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
                Statement statement = connection.createStatement()
        ) {
            String sqlQuery = "SELECT * FROM holidays";
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            FileInputStream fileInputStream = new FileInputStream(HOLIDAYS_REPORT_JRXML);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

            LOGGER.info("Compiling report... ");
            JasperReport report = JasperCompileManager.compileReport(bufferedInputStream);
            LOGGER.info("Done compiling... ");

            JRResultSetDataSource dataSource = new JRResultSetDataSource(resultSet);

            LOGGER.info("Filling report... ");
            JasperPrint print = JasperFillManager.fillReport(report, null, dataSource);
            LOGGER.info("Done filling the report... ");

            LOGGER.info("Exporting report to pdf... ");
            JasperExportManager.exportReportToPdfFile(print, GENERATED_REPORT_PATH + reportName);
            LOGGER.info("Done exporting to pdf file... ");

            JasperViewer.viewReport(print, false);
        } catch (SQLException | JRException | FileNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
}
