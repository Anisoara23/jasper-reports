package org.example.staticreports.beandatasource;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.example.staticreports.beandatasource.bean.Holidays;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;

public class JRBeanCollectionReportDataSource {

    public static JRDataSource getDataSource(File file) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Holidays.class);

            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            Holidays holidays = (Holidays) unmarshaller.unmarshal(file);

            return new JRBeanCollectionDataSource(holidays.getHolidays());
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }
}
