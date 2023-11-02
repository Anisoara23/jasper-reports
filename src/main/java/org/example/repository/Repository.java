package org.example.repository;

import java.io.File;
import java.sql.ResultSet;

public interface Repository {

    void prepareDatabase(File file);

    ResultSet getResultSet();
}
