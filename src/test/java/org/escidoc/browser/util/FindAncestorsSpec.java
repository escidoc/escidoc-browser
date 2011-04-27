package org.escidoc.browser.util;

import static org.hamcrest.Matchers.is;

import java.net.URI;

import junit.framework.Assert;

import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceType;
import org.escidoc.browser.model.internal.HasNoNameResourceImpl;
import org.escidoc.browser.repository.UtilRepository;
import org.escidoc.browser.repository.internal.UtilRepositoryImpl;
import org.junit.Test;

public class FindAncestorsSpec {

    private static final String INPUT_ID = "escidoc:16037";

    final String[] LEAF_AND_ITS_ANCESTORS = new String[] { INPUT_ID,
        "escidoc:16038", "escidoc:16048", "escidoc:10281" };

    private UtilRepository repository;

    @Test
    public void itShouldReturnListOfResourceWithContextAsItsLastElement()
        throws Exception {
        initRepo();
        // when
        final ResourceModel[] result =
            repository.findAncestors(new HasNoNameResourceImpl(INPUT_ID,
                ResourceType.ITEM));

        // should
        Assert.assertEquals(LEAF_AND_ITS_ANCESTORS, result);
    }

    @Test
    public void itShouldReturnParentOfItem() throws Exception {
        // Given:
        initRepo();
        final String parentOfItem = "escidoc:16038";
        // When:
        final ResourceModel parent =
            repository.findParent(new HasNoNameResourceImpl(INPUT_ID,
                ResourceType.ITEM));
        // AssertThat:
        org.hamcrest.MatcherAssert.assertThat(parent.getId(), is(parentOfItem));
    }

    @Test
    public void itShouldReturnParentOfContainer() throws Exception {
        // Given:
        initRepo();
        final String parentOfContainer = "escidoc:16048";
        // When:
        final ResourceModel parent =
            repository.findParent(new HasNoNameResourceImpl("escidoc:16038",
                ResourceType.CONTAINER));
        // AssertThat:
        org.hamcrest.MatcherAssert.assertThat(parent.getId(),
            is(parentOfContainer));
    }

    @Test
    public void itShouldReturnContextOfContainer() throws Exception {
        initRepo();

        // When:
        final ResourceModel parent =
            repository.findParent(new HasNoNameResourceImpl("escidoc:16048",
                ResourceType.CONTAINER));

        org.hamcrest.MatcherAssert.assertThat(parent.getId(),
            is("escidoc:10281"));
    }

    private void initRepo() {
        repository = new UtilRepositoryImpl(new EscidocServiceLocation() {

            @Override
            public void setEscidocUri(final URI escidocUri) {
                throw new UnsupportedOperationException("Not yet implemented");

            }

            @Override
            public void setApplicationUri(final URI appUri) {
                throw new UnsupportedOperationException("Not yet implemented");

            }

            @Override
            public String getLogoutUri() {
                throw new UnsupportedOperationException("Not yet implemented");

            }

            @Override
            public String getLoginUri() {
                throw new UnsupportedOperationException("Not yet implemented");
            }

            @Override
            public String getEscidocUri() {
                return "http://escidev4:8080";
            }
        });
    }
}
