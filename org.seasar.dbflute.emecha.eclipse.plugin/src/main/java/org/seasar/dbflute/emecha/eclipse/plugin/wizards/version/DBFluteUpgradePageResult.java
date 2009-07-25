package org.seasar.dbflute.emecha.eclipse.plugin.wizards.version;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author jflute
 * @since 0.2.3 (2009/01/31 Saturday)
 */
public class DBFluteUpgradePageResult {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected String outputDirectory;
    protected String project;
    protected String versionInfoDBFlute;

    // ===================================================================================
    //                                                                           Filtering
    //                                                                           =========
    /**
     * @param line The line string. (NotNull)
     * @param prefixOfMark The mark of prefix. For example '${'. (NotNull)
     * @param suffixOfMark The mark of suffix. For example '}'. (NotNull)
     * @return The filtered line string. (NotNull)
     */
    public String filterLineByResult(String line, String prefixOfMark, String suffixOfMark) {
        final Method[] methods = this.getClass().getMethods();
        for (Method method : methods) {
            if (!method.getName().startsWith("get")) {
                continue;
            }
            if (!method.getReturnType().equals(String.class)) {
                continue;
            }
            final String propertyNameInitCap = method.getName().substring("get".length());
            final String firstString = propertyNameInitCap.substring(0, 1);
            final String remainderString = propertyNameInitCap.substring(1);
            final String propertyName = firstString.toLowerCase() + remainderString;
            String value;
            try {
                value = (String) method.invoke(this, new Object[] {});
            } catch (IllegalArgumentException e) {
                throw new IllegalStateException(e);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            } catch (InvocationTargetException e) {
                throw new IllegalStateException(e);
            }
            line = replace(line, prefixOfMark + propertyName + suffixOfMark, value);
        }
        return line;
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String replace(String text, String fromText, String toText) {
        if (text == null) {
            return null;
        }
        if (fromText == null || toText == null) {
            String msg = "The fromText and toText should not be null:";
            msg = msg + " fromText=" + fromText + " toText=" + toText;
            throw new IllegalArgumentException(msg);
        }
        StringBuffer buf = new StringBuffer(100);
        int pos = 0;
        int pos2 = 0;
        while (true) {
            pos = text.indexOf(fromText, pos2);
            if (pos == 0) {
                buf.append(toText);
                pos2 = fromText.length();
            } else if (pos > 0) {
                buf.append(text.substring(pos2, pos));
                buf.append(toText);
                pos2 = pos + fromText.length();
            } else {
                buf.append(text.substring(pos2));
                break;
            }
        }
        return buf.toString();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(String containerName) {
        this.outputDirectory = containerName;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String projectName) {
        this.project = projectName;
    }

    public String getVersionInfoDBFlute() {
        return versionInfoDBFlute;
    }

    public void setVersionInfoDBFlute(String versionInfoDBFlute) {
        this.versionInfoDBFlute = versionInfoDBFlute;
    }
}
