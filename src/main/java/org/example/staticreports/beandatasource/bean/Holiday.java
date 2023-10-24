package org.example.staticreports.beandatasource.bean;

import org.example.staticreports.beandatasource.adapter.DateAdapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;

import static org.example.utils.ReportUtils.IT;
import static org.example.utils.ReportUtils.ITALIA;
import static org.example.utils.ReportUtils.MD;

@XmlRootElement(name = "holydays")
@XmlAccessorType(XmlAccessType.FIELD)
public class Holiday {

    @XmlElement(name = "COUNTRY")
    private String country;

    @XmlElement(name = "DATE")
    @XmlJavaTypeAdapter(DateAdapter.class)
    private Date date;

    @XmlElement(name = "NAME")
    private String name;

    public String getCountry() {
        return country.equalsIgnoreCase(ITALIA)?IT:MD;
    }

    public void setCountry(String country) {
        this.country = country.equalsIgnoreCase(ITALIA)?IT:MD;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
