/*
 * Copyright 2013 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.dbflute.emecha.synchronizer.preferences;

import org.eclipse.jface.preference.*;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.dbflute.emecha.synchronizer.EMSynchronizer;

/**
 * This class represents a preference page that is contributed to the Preferences dialog. By subclassing
 * <samp>FieldEditorPreferencePage</samp>, we can use the field support built into JFace that allows us to create a page
 * that is small and knows how to save, restore and apply itself. <p> This page is used to modify preferences only. They
 * are stored in the preference store that belongs to the main plug-in class. That way, preferences can be accessed
 * directly via the preference store.
 */

public class EMSynchronizerPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    private IntegerFieldEditor portEditor;

    public EMSynchronizerPreferencePage() {
        super(GRID);
        setPreferenceStore(EMSynchronizer.getDefault().getPreferenceStore());
        setDescription("Resource Synchronizer Settings."); //$NON-NLS-1$
    }

    /**
     * Creates the field editors. Field editors are abstractions of the common GUI blocks needed to manipulate various
     * types of preferences. Each field editor knows how to save and restore itself.
     */
    public void createFieldEditors() {
        portEditor = new IntegerFieldEditor(PreferenceConstants.P_LISTEN_PORT, "&Port", getFieldEditorParent()); //$NON-NLS-1$
        portEditor.setEmptyStringAllowed(false);
        addField(portEditor);

    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
     */
    public void init(IWorkbench workbench) {
    }

    @Override
    protected void performApply() {
        super.performApply();
        int newValue = portEditor.getIntValue();
        int serverPort = EMSynchronizer.getServerPort();
        if (serverPort != newValue) {
            EMSynchronizer.serverRestart();
        }
    }
}