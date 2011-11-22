/*
 *
 */
package org.seasar.dbflute.emecha.eclipse.plugin.emsql.template;

public interface ISqlTemplateProcessor {

    /**
     * 改行コードを設定する。
     * @param lineSeparator 改行コード
     */
    public abstract void setLineSeparator(String lineSeparator);

    /**
     * Select文の雛型を取得する。
     * @return Select Sql.
     */
    public abstract String getSelectSqlTemplate();

    /**
     * ページングを行うSelect文の雛型を取得する。
     * @return Select Paging Sql.
     */
    public abstract String getSelectPagingSqlTemplate();

    /**
     * Insert文の雛型を取得する。
     * @return Insert Sql.
     */
    public abstract String getInsertSqlTemplate();

    /**
     * Delete文の雛型を取得する。
     * @return Delete Sql.
     */
    public abstract String getDeleteSqlTemplate();

    /**
     * Update文の雛型を取得する。
     * @return Update Sql.
     */
    public abstract String getUpdateSqlTemplate();

}