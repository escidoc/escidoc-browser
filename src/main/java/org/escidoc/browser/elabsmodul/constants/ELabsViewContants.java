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
 * Copyright 2011 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.
 * All rights reserved.  Use is subject to license terms.
 */
package org.escidoc.browser.elabsmodul.constants;

import com.vaadin.terminal.Resource;
import com.vaadin.terminal.ThemeResource;

public class ELabsViewContants {

    private ELabsViewContants() {
    }

    /*
     * ### View contants of eLabs Modul ###
     */

    public static final String ADD_STUDY = "Add Study to Context";

    public static final String MODIFY_STUDY = "Edit current Study";

    public static final String DELETE_STUDY = "Delete current Study";

    public static final String ADD_INVESTIGATION = "Add Investigation to Study";

    public static final String MODIFY_INVESTIGATION = "Edit current Investigation";

    public static final String DELETE_INVESTIGATION = "Delete current Investigation";

    public static final String ADD_RIG = "Add New Rig";

    public static final String MODIFY_RIG = "Edit current Rig";

    public static final String DELETE_RIG = "Delete current Rig";

    public static final String ADD_INSTRUMENT = "Add New Instrument";

    public static final String MODIFY_INSTRUMENT = "Edit current Instrument";

    public static final String DELETE_INSTRUMENT = "Delete current Instrument";

    public static final String ESYNC_DEFAULT_URI_VALUE = "http://localhost:9998";

    public static final String DEPOSITOR_DEFAULT_URI_VALUE = "http://localhost:8080/configuration/configuration";

    // eLabs Context View
    // TODO add content

    // eLabs Study View
    public static final String L_STUDY_TITLE = "Title:";

    public static final String L_STUDY_DESC = "Description:";

    public static final String L_STUDY_MOT_PUB = "Motivating Publications:";

    public static final String L_STUDY_RES_PUB = "Resulting Publications:";

    // eLabs Investigation View
    public static final String L_INVESTIGATION_TITLE = "Title:";

    public static final String L_INVESTIGATION_DESC = "Description:";

    public static final String L_INVESTIGATION_DURATION = "Duration (DD | HH | MM):";

    public static final String L_INVESTIGATION_RIG = "Selected Rig:";

    public static final String L_INVESTIGATION_MOT_PUB = "Motivating Publications:";

    public static final String L_INVESTIGATION_INVESTIGATOR = "Investigator:";

    public static final String L_INVESTIGATION_DEPOSIT_SERVICE = "Deposit Service URL:";

    // eLabs Rig View

    public static final String L_RIG_TITLE = "Title:";

    public static final String L_RIG_DESC = "Description:";

    public static final String L_RIG_CONTENT = "Instruments:";

    public static final String RIG_NO_DESCRIPTION_BY_INSTR = "<no description available>";

    // eLabs Instrument View

    public static final String L_INSTRUMENT_TITLE = "Title:";

    public static final String L_INSTRUMENT_DESC = "Description:";

    public static final String L_INSTRUMENT_CONFIGURATION_KEY = "Configuration:";

    public static final String L_INSTRUMENT_CONFIGURATION_VALUE = "This instrument has a configuration";

    public static final String L_INSTRUMENT_CALIBRATION_KEY = "Calibration:";

    public static final String L_INSTRUMENT_CALIBRATION_VALUE = "This instrument requries a calibration";

    public static final String L_INSTRUMENT_ESYNC_DAEMON = "eSync-Daemon:";

    public static final String L_INSTRUMENT_FOLDER = "Folder:";

    public static final String L_INSTRUMENT_FILE_FORMAT = "File Format:";

    public static final String L_INSTRUMENT_DEVICE_SUPERVISOR = "Device Supervisor:";

    public static final String L_INSTRUMENT_INSTITUTE = "Institute / Organizational Unit:";

    public static final String L_INSTRUMENT_ESYNC_DAEMON_HELP =
        "http://hostname:port<br>The hostname of the computer to which the instrument writes data and the port to which the daemon listens.";

    public static final String L_INSTRUMENT_ESYNC_DAEMON_INPUTDATA = "http://hostname:port";

    public static final String L_INSTRUMENT_FOLDER_HELP =
        "The folder to which the instrument writes data. This folder will be monitored by the eSynch-Daemon.";

    // INSRUMENT_PROPERTIES PROPERTIES
    public static final String P_INSTRUMENT_TITLE = "name";

    public static final String P_INSTRUMENT_DESC = "description";

    public static final String P_INSTRUMENT_CREATEON = "createdOn";

    public static final String P_INSTRUMENT_CREATEDBY = "createdBy";

