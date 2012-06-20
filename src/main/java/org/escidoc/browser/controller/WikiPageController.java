package org.escidoc.browser.controller;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.escidoc.browser.model.ItemProxy;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.model.internal.ItemProxyImpl;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.view.WikiPageView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import cylon.creole.CreoleParser;
import cylon.creole.DefaultCreoleParser;
import cylon.html.HtmlRenderer;
import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.common.MetadataRecord;
import de.escidoc.core.resources.om.item.Item;

/**
 * 
 * @author ajb <br />
 *         org.escidoc.browser.Controller=org.escidoc.browser.WikiPage;
 *         http://www.w3.org/1999/02/22-rdf-syntax-ns#type=org.escidoc.resources.Item;
 */
public class WikiPageController extends ItemController {
    private static final Logger LOG = LoggerFactory.getLogger(WikiPageController.class);

    public ItemProxy itemProxy;

    private String WIKIPAGEMD = "wikiArticle";

    private static String URI_DC = "http://purl.org/dc/elements/1.1/";

    public WikiPageController(Repositories repositories, Router router, ResourceProxy resourceProxy) {
        super(repositories, router, resourceProxy);
        createView();
    }

    @Override
    public void createView() {
        view = new WikiPageView(getRouter(), getResourceProxy(), this);
    }

    public boolean hasWikiContent() {
        try {
            Element element = ((ItemProxyImpl) resourceProxy).getMetadataRecords().get(WIKIPAGEMD).getContent();
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    /**
     * Get and return the elements of an MD Wiki Element
     * 
     * @return
     */
    public String[] getWikiPageContent() {
        if (hasWikiContent()) {
            String[] wikiContent = new String[2];
            getWikiElements(wikiContent);
            return wikiContent;
        }
        else {
            String[] notFound = { "no title", "Empty Content" };
            return notFound;
        }
    }

    /**
     * Getting the title & description from the MD element
     * 
     * @param wikiContent
     */
    private void getWikiElements(String[] wikiContent) {
        Element element = ((ItemProxyImpl) resourceProxy).getMetadataRecords().get(WIKIPAGEMD).getContent();
        if (element != null && element.getChildNodes() != null) {
            final NodeList nodeList = element.getChildNodes();
            LOG.debug(element.getTextContent() + nodeList.getLength());
            for (int i = 0; i < nodeList.getLength(); i++) {
                final Node node = nodeList.item(i);
                final String nodeName = node.getLocalName();
                final String nsUri = node.getNamespaceURI();

                if (nodeName == null || nodeName.equals("")) {
                    continue;
                }
                if ("title".equals(nodeName) && URI_DC.equals(nsUri)) {
                    wikiContent[0] = node.getTextContent();
                }
                if ("description".equals(nodeName) && URI_DC.equals(nsUri)) {
                    wikiContent[1] = node.getTextContent();
                }
            }
        }
    }

    public void createWikiContent(String title, String content) {
        System.err.println("Hell-o");
        if (hasWikiContent()) {
            try {
                MetadataRecord metadataRecord = ((ItemProxyImpl) resourceProxy).getMetadataRecords().get(WIKIPAGEMD);
                final Element metaDataContent = createWikiDOMElement(title, content);
                metadataRecord.setContent(metaDataContent);
                updateMetadata(metadataRecord);
                showTrayMessage("Updated!", "Content was updated successfully!");

            }
            catch (EscidocClientException e) {
                LOG.error(e.getLocalizedMessage());
                showError(e.getLocalizedMessage());
            }
        }
        else {
            MetadataRecord md = new MetadataRecord(WIKIPAGEMD);
            md.setContent(createWikiDOMElement(title, content));
            Item item;
            try {
                item = repositories.item().findItemById(resourceProxy.getId());
                repositories.item().addMetaData(md, item);
                showTrayMessage("Inserted!", "Content was inserted successfully!");
            }
            catch (EscidocClientException e) {
                LOG.error(e.getLocalizedMessage());
                e.printStackTrace();
            }

        }
    }

    public Element createWikiDOMElement(String wikiTitle, String wikiContent) {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setCoalescing(true);
        factory.setValidating(true);
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            final Document doc = builder.newDocument();
            final Element wikiElement = doc.createElementNS("https://www.escidoc.org/ontologies/wiki#", "WikiArticle");

            final Element title = doc.createElementNS(URI_DC, "title");
            title.setPrefix("dc");
            title.setTextContent(wikiTitle);
            wikiElement.appendChild(title);

            final Element description = doc.createElementNS(URI_DC, "description");
            description.setPrefix("dc");
            description.setTextContent(wikiContent);
            wikiElement.appendChild(description);

            return wikiElement;
        }
        catch (DOMException e) {
            LOG.error(e.getLocalizedMessage());
        }
        catch (ParserConfigurationException e) {
            LOG.error(e.getLocalizedMessage());
        }
        return null;
    }

    public String parseCreole(String content) {
        // Parsing with JCreole
        // try {
        // CreoleParser parser = new CreoleParser();
        // parser.setPrivileges(EnumSet
        // .of(JCreolePrivilege.ENUMFORMAT, JCreolePrivilege.TOC, JCreolePrivilege.RAWHTML));
        // /*
        // * Replace the statement above with something like this to test privileges: parser.setPrivileges(EnumSet.of(
        // * JCreolePrivilege.ENUMFORMATS, JCreolePrivilege.TOC, JCreolePrivilege.RAWHTML,
        // * JCreolePrivilege.STYLESHEET, JCreolePrivilege.JCXBLOCK, JCreolePrivilege.JCXSPAN, JCreolePrivilege.STYLER
        // * ));
        // */
        //
        // Object retVal = parser.parse(CreoleScanner.newCreoleScanner(new StringBuilder(content), false, null));
        // content = retVal.toString();
        // }
        // catch (Exception e) {
        // LOG.debug("Error Parsing " + e.getLocalizedMessage());
        // }
        // return content;

        CreoleParser parser = new DefaultCreoleParser();
        cylon.dom.Document document = parser.document(content);
        HtmlRenderer renderer = new HtmlRenderer(true);
        document.accept(renderer);
        System.out.println(renderer.getResult());
        return renderer.getResult();

    }

}
