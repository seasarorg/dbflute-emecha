package org.seasar.dbflute.emecha.eclipse.plugin.wizards.client.listener;

import org.eclipse.core.resources.IResource;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.seasar.dbflute.emecha.eclipse.plugin.core.exception.EmPluginException;
import org.seasar.dbflute.emecha.eclipse.plugin.core.wrapper.resource.EmContainer;
import org.seasar.dbflute.emecha.eclipse.plugin.core.wrapper.resource.EmWorkspaceRoot;
import org.seasar.dbflute.emecha.eclipse.plugin.wizards.client.DBFluteNewClientPage;

/**
 * @author jflute
 * @since 0.1.0 (2007/08/12 Sunday)
 */
public abstract class DBFluteNewClientAbstractModifyListener implements ModifyListener {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected DBFluteNewClientPage page;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DBFluteNewClientAbstractModifyListener(DBFluteNewClientPage page) {
        this.page = page;
    }

    // ===================================================================================
    //                                                                      Implementation
    //                                                                      ==============
    public void modifyText(ModifyEvent e) {
        handle();
    }

    public void handle() {
        try {
            if (!checkOutputDirectory()) {
                return;
            }
            if (!checkProject()) {
                return;
            }
            if (!checkDatabase()) {
                return;
            }
            if (!checkPackageBase()) {
                return;
            }
            if (!checkDatabaseInfoDriver()) {
                return;
            }
            if (!checkDatabaseInfoUrl()) {
                return;
            }
            if (!checkDatabaseInfoUser()) {
                return;
            }
            if (!checkVersionInfoDBFlute()) {
                return;
            }
        } finally {
            final String database = page.getDatabase();
            if (database != null && database.trim().length() > 0) {
                doTrueBehaviorOfDatabase(database);
            }
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
    //                                                                             Project
    //                                                                             =======
    protected boolean checkProject() {
        final String project = page.getProject();
        return checkRequired("Client Project", project);
    }

    // ===================================================================================
    //                                                                            Database
    //                                                                            ========
    protected boolean checkDatabase() {
        final String database = page.getDatabase();
        if (!checkRequired("Database", database)) {
            page.delegateSettingDatabaseColor(60, 30, 30);
            return false;
        }
        return true;
    }

    abstract protected void doTrueBehaviorOfDatabase(String database);

    // ===================================================================================
    //                                                                        Package Base
    //                                                                        ============
    protected boolean checkPackageBase() {
        final String packageBase = page.getPackageBase();
        return checkRequired("Package Base", packageBase);
    }

    // ===================================================================================
    //                                                                Database Info Driver
    //                                                                ====================
    protected boolean checkDatabaseInfoDriver() {
        final String databaseInfoDriver = page.getDatabaseInfoDriver();
        return checkRequired("Driver", databaseInfoDriver);
    }

    protected boolean checkDatabaseInfoUrl() {
        final String databaseInfoUrl = page.getDatabaseInfoUrl();
        return checkRequired("Url", databaseInfoUrl);
    }

    protected boolean checkDatabaseInfoUser() {
        final String databaseInfoUser = page.getDatabaseInfoUser();
        return checkRequired("User", databaseInfoUser);
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
