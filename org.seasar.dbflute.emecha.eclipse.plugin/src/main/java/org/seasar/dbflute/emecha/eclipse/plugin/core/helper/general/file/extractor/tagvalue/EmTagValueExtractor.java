package org.seasar.dbflute.emecha.eclipse.plugin.core.helper.general.file.extractor.tagvalue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author jflute
 * @since 0.1.0 (2007/08/12 Sunday)
 */
public class EmTagValueExtractor {

    protected String startTag;
    protected String endTag;

    public EmTagValueExtractor(String startTag, String endTag) {
        this.startTag = startTag;
        this.endTag = endTag;
    }

    /**
     * Extract tag value.
     * 
     * @param in Input stream. After extrating this stream will be closed. (NotNull)
     * @param filterList The list of filter. (Nullable)
     * @return Extracted tag value. (Nullable: when not found)
     * @throws IOException
     */
    public String extractTagValue(InputStream in, java.util.List<EmExtractedTagValueFilter> filterList)
            throws IOException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            boolean nextLineTarget = false;
            final StringBuilder sb = new StringBuilder();
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                line = line.trim();
                if (nextLineTarget) {
                    if (line.contains(endTag)) {
                        sb.append(line.substring(0, line.indexOf(endTag)).trim());
                        break;
                    }
                    sb.append(line);
                    continue;
                }
                if (line.contains(startTag)) {
                    final String rearString = line.substring(line.indexOf(startTag) + startTag.length()).trim();
                    if (rearString.contains(endTag)) {
                        final String targetString = rearString.substring(0, line.indexOf(endTag)).trim();
                        sb.append(targetString);
                        break;
                    }
                    sb.append(rearString);
                    nextLineTarget = true;
                }
            }
            String tagValue = sb.toString();
            if (filterList != null && !filterList.isEmpty()) {
                for (EmExtractedTagValueFilter filter : filterList) {
                    tagValue = filter.filterTagValue(tagValue);
                }
            }
            return tagValue;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignored) {
                    ignored.printStackTrace();
                }
            }
        }
    }
}
