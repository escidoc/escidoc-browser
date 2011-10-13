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

/**
 * @author ASP
 * 
 */
public final class ELabsConstants {
    public static final String REMOVE = "Remove";

    public static final String AA_LOGOUT_TARGET = "/aa/logout?target=";

    private ELabsConstants() {
    }

    public static final String SIGGIB = "SIGGIB";

    public static final String DEBUG = "DEBUG";

    public static final String BLANK = "";

    public static final String OBJECT_ID = "objectId";

    public static final String IDENTIFIER = "identifier";

    public static final String NAME = "name";

    public static final String ACTION = "action";

    public static final String DESCRIPTION = "description";

    public static final String CREATED_BY = "createdBy";

    public static final String CREATED_ON = "createdOn";

    public static final String MODIFIED_BY = "modifiedBy";

    public static final String MODIFIED_ON = "modifiedOn";

    public static final String CONFIGURATION = "configuration";

    public static final String CALIBRATION = "calibration";

    public static final String ESYNCDAEMON = "eSyncDaemon";

    public static final String DEPOSIT_SERVICE = "depositService";

    public static final String FOLDER = "folder";

    public static final String FILE_FORMAT = "fileFormat";

    public static final String CONTENT_MODEL = "contentModel";

    public static final String DEVICE_SUPERVISOR = "deviceSupervisor";

    public static final String INSTITUTE = "institute";

    public static final String INCARNATION_TYPE = "incarnationType";

    public static final String COST_CENTER = "costCenter";

    public static final String OPERATOR = "operator";

    public static final String[] INSTRUMENT_PROPERTIES = { IDENTIFIER, NAME,
        DESCRIPTION, CREATED_BY, CREATED_ON, MODIFIED_BY, MODIFIED_ON,
        CONFIGURATION, CALIBRATION, ESYNCDAEMON, FOLDER, FILE_FORMAT,
        DEVICE_SUPERVISOR, INSTITUTE, INCARNATION_TYPE, OPERATOR, COST_CENTER };

    public static final String APPROVED_BY = "approvedBy";

    public static final String APPROVED_ON = "approvedOn";

    public static final String EXPECTED_RUNTIME = "expectedRuntime";

    public static final String SETUP_TIME_DAYS = "setupTimeDays";

    public static final String SETUP_TIME_HOURS = "setupTimeHours";

    public static final String SETUP_TIME_MINUTES = "setupTimeMinutes";

    public static final String OPERATOR_PRESENT = "operatorPresent";

    public static final String INSTRUMENTS = "instruments";

    public static String[] RIG_PROPERTIES = { OBJECT_ID, NAME, DESCRIPTION,
        CREATED_BY, CREATED_ON, MODIFIED_BY, MODIFIED_ON, APPROVED_BY,
        APPROVED_ON, /* EXPECTED_RUNTIME, SETUP_TIME_DAYS, */
        SETUP_TIME_HOURS, SETUP_TIME_MINUTES, OPERATOR_PRESENT, INSTRUMENTS };

    public static final String LOCALE = "local";

    public static final String REMOTE = "remote";

    public static final String VIRTUAL = "virtual";

    public static final String STATUS = "status";

    public static final String START_DATE = "startDate";

    public static final String DURATION = "duration";

    public static final String MOTIVATING_PUBLICATIONS =
        "motivatingPublications";

    public static final String RESULTING_PUBLICATIONS = "resultingPublications";

    public static final String INVESTIGATOR = "investigator";

    public static final String[] STUDIES_PROPERTIES = { OBJECT_ID, NAME,
        DESCRIPTION, CREATED_BY, CREATED_ON, MODIFIED_BY, MODIFIED_ON,
        /* STATUS, START_DATE, DURATION, */MOTIVATING_PUBLICATIONS,
        RESULTING_PUBLICATIONS,
    /* INVESTIGATORS, DEPOSIT_SERVICE */};

    public static final String EXECUTION_PLAN = "executionPlan";

    public static final String RIG_ID = "rigId";

    public static final String DURATION_DAYS = "durationDay";

    public static final String DURATION_HOURS = "durationHour";

    public static final String DURATION_MINUTES = "durationMinute";

    public static final String[] INVESTIGATION_PROPERTIES = { OBJECT_ID, NAME,
        DESCRIPTION, DURATION_DAYS, DURATION_HOURS, DURATION_MINUTES,
        CREATED_BY, CREATED_ON, MODIFIED_BY, MODIFIED_ON, /* STATUS, */
        MOTIVATING_PUBLICATIONS, INVESTIGATOR, EXECUTION_PLAN, RIG_ID,
        DEPOSIT_SERVICE };

    public static final String[] INVESTIGATIONSERIES_PROPERTIES = { OBJECT_ID,
        NAME, DESCRIPTION, };

