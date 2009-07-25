package org.seasar.dbflute.emecha.eclipse.plugin.core.wrapper.resource;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.seasar.dbflute.emecha.eclipse.plugin.core.exception.EmPluginException;

/**
 * 
 * @author jflute
 * @since 0.1.0 (2007/07/21 Saturday)
 */
public class EmWorkspaceRoot {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private IWorkspaceRoot workspaceRoot;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public EmWorkspaceRoot(IWorkspaceRoot workspaceRoot) {
        this.workspaceRoot = workspaceRoot;
    }

    // ===================================================================================
    //                                                                        Self Creator
    //                                                                        ============
    public static EmWorkspaceRoot create() {
        return new EmWorkspaceRoot(ResourcesPlugin.getWorkspace().getRoot());
    }

    // ===================================================================================
    //                                                                    Component Finder
    //                                                                    ================
    /**
     * Find container.
     * 
     * @param containerName Container name. (NotNull)
     * @return Container. (NotNull)
     * @exception org.seasar.dbflute.emecha.eclipse.plugin.core.exception.EmPluginException When the container does not exist.
     */
    public EmContainer findContainer(String containerName) {
        if (containerName == null || containerName.trim().length() == 0) {
            String msg = "The argument[containerName] should not be null! at " + getClass().getSimpleName();
            throw new IllegalArgumentException(msg);
        }
        containerName = containerName.trim();// Because sometimes it has line separator at the rear.
        final IResource resource = workspaceRoot.findMember(new Path(containerName));
        if (resource == null || !resource.exists() || !(resource instanceof IContainer)) {
            String msg = "The container does not exist! IWorkspaceRoot.findMember() returned " + resource;
            msg = msg + " containerName=" + containerName;
            msg = msg + " workspaceRoot=" + workspaceRoot;
            throw new EmPluginException(msg);
        }
        return new EmContainer((IContainer) resource);
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public IWorkspaceRoot getWorkspaceRoot() {
        return workspaceRoot;
    }

    @Override
    public String toString() {
        return workspaceRoot != null ? workspaceRoot.toString() : "The workspaceRoot is null!";
    }
}
