package org.escidoc.browser.model.internal;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.common.base.Preconditions;

import de.escidoc.core.resources.common.MetadataRecord;
import de.escidoc.core.resources.common.MetadataRecords;
import de.escidoc.core.resources.common.properties.PublicStatus;
import de.escidoc.core.resources.common.reference.ContentModelRef;
import de.escidoc.core.resources.common.reference.ContextRef;
import de.escidoc.core.resources.om.container.Container;
import de.escidoc.core.resources.om.container.ContainerProperties;

public class ContainerBuilder {

    private static final String ESCIDOC = "escidoc";

    private static final String DC_NAMESPACE = "http://purl.org/dc/elements/1.1/";

    private final Container container = new Container();

    private final MetadataRecords metadataList = new MetadataRecords();

    private final MetadataRecord itemMetadata = new MetadataRecord(ESCIDOC);

    private final ContainerProperties containerProps = new ContainerProperties();

    private final ContextRef contextRef;

    private final ContentModelRef contentModelRef;

    public ContainerBuilder(final ContextRef contextRef, final ContentModelRef contentModelRef) {
        Preconditions.checkNotNull(contextRef, "contextRef is null: %s", contextRef);
        Preconditions.checkNotNull(contentModelRef, "contentModelRef is null: %s", contentModelRef);
        this.contextRef = contextRef;
        this.contentModelRef = contentModelRef;
    }

    public Container build(String containerName) {
        return tryBuildNewContainer(containerName);
    }

    private Container tryBuildNewContainer(String containerName) {
        try {
            setContainerName(containerName);
            setContainerProperties();
            return container;
        }
        catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }

    }

    private void setContainerName(String containerName) throws ParserConfigurationException {
        addDefaultMetadata(createNewDocument(), containerName);
    }

    private Document createNewDocument() throws ParserConfigurationException {
        return DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    }

    private void setContainerProperties() {
        containerProps.setContext(contextRef);
        containerProps.setContentModel(contentModelRef);
        containerProps.setPublicStatus(PublicStatus.RELEASED);
        container.setProperties(containerProps);
    }

    private void addDefaultMetadata(final Document doc, String containerName) {
        buildDefaultMetadata(doc, containerName);
        final MetadataRecords itemMetadataList = new MetadataRecords();
        itemMetadataList.add(itemMetadata);
        container.setMetadataRecords(itemMetadataList);
    }

    private void buildDefaultMetadata(final Document doc, String containerName) {
        itemMetadata.setName(ESCIDOC);
        itemMetadata.setContent(buildContentForItemMetadata(doc, containerName));
    }

    private Element buildContentForItemMetadata(final Document doc, String containerName) {
        Element element = doc.createElementNS(DC_NAMESPACE, "dc");
        final Element titleElmt = doc.createElementNS(DC_NAMESPACE, "title");
        titleElmt.setPrefix("dc");
        titleElmt.setTextContent(containerName);
        element.appendChild(titleElmt);
        return element;
    }

}
