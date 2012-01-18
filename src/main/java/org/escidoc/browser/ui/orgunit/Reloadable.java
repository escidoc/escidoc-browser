package org.escidoc.browser.ui.orgunit;

import de.escidoc.core.client.exceptions.EscidocClientException;

public interface Reloadable {

    void reload() throws EscidocClientException;

}
