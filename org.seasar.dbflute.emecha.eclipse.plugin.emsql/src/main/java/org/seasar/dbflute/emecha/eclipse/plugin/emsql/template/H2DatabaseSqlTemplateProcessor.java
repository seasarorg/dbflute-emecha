/*
 *
 */
package org.seasar.dbflute.emecha.eclipse.plugin.emsql.template;

import java.util.ArrayList;
import java.util.List;

/**
 * H2 Database のSqlを取得する。
 * @author schatten
 *
 */
public class H2DatabaseSqlTemplateProcessor extends DefaultSqlTemplateProcessor {
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
        list.add(" limit /*$pmb.fetchSize*/20 offset /*$pmb.pageStartIndex*/80");
        list.add("/*END*/");
        return joinList(list, getLineSeparator());
    }

}
