/*
 *
 */
package org.seasar.dbflute.emecha.eclipse.plugin.emsql.preferences;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * EMSpql 設定情報アクセス
 * @author schatten
 */
public class EMSqlPreferenceStore extends ScopedPreferenceStore {

    private static final String DATABASE_TYPE_KEY = "database";
    private static final String OUTPUT_DIRECTORY_KEY = "sqlDirectory";

    private static final String DEFAULT_SQL_OUTPUT_DIR = "/src/main/resources";

    private final IProject project;
    /**
     * @param context
     * @param qualifier
     */
    public EMSqlPreferenceStore(IProject project, String qualifier) {
        super(new ProjectScope(project), qualifier);
        this.project = project;
    }

    public String getSqlDirectory() {
        String outputDir = getString(OUTPUT_DIRECTORY_KEY);
        if (outputDir != null && !"".equals(outputDir.trim())) {
            return outputDir;
        }
        return project.getProject().getName() + DEFAULT_SQL_OUTPUT_DIR;
    }

    public String getSqlDirectory(IJavaElement javaElement) {
        // TODO package 単位で設定を切り替える対応
        return getSqlDirectory();
    }
    public String getDatabaseName() {
        return this.getString(DATABASE_TYPE_KEY);
    }
    public String getDatabaseName(IJavaElement javaElement) {
        // TODO package 単位で設定を切り替える対応
        return getDatabaseName();
    }
}
