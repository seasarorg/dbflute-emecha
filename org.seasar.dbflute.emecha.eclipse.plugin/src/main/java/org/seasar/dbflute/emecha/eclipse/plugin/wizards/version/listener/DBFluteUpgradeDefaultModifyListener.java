package org.seasar.dbflute.emecha.eclipse.plugin.wizards.version.listener;

import org.eclipse.core.resources.IResource;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.seasar.dbflute.emecha.eclipse.plugin.core.exception.EmPluginException;
import org.seasar.dbflute.emecha.eclipse.plugin.core.wrapper.resource.EmContainer;
import org.seasar.dbflute.emecha.eclipse.plugin.core.wrapper.resource.EmWorkspaceRoot;
import org.seasar.dbflute.emecha.eclipse.plugin.wizards.version.DBFluteUpgradePage;

/**
 * @author jflute
 * @since 0.2.3 (2009/01/31 Saturday)
 */
public class DBFluteUpgradeDefaultModifyListener implements ModifyListener {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected DBFluteUpgradePage page;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DBFluteUpgradeDefaultModifyListener(DBFluteUpgradePage page) {
        this.page = page;
    }

    // ===================================================================================
    //                                                                      Implementation
    //                                                                      ==============
    public void modifyText(ModifyEvent e) {
        handle();
    }

    public void handle() {
        if (!checkOutputDirectory()) {
            return;
        }
        if (!checkVersionInfoDBFlute()) {
            return;
        }
        updateStatus(null);
    }

    // ===================================================================================
    //                                                                    Output Directory
    //                                                                    ================
    protected boolean checkOutputDirectory() {
        final String outputDirectoryName = page.getOutputDirectory();
        if (outputDirectoryName.trim().length() == 0) {
            updateStatus("Eclipse Project should be specified");
            return false;
        }
        final EmContainer container;
        try {
            final EmWorkspaceRoot workspaceRoot = createEmWorkspaceRoot();
            container = workspaceRoot.findContainer(outputDirectoryName);
        } catch (EmPluginException e) {
            updateStatus(e.getMessage());
            return false;
        }
        if (container == null || (container.getType() & (IResource.PROJECT | IResource.FOLDER)) == 0) {
            updateStatus("Eclipse Project should exist: " + outputDirectoryName);
            return false;
        }
        if (!container.isAccessible()) {
            updateStatus("Eclipse Project should be writable: " + outputDirectoryName);
            return false;
        }
        return true;
    }

    // ===================================================================================
    //                                                                        Version Info
    //                                                                        ============
    protected boolean checkVersionInfoDBFlute() {
        final String versionInfoDBFlute = page.getVersionInfoDBFlute();
        return checkRequired("DBFlute Version", versionInfoDBFlute);
    }

    // ===================================================================================
    //                                                                       Common Helper
    //                                                                       =============
    protected boolean checkRequired(String name, String value) {
        if (value == null || value.trim().length() == 0) {
            updateStatus(name + " should be specified");
            return false;
        }
        return true;
    }

    protected void updateStatus(String message) {
        page.setErrorMessage(message);
        page.setPageComplete(message == null);
    }

    protected EmWorkspaceRoot createEmWorkspaceRoot() {
        return EmWorkspaceRoot.create();
    }
}
