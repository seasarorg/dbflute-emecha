package org.seasar.dbflute.emecha.eclipse.plugin.unit;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.junit.Assert;

/**
 * @author jflute
 */
public abstract class PlainTestCase {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    //private static final Log _log = LogFactory.getLog(PlainTestCase.class);

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected void log(Object msg) {
    	System.out.println(msg);
    }

    protected Date currentDate() {
        return new Date();
    }

    protected Timestamp currentTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }

    protected String ln() {
        return "\n";
    }

    // ===================================================================================
    //                                                                       Assert Helper
    //                                                                       =============
    protected <T> void assertListEmtpy(List<T> ls) {
        if (!ls.isEmpty()) {
            Assert.fail("The list shuold be empty: ls=" + ls);
        }
    }

    protected <T> void assertListNotEmtpy(List<T> ls) {
        if (ls.isEmpty()) {
            Assert.fail("The list shuold not be empty: ls=" + ls);
        }
    }
}
