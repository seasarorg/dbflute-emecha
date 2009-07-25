package org.seasar.dbflute.emecha.eclipse.plugin.core.wrapper.resource;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.seasar.dbflute.emecha.eclipse.plugin.core.exception.EmExceptionHandler;
import org.seasar.dbflute.emecha.eclipse.plugin.core.exception.EmPluginException;
import org.seasar.dbflute.emecha.eclipse.plugin.core.wrapper.runtime.EmProgressMonitor;

/**
 * @author jflute
 * @since 0.1.0 (2007/07/21 Saturday)
 */
public class EmContainer {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private IContainer container;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public EmContainer(IContainer container) {
        this.container = container;
    }

    // ===================================================================================
    //                                                                      Finding Member
    //                                                                      ==============
    public IResource findMember(String memberName) {
        return container.findMember(memberName);
    }

    /**
     * Find container.
     * 
     * @param containerName Container name. (Contains '/' OK)
     * @return The desired container. (Nullable: if the result is null, it means the container has not existed.) 
     */
    public EmContainer findContainer(String containerName) {
        if (containerName.contains("/")) {
            return handlePathElements4ContainerFinding(containerName.split("/"));
        }
        final IResource resource = container.findMember(new Path(containerName));
        if (resource == null || !resource.exists() || !(resource instanceof IContainer)) {
            String msg = "The container does not exist! EmContainer.findMember() returned " + resource;
            msg = msg + " containerName=" + containerName;
            msg = msg + " baseContainer=" + container;
            throw new EmPluginException(msg);
        }
        return new EmContainer((IContainer) resource);
    }

    protected EmContainer handlePathElements4ContainerFinding(String[] pathElements) {
        int count = 0;
        EmContainer childContainer = null;
        for (String pathElement : pathElements) {
            childContainer = doOneLoopHandlingOfPathElement4ContainerFinding(count, childContainer, pathElement);
            if (childContainer == null) {
                return null;
            }
            ++count;
        }
        return childContainer;
    }

    protected EmContainer doOneLoopHandlingOfPathElement4ContainerFinding(int count, EmContainer currentContainer,
            String pathElement) {
        if (count == 0) {
            return this.findContainer(pathElement);
        } else {
            return currentContainer.findContainer(pathElement);
        }
    }

    // ===================================================================================
    //                                                                            Location
    //                                                                            ========
    public File getLocationPureFile() {
        return getContainer().getLocation().toFile();
    }

    // ===================================================================================
    //                                                                       File Creation
    //                                                                       =============
    /**
     * @param containerName Container name. (Contains '/' OK)
     */
    public void createDir(String containerName) {
        if (containerName.contains("/")) {
            handlePathElements4DirCreation(containerName.split("/"));
            return;
        }
        try {
            final EmPath fullpath = createPath(containerName);
            if (notExists(fullpath)) {
                final String[] segments = fullpath.segments();
                final StringBuffer sb = new StringBuffer();
                for (int i = 0; i < segments.length; i++) {
                    EmPath p = createPath(sb.append(segments[i]).toString());
                    if (notExists(p)) {
                        final EmFolder folder = getFolder(p);
                        folder.create(true, true, null);
                    }
                    sb.append('/');
                }
            }
        } catch (Throwable t) {
            String msg = this.getClass().getName() + "#createDir(): containerName=" + containerName;
            EmExceptionHandler.throwAsPluginException(msg, t);
        }
    }

    protected void handlePathElements4DirCreation(String[] pathElements) {
        int count = 0;
        EmContainer childContainer = null;
        for (String pathElement : pathElements) {
            childContainer = doOneLoopHandlingOfPathElement4DirCreation(count, childContainer, pathElement);
            ++count;
        }
    }

    protected EmContainer doOneLoopHandlingOfPathElement4DirCreation(int count, EmContainer currentContainer,
            String pathElement) {
        if (count == 0) {
            this.createDir(pathElement);
            return this.findContainer(pathElement);
        } else {
            currentContainer.createDir(pathElement);
            return currentContainer.findContainer(pathElement);
        }
    }

    public EmFile createFile(String fileName) {
        final IPath path = createPath(fileName).getPath();
        final IFile file = container.getFile(path);
        return new EmFile(file);
    }

    public EmFile createFileOrSetContents(String fileName, String contents, EmProgressMonitor progressMonitor) {
        final EmFile file = createFile(fileName);
        InputStream stream = null;
        try {
            stream = new ByteArrayInputStream(contents.getBytes());
            if (file.exists()) {
                file.setContents(stream, true, true, progressMonitor);
            } else {
                file.create(stream, true, progressMonitor);
            }
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException ignored) {
                }
            }
        }
        return file;
    }

    public EmFile getFile(EmPath path) {
        return new EmFile(container.getFile(path.getPath()));
    }

    public int getType() {
        return container.getType();
    }

    public boolean isAccessible() {
        return container.isAccessible();
    }

    protected EmPath createPath(String pathString) {
        return new EmPath(pathString);
    }

    protected EmFolder getFolder(EmPath path) {
        return new EmFolder(container.getFolder(path.getPath()));
    }

    protected boolean exists(EmPath path) {
        return container.exists(path.getPath());
    }

    protected boolean notExists(EmPath path) {
        return !exists(path);
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public IContainer getContainer() {
        return container;
    }
}
