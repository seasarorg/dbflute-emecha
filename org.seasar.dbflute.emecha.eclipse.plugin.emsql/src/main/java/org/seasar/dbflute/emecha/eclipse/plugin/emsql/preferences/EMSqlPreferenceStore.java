/*
 *
 */
package org.seasar.dbflute.emecha.eclipse.plugin.emsql.preferences;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.service.prefs.Preferences;

/**
 * EMSpql 設定情報アクセス
 * @author schatten
 */
public class EMSqlPreferenceStore extends ScopedPreferenceStore {

    private IScopeContext _localStoreContext;
    private String _localNodeQualifier;
     /**
     * @param context
     * @param qualifier
     */
    public EMSqlPreferenceStore(IProject project, String qualifier) {
        super(new ProjectScope(project), qualifier);
        _localStoreContext = new ProjectScope(project);
        _localNodeQualifier = qualifier;
    }
    public Preferences getPreferenceChildNode(String keyName) {
// For befor 3.4
//        IEclipsePreferences[] preferenceNodes = this.getPreferenceNodes(false);
//        IEclipsePreferences projectPreferences = preferenceNodes[0];
        IEclipsePreferences projectPreferences = _localStoreContext.getNode(_localNodeQualifier);
        return projectPreferences.node(keyName);
    }

    public String getRecursivePackagePreference(String keyName, String packageName) {
        Preferences node = getPreferenceChildNode(keyName);
        if (node == null) {
            return null;
        }
        while (packageName.length() > 0) {
            String nodeValue = node.get(packageName, null);
            if (nodeValue != null && !"".equals(nodeValue.trim())) {
                return nodeValue;
            }
            int lastIndexOf = packageName.lastIndexOf('.');
            if (lastIndexOf < 0) {
                return null;
            }
            packageName = packageName.substring(0,lastIndexOf);
        }
        return null;
    }
    public String getDeclaredPackagePreference(String keyName, String packageName) {
        Preferences node = getPreferenceChildNode(keyName);
        if (node == null) {
            return null;
        }
        String nodeValue = node.get(packageName, null);
        if (nodeValue != null && !"".equals(nodeValue.trim())) {
            return nodeValue;
        }
        return null;
    }


    public void setPreferenceValue(String keyName, String packageName, String value) {
        if ( packageName == null || packageName.length() == 0) {
            if (value == null) {
                this.setValue(keyName, "");
            } else {
                this.setValue(keyName, value);
            }
        } else {
            Preferences node = getPreferenceChildNode(keyName);
            if (value == null || value.length() == 0) {
                node.remove(packageName);
            } else {
                node.put(packageName, value);
            }
        }
    }

    public void removePreferenceValue(String keyName, String packageName) {
        if ( packageName == null || packageName.length() == 0) {
            this.setValue(keyName, "");
        } else{
            Preferences node = getPreferenceChildNode(keyName);
            node.remove(packageName);
        }

    }

}
