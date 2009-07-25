package org.seasar.dbflute.emecha.eclipse.plugin.core.wrapper.resource;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author jflute
 * @since 0.1.0 (2007/07/21 Saturday)
 */
public class EmContainerTest {

    @Test
    public void test_createPath() throws Exception {
        final EmContainer container = new EmContainer(null);
        final EmPath path = container.createPath("dummy");
        Assert.assertNotNull(path);
    }
}
