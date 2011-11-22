/*
 *
 */
package org.seasar.dbflute.emecha.eclipse.plugin.emsql.template;

import java.util.ArrayList;
import java.util.List;

/**
 * ApacheDerby のSqlを取得する。
 * @author schatten
 */
public class ApacheDerbySqlTemplateProcessor extends DefaultSqlTemplateProcessor {
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
        list.add("offset /*$pmb.pageStartIndex*/80 rows fetch first /*$pmb.fetchSize*/20 rows only");
        list.add("/*END*/");
        return joinList(list, getLineSeparator());
    }

}