    public static final String P_INSTRUMENT_MODIFIEDON = "modifiedOn";

    public static final String P_INSTRUMENT_CONFIGURATION = "configuration";

    public static final String P_INSTRUMENT_CALIBRATION = "calibration";

    public static final String P_INSTRUMENT_ESYNCDAEMON = "eSyncDaemon";

    public static final String P_INSTRUMENT_FOLDER = "folder";

    public static final String P_INSTRUMENT_FILEFORMAT = "fileFormat";

    public static final String P_INSTRUMENT_CONTEXT = "context";

    public static final String P_INSTRUMENT_CONTENTMODEL = "contentModel";

    public static final String P_INSTRUMENT_DEVICESUPERVISOR = "deviceSupervisor";

    public static final String P_INSTRUMENT_INSTITUTE = "institute";

    public static final String[] INSTRUMENT_PROPERTIES = { P_INSTRUMENT_TITLE, P_INSTRUMENT_DESC,
        P_INSTRUMENT_CREATEON, P_INSTRUMENT_CREATEDBY, P_INSTRUMENT_MODIFIEDON, P_INSTRUMENT_CONFIGURATION,
        P_INSTRUMENT_CALIBRATION, P_INSTRUMENT_ESYNCDAEMON, P_INSTRUMENT_FOLDER, P_INSTRUMENT_FILEFORMAT,
        P_INSTRUMENT_CONTEXT, P_INSTRUMENT_CONTENTMODEL, P_INSTRUMENT_DEVICESUPERVISOR, P_INSTRUMENT_INSTITUTE };

    // Investigation Properties
    public static final String P_INVESTIGATION_TITLE = "name";

    public static final String P_INVESTIGATION_DESC = "description";

    public static final String P_INVESTIGATION_DEPOSIT_SERVICE = "depositEndpoint";

    public static final String P_INVESTIGATION_INVESTIGATOR = "investigator";

    public static final String P_INVESTIGATION_DURATION = "maxRuntime";

    public static final String P_INVESTIGATION_RIG = "rigTitle";

    public static final String[] INVESTIGATION_PROPERTIES = { P_INVESTIGATION_TITLE, P_INVESTIGATION_DESC,
        P_INVESTIGATION_DEPOSIT_SERVICE, P_INVESTIGATION_INVESTIGATOR, P_INVESTIGATION_DURATION, P_INVESTIGATION_RIG };

    // Study Properties
    public static final String P_STUDY_TITLE = "name";

    public static final String P_STUDY_DESC = "description";

    public static final String P_STUDY_MOT_PUB = "motivatingPublication";

    public static final String P_STUDY_RES_PUB = "resultingPublication";

    public static final String[] STUDY_PROPERTIES = { P_STUDY_TITLE, P_STUDY_DESC, P_STUDY_MOT_PUB, P_STUDY_RES_PUB };

    // Rig Properties

    public static final String P_RIG_TITLE = "name";

    public static final String P_RIG_DESC = "description";

    public static final String P_RIG_CONTENT = "contentList";

    public static final String[] RIG_PROPERTIES = { P_RIG_TITLE, P_RIG_DESC, P_RIG_CONTENT };

    // eLabs Header Labels
    public static final String RESOURCE_RIG_NAME = "Rig item: ";

    public static final String RESOURCE_INSTRUMENT_NAME = "Instrument item: ";

    public static final String RESOURCE_GENERATEDITEM_NAME = "Item: ";

    public static final String RESOURCE_STUDY_NAME = "Study container: ";

    public static final String RESOURCE_INVESTIGATION_NAME = "Investigation container: ";

    public static final String RESOURCE_ELABS_CONTEXT_NAME = "Default BW-eLabs context";

    public static final String MAINPANEL_PROPERTIES_LABEL = "Properties";

    // eLabs Buttons
    public static final String BTN_START = "Start";

    public static final String BTN_STOP = "Stop";

    public static final String BTN_SAVE = "Save";

    public static final String BTN_CANCEL = "Cancel";

    public static final String BTN_START_TOOLTIP = "Start operation";

    public static final String BTN_STOP_TOOLTIP = "Stop operation";

    public static final String BTN_SAVE_TOOLTIP = "Save modification";

    public static final String BTN_CANCEL_TOOLTIP = "Cancel modification";

    // eLabs Tab positiions
    public static final int TAB_DETAILS_POSITION = 0;

    public static final int TAB_INVESTIGATION_SERIES_POSITION = 1;

    public static final int TAB_INVESTIGATION_POSITION = 2;

    public static final int TAB_HISTORY_POSITION = 3;

    public static final int TAB_RIGHTS_POSITION = 4;

