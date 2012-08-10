/*
 *
 */
package org.seasar.dbflute.emecha.eclipse.plugin.dfassist;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 *
 * @author schatten
 */
public class DfAssistPlugin extends AbstractUIPlugin {
    // The plug-in ID
    public static final String PLUGIN_ID = "org.seasar.dbflute.emecha.eclipse.plugin.dfassist";

    private static DfAssistPlugin plugin;
    /**
     *
     */
    public DfAssistPlugin() {
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * @return plugin
     */
    public static DfAssistPlugin getPlugin() {
        return plugin;
    }

    /**
     * Returns an image descriptor for the image file at the given
     * plug-in relative path
     *
     * @param path the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(String path) {
        return imageDescriptorFromPlugin(PLUGIN_ID, path);
    }
    public static void log(String message) {
        IStatus status = new Status(IStatus.INFO, plugin.getBundle().getSymbolicName(), IStatus.INFO,
                message, null);
        plugin.getLog().log(status);
    }
    public static void log(String message, Throwable t) {
        IStatus status = new Status(IStatus.ERROR, plugin.getBundle().getSymbolicName(), IStatus.ERROR,
                message, t);
        plugin.getLog().log(status);
    }
    public static void log(Throwable t) {
        IStatus status = new Status(IStatus.ERROR, plugin.getBundle().getSymbolicName(), IStatus.ERROR,
                t.getMessage(), t);
        plugin.getLog().log(status);
    }
}
