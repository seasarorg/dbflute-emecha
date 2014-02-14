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
package org.dbflute.emecha.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * EMecha prefarences pages grouping.
 * @author schatten
 */
public class EMechaPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

    /**
     *
     */
    public EMechaPreferencePage() {
        super("EMecha");
        noDefaultAndApplyButton();
    }

    /**
     * @param title
     */
    public EMechaPreferencePage(String title) {
        super(title);
        noDefaultAndApplyButton();
    }

    /**
     * {@inheritDoc}
     * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
     */
    @Override
    public void init(IWorkbench workbench) {
        // nothing
    }

    /**
     * {@inheritDoc}
     * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createContents(Composite parent) {
        setTitle("This is blank page. Please select submenu.");
        Composite composite = new Composite(parent, SWT.NONE);
        return composite;
    }

}
