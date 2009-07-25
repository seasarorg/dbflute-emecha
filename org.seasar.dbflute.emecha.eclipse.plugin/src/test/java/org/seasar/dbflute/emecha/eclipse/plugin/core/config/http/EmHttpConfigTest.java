package org.seasar.dbflute.emecha.eclipse.plugin.core.config.http;

import org.junit.Assert;
import org.junit.Test;
import org.seasar.dbflute.emecha.eclipse.plugin.core.meta.website.EmMetaFromWebSite;

/**
 * 
 * @author jflute
 * @since 0.1.0 (2007/09/19 Wednesday)
 */
public class EmHttpConfigTest {

    @Test
    public void test_EmHttpConfig_getLatestVersionS2Dao() throws Exception {
        // ## Arrange ##
        final EmMetaFromWebSite config = new EmMetaFromWebSite();
        config.loadMeta();

        // ## Act ##
        final String latestVersionS2Dao = config.getLatestVersionS2Dao();

        // ## Assert ##
        System.out.println(latestVersionS2Dao);
        Assert.assertNotNull(latestVersionS2Dao);
    }

    @Test
    public void test_EmHttpConfig_getLatestVersionDBFlute() throws Exception {
        // ## Arrange ##
        final EmMetaFromWebSite config = new EmMetaFromWebSite();
        config.loadMeta();

        // ## Act ##
        final String latestVersionDBFlute = config.getLatestVersionDBFlute();

        // ## Assert ##
        System.out.println(latestVersionDBFlute);
        Assert.assertNotNull(latestVersionDBFlute);
    }

    @Test
    public void test_EmHttpConfig_getLatestSnapshotVersionDBFlute() throws Exception {
        // ## Arrange ##
        final EmMetaFromWebSite config = new EmMetaFromWebSite();
        config.loadMeta();

        // ## Act ##
        final String latestVersionDBFlute = config.getLatestSnapshotVersionDBFlute();

        // ## Assert ##
        System.out.println(latestVersionDBFlute);
        Assert.assertNotNull(latestVersionDBFlute);
    }
}
