/**
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License").  You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE
 * or https://www.escidoc.org/license/ESCIDOC.LICENSE .
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at license/ESCIDOC.LICENSE.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 *
 *
 * Copyright 2012 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.
 * All rights reserved.  Use is subject to license terms.
 */
package org.escidoc.browser.ui.helper;

import com.google.common.base.Preconditions;

import org.escidoc.browser.AppConstants;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.internal.ContainerModel;
import org.escidoc.browser.model.internal.ItemModel;
import org.escidoc.browser.util.Utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import de.escidoc.core.resources.om.container.Container;
import de.escidoc.core.resources.om.item.Item;
import de.escidoc.core.resources.sb.search.SearchResult;
import gov.loc.www.zing.srw.SearchRetrieveRequestType;

public final class Util {

    /**
     * No instance allowed for utility classes.
     */
    private Util() {
    }

    public static final boolean isEscidocUrlExists(final Map<String, String[]> parameters) {
        Preconditions.checkNotNull(parameters, "parameters is null: %s", parameters);
        return parameters.containsKey(AppConstants.ESCIDOC_URL);
    }

    public static final boolean hasTabArg(final Map<String, String[]> parameters) {
        Preconditions.checkNotNull(parameters, "parameters is null: %s", parameters);
        return parameters.containsKey(AppConstants.ARG_TAB);
    }

    public static boolean hasObjectType(final Map<String, String[]> parameters) {
        Preconditions.checkNotNull(parameters, "parameters is null: %s", parameters);
        return parameters.containsKey(AppConstants.ARG_TYPE)
            && (parameters.get(AppConstants.ARG_TYPE)[0].equals("ITEM")
                || parameters.get(AppConstants.ARG_TYPE)[0].equals("CONTAINER")
                || parameters.get(AppConstants.ARG_TYPE)[0].equals("CONTEXT") || parameters.get(AppConstants.ARG_TYPE)[0]
                .equals("sa"));
    }

    public final static boolean doesTokenExist(final Map<String, String[]> parameters) {
        Preconditions.checkNotNull(parameters, "parameters is null: %s", parameters);
        return parameters.containsKey(AppConstants.ESCIDOC_USER_HANDLE);
    }

    public final static SearchRetrieveRequestType createEmptyFilter() {
        return Utils.createEmptyFilter();
    }

    public static final void addToResults(final List<ResourceModel> results, final SearchResult searchResult) {
        Preconditions.checkNotNull(results, "results is null: %s", results);
        Preconditions.checkNotNull(searchResult, "record is null: %s", searchResult);

        if (searchResult.getContent() == null) {
            return;
        }

        if (searchResult.getContent() instanceof Container) {
            results.add(new ContainerModel((Container) searchResult.getContent()));
        }
        else if (searchResult.getContent() instanceof Item) {
            results.add(new ItemModel((Item) searchResult.getContent()));
        }
    }

    public static URI parseEscidocUriFrom(final Map<String, String[]> parameters) throws URISyntaxException {
        final URI escidocUri = new URI(parameters.get(AppConstants.ESCIDOC_URL)[0]);
        return escidocUri;
    }

    public static SearchRetrieveRequestType createQueryForTopLevelContainersAndItems(final String id) {
        Preconditions.checkNotNull(id, "id is null: %s", id);
        Preconditions.checkArgument(!id.isEmpty(), "id is empty: %s", id);
        final SearchRetrieveRequestType filter = Utils.createEmptyFilter();
        filter.setQuery(topLevelContainersAndItems(id));
        return filter;
    }

    private static String topLevelContainersAndItems(final String id) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(top-level-containers=true or top-level-items=true) and \"/properties/context/id\"=");
        stringBuilder.append(id);
        final String query = stringBuilder.toString();
        return query;
    }
}