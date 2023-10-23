package org.example.staticreports.beandatasource.bean;

import org.example.staticreports.beandatasource.adapter.DateAdapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;

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
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
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
