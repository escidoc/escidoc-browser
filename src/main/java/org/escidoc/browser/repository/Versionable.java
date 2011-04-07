package org.escidoc.browser.repository;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.common.versionhistory.VersionHistory;

public interface Versionable {

    VersionHistory findVersionHistory(String id) throws EscidocClientException;

}
