/*
 *
 */
package org.seasar.dbflute.emecha.eclipse.plugin.emsql;

import java.util.HashMap;
import java.util.Map;

/**
 * サポートしているデータベースの列挙
 * @author schatten
 */
public enum SupportDatabase {
    //-- Main Support
    MySQL("mysql"),
    PostgreSQL("postgresql"),
    Oracle("oracle"),
    DB2("db2"),
    SQLServer("mssql"),
    H2Database("h2"),
    ApacheDerby("derby"),
    //-- sub supported
    SQLite("sqlite"),
    MSAccess("msaccess"),
    //-- a-little-bit supported
    Sybase("sybase"),
    ;
    private final String dbName;
    private static final Map<String, SupportDatabase> databaseMap;
    static {
        databaseMap = new HashMap<String, SupportDatabase>();
        for (SupportDatabase database : SupportDatabase.values()) {
            databaseMap.put(database.getDbName().toLowerCase(), database);
        }
    }
    private SupportDatabase(String dbName) {
        this.dbName = dbName;
    }
    public String getDbName() {
        return this.dbName;
    }
    public static SupportDatabase nameOf(String name) {
        if (name == null) {
            return null;
        }
        return databaseMap.get(name.toLowerCase());
    }
}
