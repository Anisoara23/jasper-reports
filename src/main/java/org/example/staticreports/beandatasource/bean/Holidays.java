package org.example.staticreports.beandatasource.bean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "Year-2021")
@XmlAccessorType(XmlAccessType.FIELD)
public class Holidays {

    @XmlElement(name = "holydays")
    private List<Holiday> holidays = new ArrayList<>();

    public List<Holiday> getHolidays() {
        return holidays;
    }

    public void setHolidays(List<Holiday> holidays) {
        this.holidays = holidays;
    }
}
