package org.seasar.dbflute.emecha.eclipse.plugin.core.helper.dicon.extract;

import org.seasar.dbflute.emecha.eclipse.plugin.core.helper.general.file.extractor.tagvalue.EmExtractedTagValueFilter;

/**
 * @author jflute
 * @since 0.1.0 (2007/08/12 Sunday)
 */
public interface EmExtractingTagValueResource {
    public String getDiconName();

    public String getStartTag();

    public String getEndTag();

    public java.util.List<EmExtractedTagValueFilter> getFilterList();
}
