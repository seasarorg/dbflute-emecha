package org.seasar.dbflute.emecha.eclipse.plugin.dfassist.nls;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "org.seasar.dbflute.emecha.eclipse.plugin.dfassist.nls.messages"; //$NON-NLS-1$
    public static String HYPERLINK_CUSTOMIZE_ENTITY;
    public static String HYPERLINK_PARAMETER_BEAN;
    public static String HYPERLINK_SQL_FILE;
    public static String QUICK_FIX_DERIVED_FIELD_PROPERTY;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
