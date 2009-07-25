package org.seasar.dbflute.emecha.eclipse.plugin.core.wrapper.resource;

import org.junit.Test;
import org.seasar.dbflute.emecha.eclipse.plugin.unit.PlainTestCase;

/**
 * @author jflute
 * @since 0.1.0 (2007/07/21 Saturday)
 */
public class EmContainerTest extends PlainTestCase {

    @Test
    public void test_createPath() throws Exception {
        final EmContainer container = new EmContainer(null);
        final EmPath path = container.createPath("dummy");
        assertNotNull(path);
    }
}
