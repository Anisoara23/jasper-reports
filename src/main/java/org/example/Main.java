package org.example;

import org.example.dynamicreports.CrossTabAndChartReport;
import org.example.dynamicreports.DynamicHolidaysReport;
import org.example.repository.Repository;
import org.example.staticreports.beandatasource.JRBeanCollectionDataSourceReport;
import org.example.generator.ReportGenerator;
import org.example.staticreports.mapdatasource.JRMapCollectionDataSourceReport;
import org.example.staticreports.resultsetdatasource.JRResultSetDataSourceReport;

import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        Repository repository = new Repository();
        repository.prepareDatabase();

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
                case "1" -> new JRMapCollectionDataSourceReport("report1.pdf");
                case "2" -> new JRBeanCollectionDataSourceReport("report2.pdf");
                case "3" -> new JRResultSetDataSourceReport("report3.pdf");
                case "4" -> new DynamicHolidaysReport("report4.pdf");
                case "5" -> new CrossTabAndChartReport("report5.pdf");
                default -> throw new IllegalStateException();
            };

            reportGenerator.generateReport();
        }
    }
}