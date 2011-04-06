package org.escidoc.browser.ui.listeners;

import org.escidoc.browser.ui.WindowDimension;

public interface WindowResizeObserver {

    void setDimension(WindowDimension windowDimension);

    WindowDimension getDimension();

}
