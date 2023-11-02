package org.example;

import org.example.dynamicreports.CrossTabAndChartReport;
import org.example.dynamicreports.DynamicHolidaysReport;
import org.example.generator.ReportGenerator;
import org.example.repository.Repository;
import org.example.repository.RepositoryImpl;
import org.example.staticreports.CommonJRDataSourceReportGenerator;
import org.example.staticreports.beandatasource.JRBeanCollectionReportDataSource;
import org.example.staticreports.mapdatasource.JRMapCollectionReportDataSource;
import org.example.staticreports.resultsetdatasource.JRResultSetReportDataSource;

import java.io.File;
import java.util.Scanner;

import static org.example.utils.ReportUtils.HOLIDAYS_XML;


public class Main {
    public static void main(String[] args) {
        File file = new File(HOLIDAYS_XML);

        Repository repository = new RepositoryImpl();
        repository.prepareDatabase(file);

        while (true) {
            System.out.println("\nGenerate static report from(type any other key to exit): ");
            System.out.println("\nStatic reports:");
            System.out.println("1. Map Collection Data Source;");
            System.out.println("2. Bean Data Source;");
            System.out.println("3. Result Set Data Source");
            System.out.println("\nDynamic reports:");
            System.out.println("4. Dynamic Holidays Report;");
            System.out.println("5. Crosstab with Chart Report;");

            Scanner sc = new Scanner(System.in);
            String reportType = sc.next();

            if (!reportType.matches("[12345]")) {
                break;
            }

            ReportGenerator reportGenerator = switch (reportType) {
                case "1" -> new CommonJRDataSourceReportGenerator(
                        "report1.pdf",
                        JRMapCollectionReportDataSource.getDataSource(file)
                );
                case "2" -> new CommonJRDataSourceReportGenerator(
                        "report2.pdf",
                        JRBeanCollectionReportDataSource.getDataSource(file)
                );
                case "3" -> new CommonJRDataSourceReportGenerator(
                        "report3.pdf",
                        JRResultSetReportDataSource.getDataSource(repository)
                );
                case "4" -> new DynamicHolidaysReport("report4.pdf", repository.getResultSet());
                case "5" -> new CrossTabAndChartReport("report5.pdf", repository.getResultSet());
                default -> throw new IllegalStateException();
            };

            reportGenerator.generateReport();
        }
    }
}