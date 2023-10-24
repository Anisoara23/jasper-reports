package org.example;

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

        ReportGenerator reportGenerator = null;
        while (true) {
            System.out.println("Generate static report from(type any other key to exit): ");
            System.out.println("1. Map Collection Data Source;");
            System.out.println("2. Bean Data Source;");
            System.out.println("3. Result Set Data Source");

            Scanner sc = new Scanner(System.in);
            String reportType = sc.next();

            if (!reportType.matches("[123]")) {
                break;
            }
            switch (reportType) {
                case "1" -> reportGenerator = new JRMapCollectionDataSourceReport("report1.pdf");
                case "2" -> reportGenerator = new JRBeanCollectionDataSourceReport("report2.pdf");
                case "3" -> reportGenerator = new JRResultSetDataSourceReport("report3.pdf");
            }
            reportGenerator.generateReport();
        }

    }
}