    public static final int TAB_RESULTS_POSITION = 5;

    //
    public static final String USER_DESCR_ON_HOR_LAYOUT_TO_EDIT = "Please click on the panel to edit the value";

    public static final String USER_DESCR_ON_HOR_LAYOUT_TO_SAVE = "Please click on the panel to save the value";

    public static final String USER_DESCR_ON_FORM_LAYOUT_TO_SAVE = "Please click to save";

    public static final String USER_DESCR_ON_LABEL_TO_EDIT = "Please click on the label to edit the value";

    public static final String USER_DESCR_ON_LABEL_TO_SAVE = "Please click on the label to save the value";

    public static final String USER_DESCR_ON_TEXTFIELD_TO_SAVE_OR_CANCEL =
        "Please click ENTER to save the value or ESC to cancel the edit";

    public static final String HOR_PANEL_HEIGHT = "30px";

    public static final String HOR_LABEL_HEIGHT = "30px";

    public static final String LABEL_WIDTH = "200px";

    public static final String TEXT_WIDTH = "400px";

    public static final String COMBOBOX_WIDTH = "300px";

    public static final String DIV_ALIGN_RIGHT = "<div align=\"right\">";

    public static final String DIV_ALIGN_LEFT = "<div align=\"left\">";

    public static final String DIV_END = "</div>";

    public static final String STYLE_ELABS_FORM = "elabsForm";

    public static final String STYLE_ELABS_HOR_PANEL = "elabsLine";

    public static final String STYLE_ELABS_HOR_PANEL_FOR_TABLE = "elabsLineWithTable";

    public static final String STYLE_ELABS_TEXT = "elabsTextField";

    public static final String STYLE_ELABS_TEXT_AS_LABEL = "elabsLabel";

    public static final String DIALOG_SAVEINSTRUMENT_HEADER = "Saving Instrument";

    public static final String DIALOG_SAVEINSTRUMENT_TEXT = "Are you sure to save this Instrument?";

    public static final String DIALOG_SAVERIG_HEADER = "Saving Rig";

    public static final String DIALOG_SAVERIG_TEXT = "Are you sure to save this Rig?";

    public static final String DIALOG_SAVESTUDY_HEADER = "Saving Study";

    public static final String DIALOG_SAVESTUDY_TEXT = "Are you sure to save this Study?";

    public static final String DIALOG_SAVE_INVESTIGATION_HEADER = "Saving Investigation";

    public static final String DIALOG_SAVE_INVESTIGATION_TEXT = "Are you sure to save this Investigation?";

    public static final String[] INVESTIGATION_SERIES_PROPERTIES = { "name", "description" };

    // ICONs
    public static final Resource ICON_16_OK = new ThemeResource("runo/icons/16/ok.png");

    public static final Resource ICON_16_CANCEL = new ThemeResource("runo/icons/16/cancel.png");

    public static final Resource ICON_16_EMAIL = new ThemeResource("runo/icons/16/email.png");

    public static final Resource ICON_16_EMAIL_REPLY = new ThemeResource("runo/icons/16/email-reply.png");

    public static final Resource ICON_16_HELP = new ThemeResource("runo/icons/16/help.png");

    public static final Resource ICON_16_USER = new ThemeResource("runo/icons/16/user.png");

    public static final Resource ICON_16_USERS = new ThemeResource("runo/icons/16/users.png");

    public static final Resource ICON_16_GLOBE = new ThemeResource("runo/icons/16/globe.png");

    public static final Resource ICON_16_NOTE = new ThemeResource("runo/icons/16/note.png");

    public static final Resource ICON_16_DOC_TXT = new ThemeResource("runo/icons/16/document-txt.png");

    public static final Resource ICON_16_DOC_PDF = new ThemeResource("runo/icons/16/document-pdf.png");

    public static final Resource ICON_16_DOC_PPT = new ThemeResource("runo/icons/16/document-ppt.png");

    public static final Resource ICON_16_DOC_DOC = new ThemeResource("runo/icons/16/document-doc.png");

    public static final Resource ICON_16_DOC_IMG = new ThemeResource("runo/icons/16/document-image.png");

    public static final Resource ICON_16_DOC_WEB = new ThemeResource("runo/icons/16/document-web.png");

    // Additional Caption names
    public static final String BWELABS_STUDY = "BW-eLabs Study";

    public static final String BWELABS_INVSERIES = "BW-eLabs Investigation Series";

    public static final String DIALOG_SAVE_INVESTIGATION_SERIES_HEADER = "Saving Investigation Series";

    public static final String DIALOG_SAVE_INVESTIGATION_SERIES_TEXT =
        "Are you sure to save this Investigation Series?";
}
