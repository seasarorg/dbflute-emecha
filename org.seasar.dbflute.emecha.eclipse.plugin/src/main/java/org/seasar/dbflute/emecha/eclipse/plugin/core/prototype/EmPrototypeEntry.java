/*
 * Copyright 2004-2006 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.emecha.eclipse.plugin.core.prototype;

import java.net.URL;
import java.util.Enumeration;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.seasar.dbflute.emecha.eclipse.plugin.EMechaPluginSymbol;
import org.seasar.dbflute.emecha.eclipse.plugin.core.exception.EmPluginException;

/**
 * @author jflute
 * @since 0.1.0 (2007/07/21 Saturday)
 */
public class EmPrototypeEntry {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    public static final String PROTOTYPE_PATH = "prototype";

    public static final String PROJECT_NAME_MARK = "$$project$$";

    public static final String FILE_SEPARATOR = "/";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final Bundle bundle;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public EmPrototypeEntry() {
        bundle = Platform.getBundle(EMechaPluginSymbol.getPluginId());
    }

    // ===================================================================================
    //                                                                             Creator
    //                                                                             =======
    public static EmPrototypeEntry create() {
        return new EmPrototypeEntry();
    }

    // ===================================================================================
    //                                                                        Path Builder
    //                                                                        ============
    public static String buildDBFluteClientPath() {
        return PROTOTYPE_PATH + FILE_SEPARATOR + "dbflute" + FILE_SEPARATOR + "client";
    }

    // ===================================================================================
    //                                                                        Entry Finder
    //                                                                        ============
    public URL findPrototypeEntry(String path) {
        return bundle.getEntry(resolvePath(PROTOTYPE_PATH, path));
    }

    public URL findDBFluteClientEntry(String path) {
        return bundle.getEntry(resolvePath(buildDBFluteClientPath(), path));
    }

    public java.util.List<URL> findDBFluteClientEntries() {
        final Enumeration<?> resources = bundle.findEntries(buildDBFluteClientPath(), "*", true);
        if (resources == null) {
            String msg = "Not found the resources: path=" + buildDBFluteClientPath();
            throw new EmPluginException(msg);
        }
        final java.util.List<URL> urlList = new java.util.ArrayList<URL>();
        while (resources.hasMoreElements()) {
            final URL url = (URL) resources.nextElement();
            final String path = url.getPath();
            if (path.contains(".svn")) {
                continue;
            }
            urlList.add(url);
        }
        return urlList;
    }

    protected String resolvePath(String baseDirectory, String path) {
        final String fileSeparator = FILE_SEPARATOR;
        if (path.startsWith(baseDirectory + fileSeparator)) {
            return path;
        }
        if (path.startsWith(fileSeparator + baseDirectory + fileSeparator)) {
            return path.substring(fileSeparator.length());
        }
        if (path.startsWith(fileSeparator)) {
            return baseDirectory + path;
        }
        return baseDirectory + fileSeparator + path;
    }

}
