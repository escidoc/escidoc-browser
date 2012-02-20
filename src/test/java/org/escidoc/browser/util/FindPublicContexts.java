package org.escidoc.browser.util;

import static org.junit.Assert.assertTrue;

import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.internal.EscidocServiceLocationImpl;
import org.escidoc.browser.repository.internal.ContextRepository;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

public class FindPublicContexts {
    @Ignore
    @Test
    public void shouldListMinimalOneContext() throws Exception {
        // Given X0 && ...Xn
        // When
        EscidocServiceLocationImpl a = new EscidocServiceLocationImpl();
        a.setEscidocUri("http://esfedrep1.fiz-karlsruhe.de:8080");
        ContextRepository repository = new ContextRepository(a);
        List<ResourceModel> all = repository.findAll();
        // Then ensure that
        assertTrue(!all.isEmpty());
    }

}
