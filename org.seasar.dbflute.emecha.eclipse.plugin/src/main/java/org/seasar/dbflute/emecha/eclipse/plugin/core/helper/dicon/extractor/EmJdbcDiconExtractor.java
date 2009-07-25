package org.seasar.dbflute.emecha.eclipse.plugin.core.helper.dicon.extractor;

import java.util.ArrayList;
import java.util.List;

import org.seasar.dbflute.emecha.eclipse.plugin.core.helper.general.file.extractor.tagvalue.EmExtractedTagValueFilter;
import org.seasar.dbflute.emecha.eclipse.plugin.core.helper.general.file.extractor.tagvalue.EmExtractedTagValueFilterTrimDoubleQuotation;

/**
 * @author jflute
 * @since 0.1.0 (2007/08/12 Sunday)
 */
public class EmJdbcDiconExtractor {

    public String extractDatabaseUrl(String projectName) {
        final EmExtractingTagValueResource resource = new EmExtractingTagValueResource() {

            public String getDiconName() {
                return "jdbc.dicon";
            }

            public String getStartTag() {
                return "<property name=\"URL\">";
            }

            public String getEndTag() {
                return "</property>";
            }

            public List<EmExtractedTagValueFilter> getFilterList() {
                final List<EmExtractedTagValueFilter> filterList = new ArrayList<EmExtractedTagValueFilter>();
                filterList.add(new EmExtractedTagValueFilterTrimDoubleQuotation());
                return filterList;
            }
        };
        return new EmDiconExtractor().extractTagValue(projectName, resource);
    }
}