    // public static final Action INVESTIGATION_SERIES_ACTION = new Action(
    // "Add item to investigation series");

    // public static final Action[] INVESTIGATION_SERIES_ACTIONS =
    // new Action[] { INVESTIGATION_SERIES_ACTION };

    public static final String ESCIDOC_SERVICE_ROOT_URI =
        "eSciDoc_Service_Root_URI";

    public static final String SAMPLES = "Samples";

    // public static final String[] EXECUTION_PLAN_PROPERTIES = { SAMPLES };
    public static final String[] EXECUTION_PLAN_PROPERTIES = { NAME,
        DESCRIPTION };

    public static final String DEFAULT_PROPERTIES = "default.properties";

    public static final String CONTENT_MODEL_PROPERTIES =
        "ContentModel.properties";

    public static final String ESCIDOC = "escidoc";

    // /////////////////
    // Content Model
    public static final String ABSORPTION_SPECTRUM_CONTENT_MODEL =
        "Absorption Spectrum";

    public static final String CALIBRATION_CONTENT_MODEL = "Calibration";

    public static final String PL_SPECTRUM_CONTENT_MODEL = "PL-Spectrum";

    public static final String ACTUAL_DEVELOPING_CONTENT_MODEL =
        "Actual Developing";

    public static final String CONFIGURATION_CONTENT_MODEL = "Configuration";

    public static final String INVESTIGATION_CONTENT_MODEL = "Investigation";

    public static final String INVESTIGATIONSERIES_CONTENT_MODEL =
        "InvestigationSeries";

    public static final String RECONSTRUCTION_CONTENT_MODEL = "Reconstruction";

    public static final String ALGORITHM_CONTENT_MODEL = "Algorithm";

    public static final String RIG_CONTENT_MODEL = "Rig";

    public static final String INSTRUMENT_CONTENT_MODEL = "Instrument";

    public static final String STUDY_CONTENT_MODEL = "Study";

    public static final String HOLOGRAPHY_IMAGE_CONTENT_MODEL =
        "Holography Image";

    public static final String EXECUTION_PLAN_CONTENT_MODEL = "Execution Plan";

    public static final String LOGOUT_LABEL = "Logout";

    public static final String APP_TITLE = "BW-eLabs Solution";

    public static final String SPLASH_TITLE = "BW-eLabs Version 0.3";

    public static final String DEFAULT_CONTEXT = "DefaultContext";

    public static final String E_SCI_DOC_USER_HANDLE = "eSciDocUserHandle";

    public static final String IMAGE_CONTENT_MODEL = "Image";

    public static final String SAMPLE_CONTENT_MODEL = "Sample";

    public static final Resource DEVICE_ICON = new ThemeResource(
        "icons/32/Device.jpg");

    public static final Resource STUDY_ICON = new ThemeResource(
        "icons/32/StudyIcon.png");

    public static final Resource RIGS_ICON = new ThemeResource(
        "icons/32/Rigs.png");

    public static final Resource EXECUTE_ICON = new ThemeResource(
        "icons/32/Execute.png");

    public static final Resource SEARCH_ICON = new ThemeResource(
        "icons/32/maginfier.png");

    public static final Resource SEARCH_ICON_SMALL = new ThemeResource(
        "icons/32/All_Search_LensStart_15_Hover.gif");

    public static final Resource DEBUG_ICON = new ThemeResource(
        "icons/32/debug.gif");

    public static final Resource SETUP_ICON = new ThemeResource(
        "icons/32/32x32-pege_setup.png");

    public static final Resource DETAILS_ICON = new ThemeResource(
        "icons/32/maginfier.png");

    public static final Resource RIGHTS_ICON = new ThemeResource(
        "icons/32/key.png");

    public static final Resource HISTORY_ICON = new ThemeResource(
        "icons/32/history.png");

    public static final ThemeResource RESULT_ICON = new ThemeResource(
        "icons/32/result.jpg");

    public static final ThemeResource LOGOUT_ICON = new ThemeResource(
        "icons/32/Log-Out-icon.png");

    public static final String DETAILS = "Details";

    public static final String HISTORY = "History";

    public static final String RIGHTS = "Rights";

    public static final String RESULTS = "Results";

    public static final String NAVIGATION_HEIGHT = "500px";

    public static final String ADD = "Add";

    public static final String EDIT = "Edit";

    public static final String DELETE = "Delete";

    public static final String INSTRUMENTS_LABEL = "Instruments";

    // //////////////////////////
    // Config constants.
    public static final String CONFIGURATION_ID = "ConfigurationID";

    public static final String USER_HANDLE = "UserHandle";

    public static final String CHECK_SUM_TYPE = "CheckSumType";

    public static final String WORKSPACE_ID = "WorkspaceID";

