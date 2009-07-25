package org.seasar.dbflute.emecha.eclipse.plugin.core.helper.general.file.extractor.tagvalue;


/**
 * @author jflute
 * @since 0.1.0 (2007/08/12 Sunday)
 */
public class EmExtractedTagValueFilterTrimTag implements EmExtractedTagValueFilter {
    protected String targetTagName;

    public EmExtractedTagValueFilterTrimTag(String targetTagName) {
        this.targetTagName = targetTagName;
    }

    public String filterTagValue(String tagValue) {
        String startTag = "<" + targetTagName + ">";
        if (tagValue.startsWith(startTag)) {
            tagValue = tagValue.substring(startTag.length()).trim();
        }
        String endTag = "</" + targetTagName + ">";
        if (tagValue.endsWith(endTag)) {
            tagValue = tagValue.substring(0, tagValue.length() - endTag.length()).trim();
        }

        return tagValue;
    }
}