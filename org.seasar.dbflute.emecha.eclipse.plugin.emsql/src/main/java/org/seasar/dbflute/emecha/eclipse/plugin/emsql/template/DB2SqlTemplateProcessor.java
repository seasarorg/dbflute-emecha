/*
 *
 */
package org.seasar.dbflute.emecha.eclipse.plugin.emsql.template;

import java.util.ArrayList;
import java.util.List;

/**
 * DB2 のSqlを取得する。
 * @author
 */
public class DB2SqlTemplateProcessor extends DefaultSqlTemplateProcessor {
    /**
     * {@inheritDoc}
     * @see org.seasar.dbflute.emecha.eclipse.plugin.emsql.template.ISqlTemplateProcessor#getSelectPagingSqlTemplate()
     */
    public String getSelectPagingSqlTemplate() {
        List<String> list = new ArrayList<String>();
        list.add("/*IF pmb.isPaging()*/");
        list.add("select *");
        list.add("  from (");
        list.add("plain.*, row_number() over() as rn");
        list.add("  from (");
        list.add("select ...");
        list.add("-- ELSE select count(*)");
        list.add("/*END*/");
        list.add("  from ...");
        list.add(" where ...");
        list.add("/*IF pmb.isPaging()*/");
        list.add(" order by ...");
        list.add("       ) plain");
        list.add("       ) ext");
        list.add(" where ext.rn > /*pmb.pageStartIndex*/80");
        list.add("   and ext.rn <= /*pmb.pageEndIndex*/100");
        list.add("/*END*/");
        return joinList(list, getLineSeparator());
    }

}
