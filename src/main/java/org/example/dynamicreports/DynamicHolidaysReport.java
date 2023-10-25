package org.example.dynamicreports;

import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.builder.DynamicReports;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.builder.component.Components;
import net.sf.dynamicreports.report.builder.component.CurrentDateBuilder;
import net.sf.dynamicreports.report.builder.component.ImageBuilder;
import net.sf.dynamicreports.report.builder.component.PageXofYBuilder;
import net.sf.dynamicreports.report.builder.component.TextFieldBuilder;
import net.sf.dynamicreports.report.builder.datatype.DataTypes;
import net.sf.dynamicreports.report.builder.style.BorderBuilder;
import net.sf.dynamicreports.report.builder.style.PenBuilder;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.exception.DRException;
import org.example.generator.ReportGenerator;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import static java.awt.Color.BLACK;
import static net.sf.dynamicreports.report.builder.DynamicReports.cmp;
import static net.sf.dynamicreports.report.builder.DynamicReports.col;
import static net.sf.dynamicreports.report.builder.DynamicReports.stl;
import static net.sf.dynamicreports.report.constant.HorizontalTextAlignment.CENTER;
import static net.sf.dynamicreports.report.constant.HorizontalTextAlignment.RIGHT;
import static net.sf.dynamicreports.report.constant.LineStyle.SOLID;
import static net.sf.dynamicreports.report.constant.VerticalTextAlignment.MIDDLE;
import static org.example.utils.ReportUtils.BACKGROUND_COLOR;
import static org.example.utils.ReportUtils.COLUMN_FONT_SIZE;
import static org.example.utils.ReportUtils.COLUMN_HEIGHT;
import static org.example.utils.ReportUtils.COLUMN_TITLE_FONT_SIZE;
import static org.example.utils.ReportUtils.COLUMN_TITLE_HEIGHT;
import static org.example.utils.ReportUtils.DATE_PATTERN;
import static org.example.utils.ReportUtils.FOOTER_FONT_SIZE;
import static org.example.utils.ReportUtils.FOOTER_PADDING;
import static org.example.utils.ReportUtils.GENERATED_REPORT_PATH;
import static org.example.utils.ReportUtils.IMAGE_HEIGHT;
import static org.example.utils.ReportUtils.IMAGE_WIDTH;
import static org.example.utils.ReportUtils.PASSWORD;
import static org.example.utils.ReportUtils.TITLE_COLOR;
import static org.example.utils.ReportUtils.TITLE_FONT_SIZE;
import static org.example.utils.ReportUtils.TITLE_HEIGHT;
import static org.example.utils.ReportUtils.TITLE_LEFT_PADDING;
import static org.example.utils.ReportUtils.TITLE_TOP_PADDING;
import static org.example.utils.ReportUtils.TITLE_WIDTH;
import static org.example.utils.ReportUtils.URL;
import static org.example.utils.ReportUtils.USER;

public class DynamicHolidaysReport implements ReportGenerator {

    private final String reportName;

    public DynamicHolidaysReport(String reportName) {
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

            PenBuilder penBuilder = stl.pen(1f, SOLID).setLineColor(BLACK);
            BorderBuilder borderBuilder = stl.border(penBuilder);
            StyleBuilder tableStyle = stl.style().bold().setBorder(borderBuilder).setHorizontalTextAlignment(CENTER);

            StyleBuilder columnTitleStyle = stl
                    .style()
                    .setBorder(borderBuilder)
                    .bold()
                    .setHorizontalTextAlignment(CENTER)
                    .setVerticalTextAlignment(MIDDLE)
                    .setFontSize(COLUMN_TITLE_FONT_SIZE)
                    .setBackgroundColor(BACKGROUND_COLOR);

            StyleBuilder columnStyle = stl
                    .style()
                    .setBorder(borderBuilder)
                    .boldItalic()
                    .setHorizontalTextAlignment(CENTER)
                    .setVerticalTextAlignment(MIDDLE)
                    .setFontSize(COLUMN_FONT_SIZE)
                    .setForegroundColor(TITLE_COLOR);

            StyleBuilder titleStyle = stl
                    .style()
                    .bold()
                    .setForegroundColor(TITLE_COLOR)
                    .setFontSize(TITLE_FONT_SIZE)
                    .setLeftPadding(TITLE_LEFT_PADDING)
                    .setTopPadding(TITLE_TOP_PADDING);

            StyleBuilder footerStyle = stl
                    .style()
                    .setBackgroundColor(BACKGROUND_COLOR)
                    .setFontSize(FOOTER_FONT_SIZE)
                    .setPadding(FOOTER_PADDING);

            ImageBuilder image = cmp
                    .image("src/main/resources/images/cedacri.png")
                    .setMinDimension(IMAGE_WIDTH, IMAGE_HEIGHT);

            TextColumnBuilder<String> country = col.column("Country", "country", DataTypes.stringType())
                    .setTitleStyle(columnTitleStyle)
                    .setStyle(columnStyle)
                    .setHeight(COLUMN_HEIGHT)
                    .setTitleHeight(COLUMN_TITLE_HEIGHT);

            TextColumnBuilder<Date> date = col.column("Date", "date", DataTypes.dateType())
                    .setTitleStyle(columnTitleStyle)
                    .setStyle(columnStyle)
                    .setPattern(DATE_PATTERN);

            TextColumnBuilder<String> name = col.column("Name", "name", DataTypes.stringType())
                    .setTitleStyle(columnTitleStyle)
                    .setStyle(columnStyle);


            TextFieldBuilder<String> title = Components.text("HOLIDAYS")
                    .setDimension(TITLE_WIDTH, TITLE_HEIGHT)
                    .setStyle(titleStyle);

            PageXofYBuilder pageXofY = cmp.pageXofY().setHorizontalTextAlignment(RIGHT);
            CurrentDateBuilder currentDate = cmp.currentDate().setPattern("EEEEE dd MMMMM");
            TextFieldBuilder<String> page = cmp.text("Page").setHorizontalTextAlignment(RIGHT);

            reportBuilder.columns(
                            country,
                            date,
                            name
                    )
                    .title(cmp.horizontalList().add(title, image))
                    .setDataSource(resultSet)
                    .setColumnStyle(tableStyle)
                    .pageFooter(cmp.horizontalList(currentDate, page, pageXofY))
                    .setPageFooterStyle(footerStyle);

            reportBuilder.toPdf(new FileOutputStream(GENERATED_REPORT_PATH + reportName));
        } catch (SQLException | DRException | FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
