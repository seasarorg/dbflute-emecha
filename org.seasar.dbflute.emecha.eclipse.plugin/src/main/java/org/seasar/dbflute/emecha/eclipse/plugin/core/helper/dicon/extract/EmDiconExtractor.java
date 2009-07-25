package org.seasar.dbflute.emecha.eclipse.plugin.core.helper.dicon.extract;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.seasar.dbflute.emecha.eclipse.plugin.core.exception.EmExceptionHandler;
import org.seasar.dbflute.emecha.eclipse.plugin.core.exception.EmPluginException;
import org.seasar.dbflute.emecha.eclipse.plugin.core.helper.general.file.extractor.tagvalue.EmExtractedTagValueFilter;
import org.seasar.dbflute.emecha.eclipse.plugin.core.helper.general.file.extractor.tagvalue.EmTagValueExtractor;
import org.seasar.dbflute.emecha.eclipse.plugin.core.util.net.EmURLUtil;
import org.seasar.dbflute.emecha.eclipse.plugin.core.wrapper.resource.EmContainer;
import org.seasar.dbflute.emecha.eclipse.plugin.core.wrapper.resource.EmWorkspaceRoot;

/**
 * @author jflute
 * @since 0.1.0 (2007/08/12 Sunday)
 */
public class EmDiconExtractor {

    public String extractTagValue(String projectName, EmExtractingTagValueResource resource) {
        try {
            final IResource jdbcDiconMember = findResource(projectName, resource.getDiconName());
            if (jdbcDiconMember == null) {
                return null;
            }
            final String startTag = resource.getStartTag();
            final String endTag = resource.getEndTag();
            final EmTagValueExtractor extractor = new EmTagValueExtractor(startTag, endTag);
            final List<EmExtractedTagValueFilter> filterList = resource.getFilterList();
            final URL jdbcDiconUrl = jdbcDiconMember.getRawLocationURI().toURL();
            final InputStream jdbcDiconInputStream = EmURLUtil.openStream(jdbcDiconUrl);
            return extractor.extractTagValue(jdbcDiconInputStream, filterList);
        } catch (IOException e) {
            String msg = "It threw the IO exception!";
            EmExceptionHandler.show(msg, e);
            return null;
        } catch (Throwable t) {
            String msg = "It threw the throwable!";
            EmExceptionHandler.show(msg, t);
            return null;
        }
    }

    protected EmContainer findSrcMainResourecsContainer(EmContainer projectContainer) {
        final EmContainer resourcesContainer;
        try {
            resourcesContainer = projectContainer.findContainer("/src/main/resources");
        } catch (EmPluginException e) {
            return null;
        }
        return resourcesContainer;
    }

    protected IResource findResource(String projectName, String diconName) {
        final EmContainer projectContainer = EmWorkspaceRoot.create().findContainer(projectName);
        if (projectContainer == null || !projectContainer.getContainer().exists()) {
            return null;
        }
        final EmContainer resourcesContainer = findSrcMainResourecsContainer(projectContainer);
        if (resourcesContainer == null || !resourcesContainer.getContainer().exists()) {
            return null;
        }
        final IResource jdbcDiconMember = resourcesContainer.findMember(diconName);
        if (jdbcDiconMember == null || !jdbcDiconMember.exists()) {
            return null;
        }
        return jdbcDiconMember;
    }
}
