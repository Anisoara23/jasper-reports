package org.example.dynamicreports;

import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.base.expression.AbstractSimpleExpression;
import net.sf.dynamicreports.report.builder.DynamicReports;
import net.sf.dynamicreports.report.builder.FieldBuilder;
import net.sf.dynamicreports.report.builder.chart.AxisFormatBuilder;
import net.sf.dynamicreports.report.builder.chart.BarChartBuilder;
import net.sf.dynamicreports.report.builder.chart.CategoryChartSerieBuilder;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.builder.component.TextFieldBuilder;
import net.sf.dynamicreports.report.builder.crosstab.CrosstabBuilder;
import net.sf.dynamicreports.report.builder.crosstab.CrosstabColumnGroupBuilder;
import net.sf.dynamicreports.report.builder.crosstab.CrosstabMeasureBuilder;
import net.sf.dynamicreports.report.builder.crosstab.CrosstabRowGroupBuilder;
import net.sf.dynamicreports.report.builder.datatype.DataTypes;
import net.sf.dynamicreports.report.builder.style.BorderBuilder;
import net.sf.dynamicreports.report.builder.style.FontBuilder;
import net.sf.dynamicreports.report.builder.style.PenBuilder;
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
import java.sql.ResultSet;
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
import static org.example.utils.ReportUtils.CELL_WIDTH;
import static org.example.utils.ReportUtils.CHART_HEIGHT;
import static org.example.utils.ReportUtils.COMPONENTS_VERTICAL_GAP;
import static org.example.utils.ReportUtils.GENERATED_REPORT_PATH;
import static org.example.utils.ReportUtils.IT;
import static org.example.utils.ReportUtils.ITALIA;
import static org.example.utils.ReportUtils.MOLDAVIA;
import static org.example.utils.ReportUtils.MONTH_NAME_PATTERN;
import static org.example.utils.ReportUtils.MONTH_NUMBER_PATTERN;
import static org.example.utils.ReportUtils.TITLE_COLOR;
import static org.example.utils.ReportUtils.TITLE_FONT_SIZE;
import static org.example.utils.ReportUtils.TITLE_PADDING;

public class CrossTabAndChartReport implements ReportGenerator {

    public static final String CHART_TITLE = "Holidays";
    private final String reportName;

    private final ResultSet resultSet;

    public CrossTabAndChartReport(String reportName, ResultSet resultSet) {
        this.reportName = reportName;
        this.resultSet = resultSet;
    }

    @Override
    public void generateReport() {
        try {
            JasperReportBuilder reportBuilder = DynamicReports.report();

            reportBuilder
                    .title(getTitleComponents())
                    .fields(getDateField())
                    .setDataSource(resultSet)
                    .setPageFormat(PageType.A4, PageOrientation.LANDSCAPE)
                    .summary(getCrossTabComponent(),
                            cmp.verticalGap(COMPONENTS_VERTICAL_GAP),
                            getBarChart()
                    )
                    .sortBy(getMonthNumberColumn());

            reportBuilder.toPdf(new FileOutputStream(GENERATED_REPORT_PATH + reportName));

        } catch (DRException | FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static FieldBuilder<Object> getDateField() {
        return field("date", DataTypes.dateType());
    }

    private static TextFieldBuilder<String> getTitleComponents() {
        return cmp
                .text("Number of holidays per month")
                .setStyle(getTitleStyle());
    }

    private static StyleBuilder getTitleStyle() {
        return stl
                .style()
                .setFontSize(TITLE_FONT_SIZE)
                .bold()
                .setPadding(TITLE_PADDING)
                .setForegroundColor(TITLE_COLOR)
                .setHorizontalTextAlignment(CENTER);
    }

    private static BarChartBuilder getBarChart() {
        return cht
                .barChart()
                .setTitle(CHART_TITLE)
                .setTitleFont(getFontArialBold())
                .setCategory(getMonthNameColumn())
                .setShowValues(true)
                .series(getCategoryChartSerieBuilder())
                .setCategoryAxisFormat(getCategoryAxisFormat())
                .setHeight(CHART_HEIGHT);
    }

    private static AxisFormatBuilder getCategoryAxisFormat() {
        return cht
                .axisFormat()
                .setLabel("Country")
                .setLabelFont(getFontArialBold());
    }

    private static CategoryChartSerieBuilder getCategoryChartSerieBuilder() {
        return cht
                .serie(getHolidaysCount())
                .setSeries(getCountryNameColumn());
    }

    private static FontBuilder getFontArialBold() {
        return stl.fontArialBold();
    }

    private static TextColumnBuilder<Integer> getHolidaysCount() {
        return col
                .column(new HolidayIncrement());
    }

    private static TextColumnBuilder<String> getMonthNameColumn() {
        return col
                .column("Date", new DateColumn(MONTH_NAME_PATTERN))
                .setDataType(DataTypes.stringType());
    }

    private static TextColumnBuilder<String> getCountryNameColumn() {
        return col
                .column("Country", new CountryColumn());
    }

    private static CrosstabBuilder getCrossTabComponent() {
        return ctab
                .crosstab()
                .headerCell(getHeaderTextComponent())
                .addRowGroup(getRowGroupBuilderByCountryCode())
                .addColumnGroup(getColumnGroupBuilderByMonthNumberColumn())
                .measures(getMeasure())
                .setCellWidth(CELL_WIDTH)
                .setStyle(getTableStyle());
    }

    private static CrosstabMeasureBuilder<Object> getMeasure() {
        return ctab
                .measure("name", String.class, Calculation.COUNT);
    }

    private static TextFieldBuilder<String> getHeaderTextComponent() {
        return cmp
                .text("Country / Month");
    }

    private static CrosstabColumnGroupBuilder<String> getColumnGroupBuilderByMonthNumberColumn() {
        return ctab
                .columnGroup(getMonthNumberColumn())
                .setHeaderHorizontalTextAlignment(RIGHT);
    }

    private static TextColumnBuilder<String> getMonthNumberColumn() {
        return col
                .column("Date", new DateColumn(MONTH_NUMBER_PATTERN))
                .setDataType(DataTypes.stringType());
    }

    private static CrosstabRowGroupBuilder<String> getRowGroupBuilderByCountryCode() {
        return ctab
                .rowGroup(getCountryCodeColumn());
    }

    private static TextColumnBuilder<String> getCountryCodeColumn() {
        return col
                .column("Country", "country", DataTypes.stringType());
    }

    private static StyleBuilder getTableStyle() {
        return stl
                .style()
                .bold()
                .setBorder(getBorderBuilder());
    }

    private static BorderBuilder getBorderBuilder() {
        return stl
                .border(getPenBuilder());
    }

    private static PenBuilder getPenBuilder() {
        return stl
                .pen(1f, SOLID)
                .setLineColor(TITLE_COLOR);
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