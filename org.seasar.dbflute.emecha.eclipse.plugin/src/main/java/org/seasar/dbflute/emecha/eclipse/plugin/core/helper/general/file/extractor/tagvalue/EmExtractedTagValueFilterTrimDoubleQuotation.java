package org.seasar.dbflute.emecha.eclipse.plugin.core.helper.general.file.extractor.tagvalue;


/**
 * @author jflute
 * @since 0.1.0 (2007/08/12 Sunday)
 */
public class EmExtractedTagValueFilterTrimDoubleQuotation implements EmExtractedTagValueFilter {
    public String filterTagValue(String tagValue) {
        if (tagValue.startsWith("\"")) {
            tagValue = tagValue.substring("\"".length()).trim();
        }
        if (tagValue.endsWith("\"")) {
            tagValue = tagValue.substring(0, tagValue.length() - "\"".length()).trim();
        }
        return tagValue;
    }
}