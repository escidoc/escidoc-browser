package org.escidoc.browser;

import gov.loc.www.zing.srw.SearchRetrieveRequestType;

import java.util.Map;

public final class Util {

    public final static boolean isEscidocUrlExists(
        final Map<String, String[]> parameters) {
        return parameters.containsKey(AppConstants.ESCIDOC_URL);
    }

    public final static boolean isTokenExist(
        final Map<String, String[]> parameters) {
        return parameters.containsKey(AppConstants.ESCIDOC_USER_HANDLE);
    }

    public final static SearchRetrieveRequestType createTopLevelQuery(
        final String id) {
        final SearchRetrieveRequestType filter =
            new SearchRetrieveRequestType();
        filter.setQuery(getQuery(id));
        return filter;
    }

    public final static String getQuery(final String id) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
            .append("top-level-containers=true OR \"/properties/context/id=");
        stringBuilder.append(id);
        stringBuilder.append("\"");
        final String topLevelContainerQuery = stringBuilder.toString();
        return topLevelContainerQuery;
    }
}