    public static final String MD5 = "MD5";

    public static final String EXPERIMENT_ID = "ExperimentID";

    public static final String EXPERIMENT_NAME = "ExperimentName";

    public static final String EXPERIMENT_DESCRIPTION = "ExperimentDescription";

    public static final String CONTENT_MODEL_ID = "ContentModelID";

    public static final String USER_EMAIL_ADDRESS = "UserEMailAddress";

    public static final String DEPOSIT_SERVER_ENDPOINT =
        "DepositServerEndpoint";

    public static final String INFRASTRUCTURE_ENDPOINT =
        "InfrastructureEndpoint";

    public static final String E_SYNC_DAEMON_ENDPOINT = "eSyncDaemonEndpoint";

    public static final String MONITORED_FOLDER = "MonitoredFolder";

    public static final String MONITORING_START_TIME = "MonitoringStartTime";

    public static final String MONITORING_DURATION = "MonitoringDuration";

    public static final String UNEXPECTED_ERROR_OCCURRED =
        "An unexpected error occurred! See log for details.";

    public static final String AND = " and ";

    public static final boolean SORT_ASCENDING = true;

    public static final boolean SORT_DESCENDING = false;

    public static final String TITLE = "Title:";

    public static final String E_SYNC_DAEMON = "eSync-Daemon:";

    public static final String DEPOSIT_SERVICE_LABEL = "Deposit Service:";

    public static final String FOLDER_LABEL = "Folder:";

    public static final String SAVE = "Save";

    public static final String CANCEL = "Cancel";

    public static final String VIEW_HISTORY = "View History";

    public static final String OK = "Ok";

    public static final String AA_LOGIN_TARGET = "/aa/login?target=";

    public static final String RIG_TYPE = "rigType";

    public static final String ENTRY_POINT_ID = "epoid";

    public static final String FAKE_EMAIL = "nobody.fool@example.org";

    public static final Resource ADD_ICON = new ThemeResource(
        "icons/32/All_Complete_PlusAdd_15_Active.gif");

    public static final Resource DELETE_ICON = new ThemeResource(
        "icons/32/All_Complete_MinusRemove_15_Active.gif");

    public static final String ODD_LINE = "background: #FFFFFF";

    public static final String EVEN_LINE = "background: #F1F5FA";

    public static int PAGE_LIMIT = 20;

    public static final String BREAD_CRUMB_STUDY = "Studies Overview";

    public static final String BREAD_CRUMB_INVESTIGATION =
        "Investigation Overview";

    public static final String BREAD_CRUMB_INVESTIGATION_SERIES =
        "Investigation Series Overview";

    public static final String BREAD_CRUMB_INSTRUMENT = "Instrument Overview";

    public static final String BREAD_CRUMB_RIG = "Rig Overview";

    public static final String BREAD_CRUMB_EXECUTION_PATH =
        "Execution Path Overview";

    public static final String BREAD_CRUMB_SETUP = "Setup Overview";

    public static final String BREAD_CRUMB_SEPERATOR = "|";

    public static final String LAST_MODIFICATION_DATE =
        "Last Modification Date";

    public static final String CREATED_DOCUMENT = "Created Document";

    public static final String ACTION_LABEL = "Action";

    public static final String BY = "By";

    public static final String EVENT_DATE = "Event Date";

    public static final String COULD_NOT_UPDATE_ERROR =
        "Unfortunately we could not update the object in eSciDoc!";

    public static final String E_SCI_DOC_STORAGE_ERROR =
        "eSciDoc Storage Error!";

    public static final String COULD_NOT_SAVE_ERROR =
        "Unfortunately we could not save the object in eSciDoc!";

    public static final int SPLIT_POSITION = 270;

    // eLabs Constants
    // TODO will be deleted
    public static final String ELABS_DEFAULT_CONTEXT_ID = "escidoc:12004";

    public static final String ELABS_DEFAULT_STUDY_CMODEL_ID = "escidoc:15001";

    public static final String ELABS_DEFAULT_INVESTIGATION_CMODEL_ID =
        "escidoc:15002";

    public static final String ELABS_DEFAULT_INSTR_CMODEL_ID = "escidoc:15003";

    public static final String ELABS_DEFAULT_RIG_CMODEL_ID = "escidoc:15004";

    public static final String ELABS_DEFAULT_GENERATED_ITEM_CMODEL_ID =
        "escidoc:xxxx";

    // eLabs Item's classname

    public static final String STUDY_CLASSNAME = "Study";

    public static final String INVESTIGATION_CLASSNAME = "Investigation";

    public static final String RIG_CLASSNAME = "Rig";

    public static final String INSTRUMENT_CLASSNAME = "Instrument";

    public static final String GEN_ITEM_CLASSNAME = "GeneratedItem";

}
