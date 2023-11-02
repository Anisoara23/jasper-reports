package org.example.staticreports.resultsetdatasource;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import org.example.repository.Repository;

public class JRResultSetReportDataSource {

    public static JRDataSource getDataSource(Repository repository) {
        return new JRResultSetDataSource(repository.getResultSet());
    }
}
