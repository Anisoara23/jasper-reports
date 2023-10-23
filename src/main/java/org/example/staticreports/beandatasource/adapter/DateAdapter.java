package org.example.staticreports.beandatasource.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.example.utils.ReportUtils.DATE_FORMAT_STRING;

public class DateAdapter extends XmlAdapter<String, Date> {

    @Override
    public Date unmarshal(String str) throws Exception {
        return new SimpleDateFormat(DATE_FORMAT_STRING).parse(str);
    }

    @Override
    public String marshal(Date date) throws Exception {
        return new SimpleDateFormat(DATE_FORMAT_STRING).format(date);
    }
}
