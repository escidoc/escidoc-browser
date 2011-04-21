package org.escidoc.browser.ui.helper;

import gov.loc.www.zing.srw.SearchRetrieveRequestType;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import org.escidoc.browser.AppConstants;
import org.escidoc.browser.model.ContainerModel;
import org.escidoc.browser.model.ItemModel;
import org.escidoc.browser.model.ResourceModel;

import com.google.common.base.Preconditions;

import de.escidoc.core.resources.om.container.Container;
import de.escidoc.core.resources.om.item.Item;
import de.escidoc.core.resources.sb.Record;
import de.escidoc.core.resources.sb.search.records.ResourceRecord;

public final class Util {

    public final static boolean isEscidocUrlExists(final Map<String, String[]> parameters) {
        Preconditions.checkNotNull(parameters, "parameters is null: %s", parameters);
        return parameters.containsKey(AppConstants.ESCIDOC_URL);
    }

    public final static boolean doesTokenExist(final Map<String, String[]> parameters) {
        Preconditions.checkNotNull(parameters, "parameters is null: %s", parameters);
        return parameters.containsKey(AppConstants.ESCIDOC_USER_HANDLE);
    }

    public final static SearchRetrieveRequestType createEmptyFilter() {
        return new SearchRetrieveRequestType();
    }

    public final static SearchRetrieveRequestType createQueryForTopLevelContainers(final String id) {
        Preconditions.checkNotNull(id, "id is null: %s", id);
        Preconditions.checkArgument(!id.isEmpty(), "id is empty: %s", id);
        final SearchRetrieveRequestType filter = new SearchRetrieveRequestType();
        filter.setQuery(createQuery(id));
        return filter;
    }

    private final static String createQuery(final String id) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("top-level-containers=true OR \"/properties/context/id=");
        stringBuilder.append(id);
        stringBuilder.append("\"");
        final String topLevelContainerQuery = stringBuilder.toString();
        return topLevelContainerQuery;
    }

    public static final void addToResults(final List<ResourceModel> results, final Record<?> record) {
        Preconditions.checkNotNull(results, "results is null: %s", results);
        Preconditions.checkNotNull(record, "record is null: %s", record);
        if (isContainer(record)) {
            results.add(toContainerModel(record));
        }
        else if (isItem(record)) {
            results.add(toItemModel(record));
        }
    }

    public static final boolean isItem(final Record<?> record) {
        Preconditions.checkNotNull(record, "record is null: %s", record);
        return ((ResourceRecord<?>) record).getRecordDataType().equals(Item.class);
    }

    public static final boolean isContainer(final Record<?> record) {
        Preconditions.checkNotNull(record, "record is null: %s", record);
        return ((ResourceRecord<?>) record).getRecordDataType().equals(Container.class);
    }

    public static final ResourceModel toItemModel(final Record<?> record) {
        return new ItemModel(getSRWResourceRecordData(record, Item.class));
    }

    public static final ResourceModel toContainerModel(final Record<?> record) {
        return new ContainerModel(getSRWResourceRecordData(record, Container.class));
    }

    @SuppressWarnings("unchecked")
    public static final <T> T getSRWResourceRecordData(final Record<?> record, final Class<T> resource) {
        Preconditions.checkNotNull(record, "record is null: %s", record);
        Preconditions.checkNotNull(resource, "resource is null: %s", resource);

        if (record instanceof ResourceRecord<?>) {
            if (((ResourceRecord<?>) ((ResourceRecord<?>) record)).getRecordDataType() == resource) {
                return (T) record.getRecordData();
            }
        }
        throw new RuntimeException("Unrecognized type: " + record.getClass());
    }

    public static URI parseEscidocUriFrom(final Map<String, String[]> parameters) throws URISyntaxException {
        final URI escidocUri = new URI(parameters.get(AppConstants.ESCIDOC_URL)[0]);
        return escidocUri;
    }

    public static SearchRetrieveRequestType createQueryForTopLevelItems(final String id) {
        Preconditions.checkNotNull(id, "id is null: %s", id);
        Preconditions.checkArgument(!id.isEmpty(), "id is empty: %s", id);
        final SearchRetrieveRequestType filter = new SearchRetrieveRequestType();
        filter.setQuery(topLevelItems(id));
        return filter;
    }

    private static String topLevelItems(final String id) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("top-level-items=true OR \"/properties/context/id=");
        stringBuilder.append(id);
        stringBuilder.append("\"");
        final String topLevelContainerQuery = stringBuilder.toString();
        return topLevelContainerQuery;
    }
}
