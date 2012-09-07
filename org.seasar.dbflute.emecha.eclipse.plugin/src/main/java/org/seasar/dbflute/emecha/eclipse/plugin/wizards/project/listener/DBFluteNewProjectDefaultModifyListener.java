/**
 *
 */
package org.seasar.dbflute.emecha.eclipse.plugin.wizards.project.listener;

import org.seasar.dbflute.emecha.eclipse.plugin.wizards.client.DBFluteNewClientPage;
import org.seasar.dbflute.emecha.eclipse.plugin.wizards.client.listener.DBFluteNewClientDefaultModifyListener;

/**
 *
 */
public class DBFluteNewProjectDefaultModifyListener extends DBFluteNewClientDefaultModifyListener {

    public DBFluteNewProjectDefaultModifyListener(DBFluteNewClientPage page) {
        super(page);
    }

    @Override
    protected boolean checkOutputDirectory() {
        return page.getOutputDirectory() != null;
    }
}
