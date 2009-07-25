package org.seasar.dbflute.emecha.eclipse.plugin.core.helper.dicon.extract;

import java.util.ArrayList;
import java.util.List;

import org.seasar.dbflute.emecha.eclipse.plugin.core.helper.general.file.extractor.tagvalue.EmExtractedTagValueFilter;
import org.seasar.dbflute.emecha.eclipse.plugin.core.helper.general.file.extractor.tagvalue.EmExtractedTagValueFilterTrimDoubleQuotation;
import org.seasar.dbflute.emecha.eclipse.plugin.core.helper.general.file.extractor.tagvalue.EmExtractedTagValueFilterTrimTag;

/**
 * @author jflute
 * @since 0.1.0 (2007/08/12 Sunday)
 */
public class EmConventionDiconExtractor extends EmDiconExtractor {

    public String extractRootPackage(String projectName) {
        final EmExtractingTagValueResource resource = new EmExtractingTagValueResource() {

            public String getDiconName() {
                return "convention.dicon";
            }

            public String getStartTag() {
                return "<initMethod name=\"addRootPackageName\">";
            }

            public String getEndTag() {
                return "</initMethod>";
            }

            public List<EmExtractedTagValueFilter> getFilterList() {
                final List<EmExtractedTagValueFilter> filterList = new ArrayList<EmExtractedTagValueFilter>();
                filterList.add(new EmExtractedTagValueFilterTrimTag("arg"));
                filterList.add(new EmExtractedTagValueFilterTrimDoubleQuotation());
                return filterList;
            }
        };
        return new EmDiconExtractor().extractTagValue(projectName, resource);
    }
}
