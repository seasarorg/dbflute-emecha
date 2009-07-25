package org.seasar.dbflute.emecha.eclipse.plugin.wizards.client.listener;

import org.seasar.dbflute.emecha.eclipse.plugin.wizards.client.DBFluteNewClientPage;
import org.seasar.dbflute.emecha.eclipse.plugin.wizards.client.definition.DatabaseInfoDef;

/**
 * @author jflute
 * @since 0.1.0 (2007/08/12 Sunday)
 */
public class DBFluteNewClientDefaultModifyListener extends DBFluteNewClientAbstractModifyListener {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected boolean aidDatabase;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DBFluteNewClientDefaultModifyListener(DBFluteNewClientPage page) {
        super(page);
    }

    // ===================================================================================
    //                                                                            Database
    //                                                                            ========
    @Override
    protected void doTrueBehaviorOfDatabase(String database) {
        if (!aidDatabase) {
            return;
        }
        final DatabaseInfoDef databaseInfo = page.findDatabaseInfo(database);
        final String driverName = databaseInfo.getDriverName();
        page.delegateSettingDatabaseInfoDriverText(driverName);
        final String urlTemplate = databaseInfo.getUrlTemplate();
        page.delegateSettingDatabaseInfoUrlText(urlTemplate);
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public DBFluteNewClientDefaultModifyListener aidDatabase() {
        this.aidDatabase = true;
        return this;
    }
}
