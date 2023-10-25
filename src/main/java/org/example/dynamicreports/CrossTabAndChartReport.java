package org.example.dynamicreports;

import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.base.expression.AbstractSimpleExpression;
import net.sf.dynamicreports.report.builder.DynamicReports;
import net.sf.dynamicreports.report.builder.chart.BarChartBuilder;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.builder.crosstab.CrosstabBuilder;
import net.sf.dynamicreports.report.builder.crosstab.CrosstabColumnGroupBuilder;
import net.sf.dynamicreports.report.builder.crosstab.CrosstabRowGroupBuilder;
import net.sf.dynamicreports.report.builder.datatype.DataTypes;
import net.sf.dynamicreports.report.builder.style.BorderBuilder;
import net.sf.dynamicreports.report.builder.style.PenBuilder;
import net.sf.dynamicreports.report.builder.style.ReportStyleBuilder;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.constant.Calculation;
import net.sf.dynamicreports.report.constant.PageOrientation;
import net.sf.dynamicreports.report.constant.PageType;
import net.sf.dynamicreports.report.definition.ReportParameters;
import net.sf.dynamicreports.report.exception.DRException;
import org.example.generator.ReportGenerator;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Serial;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import static net.sf.dynamicreports.report.builder.DynamicReports.cht;
import static net.sf.dynamicreports.report.builder.DynamicReports.cmp;
import static net.sf.dynamicreports.report.builder.DynamicReports.col;
import static net.sf.dynamicreports.report.builder.DynamicReports.ctab;
import static net.sf.dynamicreports.report.builder.DynamicReports.field;
import static net.sf.dynamicreports.report.builder.DynamicReports.stl;
import static net.sf.dynamicreports.report.constant.HorizontalTextAlignment.CENTER;
import static net.sf.dynamicreports.report.constant.HorizontalTextAlignment.RIGHT;
import static net.sf.dynamicreports.report.constant.LineStyle.SOLID;
import static org.example.utils.ReportUtils.CHART_HEIGHT;
import static org.example.utils.ReportUtils.COMPONENTS_VERTICAL_GAP;
import static org.example.utils.ReportUtils.GENERATED_REPORT_PATH;
import static org.example.utils.ReportUtils.IT;
import static org.example.utils.ReportUtils.ITALIA;
import static org.example.utils.ReportUtils.MOLDAVIA;
import static org.example.utils.ReportUtils.MONTH_NAME_PATTERN;
import static org.example.utils.ReportUtils.MONTH_NUMBER_PATTERN;
import static org.example.utils.ReportUtils.PASSWORD;
import static org.example.utils.ReportUtils.TITLE_COLOR;
import static org.example.utils.ReportUtils.TITLE_FONT_SIZE;
import static org.example.utils.ReportUtils.TITLE_PADDING;
import static org.example.utils.ReportUtils.URL;
import static org.example.utils.ReportUtils.USER;

public class CrossTabAndChartReport implements ReportGenerator {

    private final String reportName;

    public CrossTabAndChartReport(String reportName) {
        this.reportName = reportName;
    }

    @Override
    public void generateReport() {
        try (
                Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
                Statement statement = connection.createStatement()
        ) {
            String sqlQuery = "SELECT country, date, name FROM holidays";
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            JasperReportBuilder reportBuilder = DynamicReports.report();

            PenBuilder penBuilder = stl.pen(1f, SOLID).setLineColor(TITLE_COLOR);
            BorderBuilder borderBuilder = stl.border(penBuilder);
            StyleBuilder tableStyle = stl
                    .style()
                    .bold()
                    .setBorder(borderBuilder);

            TextColumnBuilder<String> countryCode = col
                    .column("Country", "country", DataTypes.stringType());
            CrosstabRowGroupBuilder<String> rowGroupBuilder = ctab
                    .rowGroup(countryCode);

            TextColumnBuilder<String> monthNumber = col
                    .column("Date", new DateColumn(MONTH_NUMBER_PATTERN))
                    .setDataType(DataTypes.stringType());
            CrosstabColumnGroupBuilder<String> columnGroupBuilder = ctab
                    .columnGroup(monthNumber)
                    .setHeaderHorizontalTextAlignment(RIGHT);

            CrosstabBuilder crosstabBuilder = ctab
                    .crosstab()
                    .headerCell(cmp.text("Country / Month"))
                    .addRowGroup(rowGroupBuilder)
                    .addColumnGroup(columnGroupBuilder)
                    .measures(
                            ctab.measure("name", String.class, Calculation.COUNT)
                    )
                    .setCellWidth(30)
                    .setStyle(tableStyle);

            TextColumnBuilder<String> countryName = col
                    .column("Country", new CountryColumn());

            TextColumnBuilder<String> monthName = col
                    .column("Date", new DateColumn(MONTH_NAME_PATTERN))
                    .setDataType(DataTypes.stringType());

            TextColumnBuilder<Integer> holidaysCount = col
                    .column(new HolidayIncrement());

            BarChartBuilder barChart = cht.barChart()
                    .setTitle("Holidays")
                    .setTitleFont(stl.fontArialBold())
                    .setCategory(monthName)
                    .setShowValues(true)
                    .series(
                            cht.serie(holidaysCount).setSeries(countryName)
                    )
                    .setCategoryAxisFormat(
                            cht.axisFormat().setLabel("Country").setLabelFont(stl.fontArialBold())
                    )
                    .setHeight(CHART_HEIGHT);


            ReportStyleBuilder titleStyle = stl
                    .style()
                    .setFontSize(TITLE_FONT_SIZE)
                    .bold()
                    .setPadding(TITLE_PADDING)
                    .setForegroundColor(TITLE_COLOR)
                    .setHorizontalTextAlignment(CENTER);

            reportBuilder
                    .title(cmp.text("Number of holidays per month").setStyle(titleStyle))
                    .fields(field("date", DataTypes.dateType()))
                    .setDataSource(resultSet)
                    .setPageFormat(PageType.A4, PageOrientation.LANDSCAPE)
                    .summary(crosstabBuilder,
                            cmp.verticalGap(COMPONENTS_VERTICAL_GAP),
                            barChart
                    )
                    .sortBy(monthNumber);

            reportBuilder.toPdf(new FileOutputStream(GENERATED_REPORT_PATH + reportName));
        } catch (SQLException | DRException | FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static class DateColumn extends AbstractSimpleExpression<String> {

        @Serial
        private static final long serialVersionUID = 1L;

        private final String pattern;

        public DateColumn(String pattern) {
            this.pattern = pattern;
        }

        @Override
        public String evaluate(ReportParameters reportParameters) {
            Date value = reportParameters.getValue("date");
            SimpleDateFormat formatter = new SimpleDateFormat(pattern);
            return formatter.format(value);
        }
    }

    private static class CountryColumn extends AbstractSimpleExpression<String> {

        @Serial
        private static final long serialVersionUID = 1L;

        @Override
        public String evaluate(ReportParameters reportParameters) {
            String country = reportParameters.getValue("country");
            if (country.equalsIgnoreCase(IT)) {
                return ITALIA;
            } else {
                return MOLDAVIA;
            }
        }
    }

    private static class HolidayIncrement extends AbstractSimpleExpression<Integer> {

        @Override
        public Integer evaluate(ReportParameters reportParameters) {
            return 1;
        }
    }
}