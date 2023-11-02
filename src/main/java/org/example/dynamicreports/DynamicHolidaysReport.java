package org.example.dynamicreports;

import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.builder.DynamicReports;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.builder.component.CurrentDateBuilder;
import net.sf.dynamicreports.report.builder.component.HorizontalListBuilder;
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
import java.sql.ResultSet;
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
import static org.example.utils.ReportUtils.CURRENT_DATE_PATTERN;
import static org.example.utils.ReportUtils.DATE_PATTERN;
import static org.example.utils.ReportUtils.FOOTER_FONT_SIZE;
import static org.example.utils.ReportUtils.FOOTER_PADDING;
import static org.example.utils.ReportUtils.GENERATED_REPORT_PATH;
import static org.example.utils.ReportUtils.IMAGE_HEIGHT;
import static org.example.utils.ReportUtils.IMAGE_WIDTH;
import static org.example.utils.ReportUtils.TITLE_COLOR;
import static org.example.utils.ReportUtils.TITLE_FONT_SIZE;
import static org.example.utils.ReportUtils.TITLE_HEIGHT;
import static org.example.utils.ReportUtils.TITLE_LEFT_PADDING;
import static org.example.utils.ReportUtils.TITLE_TOP_PADDING;
import static org.example.utils.ReportUtils.TITLE_WIDTH;

public class DynamicHolidaysReport implements ReportGenerator {

    private final ResultSet resultSet;

    private final String reportName;

    public DynamicHolidaysReport(String reportName, ResultSet resultSet) {
        this.reportName = reportName;
        this.resultSet = resultSet;
    }

    @Override
    public void generateReport() {
        try {
            JasperReportBuilder reportBuilder = DynamicReports.report();

            reportBuilder.columns(
                            getCountryColumn(),
                            getDateColumn(),
                            getNameColumn()
                    )
                    .title(getTitleComponents())
                    .setDataSource(resultSet)
                    .setColumnStyle(getTableStyle())
                    .pageFooter(getFooterComponents())
                    .setPageFooterStyle(getFooterStyle())
                    .toPdf(new FileOutputStream(GENERATED_REPORT_PATH + reportName));

        } catch (DRException | FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static HorizontalListBuilder getTitleComponents() {
        return cmp
                .horizontalList()
                .add(getTitle(), getImageBuilder());
    }

    private static HorizontalListBuilder getFooterComponents() {
        return cmp
                .horizontalList(
                        getCurrentDate(),
                        getPageTextComponent(),
                        getPageXofY()
                );
    }

    private static TextFieldBuilder<String> getPageTextComponent() {
        return cmp
                .text("Page")
                .setHorizontalTextAlignment(RIGHT);
    }

    private static CurrentDateBuilder getCurrentDate() {
        return cmp
                .currentDate()
                .setPattern(CURRENT_DATE_PATTERN);
    }

    private static PageXofYBuilder getPageXofY() {
        return cmp
                .pageXofY()
                .setHorizontalTextAlignment(RIGHT);
    }

    private static TextFieldBuilder<String> getTitle() {
        return cmp
                .text("HOLIDAYS")
                .setDimension(TITLE_WIDTH, TITLE_HEIGHT)
                .setStyle(getTitleStyle());
    }

    private static TextColumnBuilder<String> getNameColumn() {
        return col
                .column("Name", "name", DataTypes.stringType())
                .setTitleStyle(getColumnTitleStyle())
                .setStyle(getColumnStyle());
    }

    private static TextColumnBuilder<Date> getDateColumn() {
        return col
                .column("Date", "date", DataTypes.dateType())
                .setTitleStyle(getColumnTitleStyle())
                .setStyle(getColumnStyle())
                .setPattern(DATE_PATTERN);
    }

    private static TextColumnBuilder<String> getCountryColumn() {
        return col
                .column("Country", "country", DataTypes.stringType())
                .setTitleStyle(getColumnTitleStyle())
                .setStyle(getColumnStyle())
                .setHeight(COLUMN_HEIGHT)
                .setTitleHeight(COLUMN_TITLE_HEIGHT);
    }

    private static ImageBuilder getImageBuilder() {
        return cmp
                .image("src/main/resources/images/cedacri.png")
                .setMinDimension(IMAGE_WIDTH, IMAGE_HEIGHT);
    }

    private static StyleBuilder getFooterStyle() {
        return stl
                .style()
                .setBackgroundColor(BACKGROUND_COLOR)
                .setFontSize(FOOTER_FONT_SIZE)
                .setPadding(FOOTER_PADDING);
    }

    private static StyleBuilder getTitleStyle() {
        return stl
                .style()
                .bold()
                .setForegroundColor(TITLE_COLOR)
                .setFontSize(TITLE_FONT_SIZE)
                .setLeftPadding(TITLE_LEFT_PADDING)
                .setTopPadding(TITLE_TOP_PADDING);
    }

    private static StyleBuilder getColumnStyle() {
        return stl
                .style()
                .setBorder(getBorderBuilder())
                .boldItalic()
                .setHorizontalTextAlignment(CENTER)
                .setVerticalTextAlignment(MIDDLE)
                .setFontSize(COLUMN_FONT_SIZE)
                .setForegroundColor(TITLE_COLOR);
    }

    private static StyleBuilder getColumnTitleStyle() {
        return stl
                .style()
                .setBorder(getBorderBuilder())
                .bold()
                .setHorizontalTextAlignment(CENTER)
                .setVerticalTextAlignment(MIDDLE)
                .setFontSize(COLUMN_TITLE_FONT_SIZE)
                .setBackgroundColor(BACKGROUND_COLOR);
    }

    private static StyleBuilder getTableStyle() {
        return stl
                .style()
                .bold()
                .setBorder(getBorderBuilder())
                .setHorizontalTextAlignment(CENTER);
    }

    private static BorderBuilder getBorderBuilder() {
        return stl
                .border(getPenBuilder());
    }

    private static PenBuilder getPenBuilder() {
        return stl
                .pen(1f, SOLID)
                .setLineColor(BLACK);
    }
}
