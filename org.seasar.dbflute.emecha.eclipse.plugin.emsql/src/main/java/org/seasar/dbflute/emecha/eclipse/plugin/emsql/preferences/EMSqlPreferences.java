package org.seasar.dbflute.emecha.eclipse.plugin.emsql.preferences;


public interface EMSqlPreferences {

    String getDefaultSqlDirectory();

    String getSqlDirectory();

    String getSqlDirectory(String packageName);

    void setSqlDirectory(String packageName, String value);

    void setSqlDirectory(String value);

    String getDefaultDatabaseName();

    String getDatabaseName();

    String getDatabaseName(String packageName);

    void setDatabaseName(String value);

    void setDatabaseName(String packageName, String value);

    void save();
}