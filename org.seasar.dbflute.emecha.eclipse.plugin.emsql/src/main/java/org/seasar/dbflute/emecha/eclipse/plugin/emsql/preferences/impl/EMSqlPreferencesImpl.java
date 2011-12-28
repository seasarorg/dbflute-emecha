/*
 *
 */
package org.seasar.dbflute.emecha.eclipse.plugin.emsql.preferences.impl;

import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.seasar.dbflute.emecha.eclipse.plugin.core.util.LogUtil;
import org.seasar.dbflute.emecha.eclipse.plugin.emsql.EMSqlPlugin;
import org.seasar.dbflute.emecha.eclipse.plugin.emsql.preferences.EMSqlPreferenceStore;
import org.seasar.dbflute.emecha.eclipse.plugin.emsql.preferences.EMSqlPreferences;

/**
 * @author schatten
 *
 */
public class EMSqlPreferencesImpl implements EMSqlPreferences {

    private static final String DATABASE_TYPE_KEY = "database"; //$NON-NLS-1$
    private static final String OUTPUT_DIRECTORY_KEY = "sqlDirectory"; //$NON-NLS-1$
    private static final String DEFAULT_SQL_OUTPUT_DIR = "/src/main/resources"; //$NON-NLS-1$

    private final EMSqlPreferenceStore preferenceStore;
    private final IProject project;

    public EMSqlPreferencesImpl(IProject project, String qualifier) {
        this.preferenceStore = new EMSqlPreferenceStore(project, qualifier);
        this.project = project;
    }

    public String getDefaultSqlDirectory() {
        return getProjectName() + DEFAULT_SQL_OUTPUT_DIR;
    }

    public String getProjectName() {
        return project.getProject().getName();
    }

    /**
     * {@inheritDoc}
     * @see org.seasar.dbflute.emecha.eclipse.plugin.emsql.preferences.EMSqlPreferences#getSqlDirectory()
     */
    public String getSqlDirectory() {
        String outputDir = preferenceStore.getString(OUTPUT_DIRECTORY_KEY);
        if (outputDir != null && !"".equals(outputDir.trim())) {
            return outputDir;
        }
        return getDefaultSqlDirectory();
    }

    /**
     * {@inheritDoc}
     * @see org.seasar.dbflute.emecha.eclipse.plugin.emsql.preferences.EMSqlPreferences#getSqlDirectory(java.lang.String)
     */
    public String getSqlDirectory(String packageName) {
        String outputDir = preferenceStore.getRecursivePackagePreference(OUTPUT_DIRECTORY_KEY, packageName);
        if (outputDir != null) {
            return outputDir;
        }
        return getSqlDirectory();
    }

    /**
     * {@inheritDoc}
     * @see org.seasar.dbflute.emecha.eclipse.plugin.emsql.preferences.EMSqlPreferences#setSqlDirectory(java.lang.String, java.lang.String)
     */
    public void setSqlDirectory(String packageName, String value) {
        preferenceStore.setPreferenceValue(OUTPUT_DIRECTORY_KEY, packageName, value);
    }

    /**
     * {@inheritDoc}
     * @see org.seasar.dbflute.emecha.eclipse.plugin.emsql.preferences.EMSqlPreferences#setSqlDirectory(java.lang.String)
     */
    public void setSqlDirectory(String value) {
        preferenceStore.setPreferenceValue(OUTPUT_DIRECTORY_KEY, null, value);
    }

    /**
     * {@inheritDoc}
     * @see org.seasar.dbflute.emecha.eclipse.plugin.emsql.preferences.EMSqlPreferences#getDatabaseName()
     */
    public String getDatabaseName() {
        return preferenceStore.getString(DATABASE_TYPE_KEY);
    }

    /**
     * {@inheritDoc}
     * @see org.seasar.dbflute.emecha.eclipse.plugin.emsql.preferences.EMSqlPreferences#getDatabaseName(java.lang.String)
     */
    public String getDatabaseName(String packageName) {
        String databaseName = preferenceStore.getRecursivePackagePreference(DATABASE_TYPE_KEY, packageName);
        if (databaseName != null) {
            return databaseName;
        }
        return getDatabaseName();
    }

    public String getDefaultDatabaseName() {
        return "";
    }

    /**
     * {@inheritDoc}
     * @see org.seasar.dbflute.emecha.eclipse.plugin.emsql.preferences.EMSqlPreferences#setDatabaseName(java.lang.String)
     */
    public void setDatabaseName(String value) {
        preferenceStore.setPreferenceValue(DATABASE_TYPE_KEY, null, value);
    }

    /**
     * {@inheritDoc}
     * @see org.seasar.dbflute.emecha.eclipse.plugin.emsql.preferences.EMSqlPreferences#setDatabaseName(java.lang.String, java.lang.String)
     */
    public void setDatabaseName(String packageName, String value) {
        preferenceStore.setPreferenceValue(DATABASE_TYPE_KEY, packageName, value);
    }

    /**
     * {@inheritDoc}
     * @see org.seasar.dbflute.emecha.eclipse.plugin.emsql.preferences.EMSqlPreferences#save()
     */
    public void save() {
        try {
            preferenceStore.save();
        } catch (IOException e) {
            LogUtil.log(EMSqlPlugin.getDefault(), e);
        }
    }

}
