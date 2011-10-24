package org.escidoc.browser.elabsmodul.interfaces;

import java.util.List;

import org.escidoc.browser.elabsmodul.model.InstrumentBean;

/**
 * Interface for the rig controller.
 */
public interface IRigAction extends ISaveAction {

    /**
     * Returns with a set of instruments from the infrastructure which are not in the list given by the argument.
     * 
     * @param containedInstruments
     *            list of already stored instrument's id
     * @return new instruments
     */
    List<InstrumentBean> getNewAvailableInstruments(final List<String> containedInstruments);
}
