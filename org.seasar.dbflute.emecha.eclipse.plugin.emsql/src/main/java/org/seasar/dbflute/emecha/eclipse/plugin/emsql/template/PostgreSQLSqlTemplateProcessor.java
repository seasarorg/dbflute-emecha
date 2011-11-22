/*
 *
 */
package org.seasar.dbflute.emecha.eclipse.plugin.emsql.template;

import java.util.ArrayList;
import java.util.List;

/**
 * PostgreSQL のSqlを取得する。
 * @author schatten
 *
 */
public class PostgreSQLSqlTemplateProcessor extends DefaultSqlTemplateProcessor {
    /**
     * {@inheritDoc}
     * @see org.seasar.dbflute.emecha.eclipse.plugin.emsql.template.ISqlTemplateProcessor#getSelectPagingSqlTemplate()
     */
    public String getSelectPagingSqlTemplate() {
        List<String> list = new ArrayList<String>();
        list.add("/*IF pmb.isPaging()*/");
        list.add("select ...");
        list.add("-- ELSE select count(*)");
        list.add("/*END*/");
        list.add("  from ...");
        list.add(" where ...");
        list.add("/*IF pmb.isPaging()*/");
        list.add(" order by ...");
        list.add("offset /*$pmb.pageStartIndex*/80 limit /*$pmb.fetchSize*/20");
        list.add("/*END*/");
        return joinList(list, getLineSeparator());
    }

}
