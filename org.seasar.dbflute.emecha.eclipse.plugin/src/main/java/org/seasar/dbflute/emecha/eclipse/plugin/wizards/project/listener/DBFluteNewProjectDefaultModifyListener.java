/**
 *
 */
package org.seasar.dbflute.emecha.eclipse.plugin.wizards.project.listener;

import org.seasar.dbflute.emecha.eclipse.plugin.wizards.client.listener.DBFluteNewClientDefaultModifyListener;
import org.seasar.dbflute.emecha.eclipse.plugin.wizards.project.DBFluteNewClientProjectPage;

/**
 *
 */
public class DBFluteNewProjectDefaultModifyListener extends DBFluteNewClientDefaultModifyListener {

    public DBFluteNewProjectDefaultModifyListener(DBFluteNewClientProjectPage page) {
        super(page);
    }

    @Override
    protected boolean checkOutputDirectory() {
        return true;
    }
}
