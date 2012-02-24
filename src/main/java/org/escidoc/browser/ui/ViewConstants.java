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
package org.escidoc.browser.ui;

public class ViewConstants {

    // Footer
    public static final String VERSION = "0.4.1-SNAPSHOT";

    public static final String PRODUCT_NAME = " eSciDoc Browser ";

    public static final String ADMIN_TOOL = "Admin Tool";

    public static final String CHANGE = "Switch Instance of eSciDoc";

    //
    public static final String BREAD_CRUMP_CONTENT =
        "<ul id='crumbs'><li><a href='#'>Home</a></li><li><a href='#'>Main section</a></li><li><a href='#'>Sub section</a></li><li><a href='#'>Sub sub section</a></li><li>The page you are on right now</li></ul>";

    public static final String THEME_NAME = "myTheme";

    public static final String MAIN_WINDOW_TITLE = "eSciDoc Browser";

    public static final String ERROR = "Error";

    public static final String LOGIN = "Login";

    public static final String LOGOUT = "Logout";

    public static final String HEADER = "header";

    public static final String START_LABEL = "Start";

    public static final String LOGIN_WINDOW_WIDTH = "430px";

    public static final String WELCOMING_MESSAGE = "Welcome to esciDoc";

    public static final String OK_LABEL = "Ok";

    public static final String ESCIDOC_URI_TEXTFIELD = "eSciDoc URI";

    public static final String HTTP = "http://";

    public static final String ESCIDOC_URI_CAN_NOT_BE_EMPTY = "eSciDoc URI can not be empty.";

    public static final String SEARCH = "Search";

    public static final String GUEST = "Guest";

    public static final String CURRENT_USER = "User: ";

    public static final String EMPTY_STRING = "";

    public static final String DELETE_RESOURCE = "Delete Resource";

    public static final String ADD_CONTEXT = "Add Context";

    public static final String DELETE = "Delete";

    public static final String DELETED = "Resource was deleted succesfully!";

    public static final String CREATE = "Create";

    public static final String CREATE_CONTAINER = "Create Container";

    public static final String CREATE_RESOURCE = "Create Resource";

    public static final String EDIT_METADATA = "Edit MetaData";

    // ResourceAddViewImpl ContainerAddView
    public static final String CONTAINER_NAME = "Container name";

    public static final String RESOURCE_NAME_GENERIC = "Resource Name ";

    public static final String PLEASE_ENTER_A_CONTAINER_NAME = "Please enter a Container Name";

    public static final String PLEASE_ENTER_A_RESOURCE_NAME = "Please enter a Resource Name";

    public static final String PLEASE_SELECT_CONTENT_MODEL = "Please select Content Model";

    public static final String PLEASE_SELECT_RESOURCE_TOCREATE = "Please select the resource type";

    public static final String ERROR_RETRIEVING_CONTENTMODEL = "Could not retrieve the content model ";

    public static final String PLEASE_ENTER_A_NAME = "Could not retrieve the content model ";

    public static final String RESOURCE_LENGTH = "Resource Name must be 3-25 characters";

    public static final String ERROR_NO_RESOURCETYPE_IN_CONTENTMODEL =
        "Could not find a definition for the resource type in Content Model \"";

    public static final String COULD_NOT_LOAD_CONSTANTS_METADATA_CLASS =
        "Could not load the class containing the Metadata Constants for this module";

    public static final String ITEM_NAME = "Item name";

    public static final String PLEASE_ENTER_AN_ITEM_NAME = "Item Name must be 3-25 characters";;

    public static final String ITEM_NAME_MUST_BE_3_25_CHARACTERS = "Item Name must be 3-25 characters";

    public static final String CREATE_ITEM = "Create Item";

    public static final String ADD_COMPONENT = "Add Component";

    public static final String ADD_ITEM = "Add Item";

    public static final String ADD_CONTAINER = "Add Container";

    public static final String ADD_RESOURCE = "Add Resource";

    public static final String DELETE_CONTAINER = "Delete Container";

    public static final String DELETE_CONTEXT = "Delete Context";

    public static final String CREATED_BY = "Created by";

    public static final String LAST_MODIFIED_BY = "Last modification by";

    public static final String RESOURCE_NAME = "Item: ";

    public static final String CREATED_ON = "Created on ";

    public static final String NOT_AUTHORIZED = "Not Authorized";

    public static final String LOCKED = "Resource was successfully locked";

    public static final String UNLOCKED = "Resource was successfully unlocked";

    public static final String SUBMITTED = "Public Status for the resource was changed to Submitted";

    public static final String IN_REVISION = "Public Status for the resource was changed to In-Revision";

    public static final String RELEASED = "Public Status for the resource was changed to Released";

    public static final String WITHDRAWN = "Public Status for the resource was changed to Withdrawn";

    public static final String SELECT_FILE = "Select file";

    public static final String XML_IS_NOT_WELL_FORMED = "XML is NOT well formed.";

    public static final String XML_IS_WELL_FORMED = "XML is well formed.";

    public static final String ADD_ITEM_S_METADATA = "Add Item's Metadata";

    public static final String ADD_CONTAINER_S_METADATA = "Add Container's Metadata";

    public static final String METADATA = "Metadata";

    public static final String COMPONENTS = "Components";

    public static final String UPLOAD_A_WELLFORMED_XML_FILE_TO_REPLACE_METADATA =
        "Upload a wellformed XML file to replace metadata!";

    public static final String ORGANIZATIONAL_UNIT = "Organizational Unit";

    public static final String ADMIN_DESCRIPTION = "Admin Description";

    public static final String RELATIONS = "Relations";

    public static final String ADDITIONAL_RESOURCES = "Additional Resources";

    // Router
    public static final String LAYOUT_ERR_CANNOT_FIND_CLASS = "Could not load the class name for the layout ";

    public static final String LAYOUT_ERR_INSTANTIATE_CLASS = "Could not create an instance out of the layout class ";

    public static final String LAYOUT_ERR_ILLEG_EXEP = "Could not load the layout because of an Illegal Exception ";

    public static final String CONTROLLER_ERR_CANNOT_FIND_CLASS = "Could not load the class name for the controller ";

    public static final String CONTROLLER_ERR_INSTANTIATE_CLASS =
        "Could not create an instance out of the controller class ";

    public static final String CONTROLLER_ERR_ILLEG_EXEP =
        "Could not load the controller because of an Illegal Exception ";

    public static final String CONTROLLER_ERR_SECU_EXEP =
        "Could not load the controller because of an Security Exception ";

    public static final String CONTROLLER_ERR_INVOKE_EXEP =
        "Could not load the controller because of an Invocation Exception ";

    public static final String CONTROLLER_ERR_NOSUCHMETH_EXEP =
        "Could not load the controller because no such method exists ";

    public static final String LAYOUT_ERR_CANNOT_LOAD_CLASS = "Could not load Layout Name from the properties ";

    // /End Router

    public static final String CLOSE_ALL_OPEN_TABS = "Close all open Tabs";

    public static final String COULD_NOT_RETRIEVE_APPLICATION_URL = "Could not retrieve application URL";

    public static final String PERMANENT_LINK = "Permanent Link";

    // DirectMember

    public static final String CANNOT_CREATE_BUTTONS = "CANNOT CREATE BUTTONS FOR THIS VIEW";

    public static final String VIEW_ERROR_CANNOT_LOAD_VIEW = "Not able to load the view for this resource: ";

    public static final String RESOURCES = "Resources";

    public static final String TOOLS = "Tools";

    public static final String ORGANIZATIONAL_UNITS = "Organizational Units";

    public static final String USERS = "Users";

    public static final String REINDEX = "Reindex";

    public static final String REPOSITORY_INFORMATION = "Repository Information";

    public static final String LOAD_EXAMPLE = "Load Example";

    public static final String CREATE_RESOURCES = "Create Resources";

    public static final String LOAD_EXAMPLE_TEXT = "<p>Loads a set of example objects into the framework.</p>";

    public static final String CLEAR_INDEX = "Clear Index?";

    public static final String INDEX_NAME = "Index Name";

    public static final String REINDEX_TEXT =
        "Reinitialize the search index. The initialization runs asynchronously and returns some useful information to the user, e.g. the total number of objects found.";

    public static final String SEARCH_RESULTS = "Search Results";

    public static final String USER_ACCOUNTS = "User Accounts";

    public static final String BULK_TASKS = "Bulk Tasks";

    public static final String FILTERING_RESOURCES_TITLE = "Filter and Purge Resources";

    public static final String FILTER_DESCRIPTION_TEXT =
        "Filters work on all resources, independent from their status in object lifecycle. Access policies are evaluated for each resource in the answer set. The answer set will only contain those resources the user has access to. Similar to searches, filter methods are based on the SRU standard, so queries are formulated in CQL. Filter methods are not provided by a dedicated service. Instead, they belong to the APIs of the respective resource services. They always retrieve resources of the same type, i.e. the filter method of the Item service will always retrieve Item representations exclusively.(An exception to this rule are the retrieveMembers() methods in the Context and Container service: the result list may contain both Items and Containers.)";

    public static final String FILTER_LABEL = "Filter";

    public static final String PLEASE_SELECT_A_RESOURCE_TYPE = "Please select a resource type";

    public static final String EXAMPLE_QUERY = "\"/properties/created-by/id\"=escidoc:exuser1";

    public static final String FILTER_EXAMPLE_TOOLTIP_TEXT =
        "<h2>Query examples: </h2>"
            + "<ul>"
            + "  <li>\"/id\"=escidoc:5 <br/> filter selected resource type with the id escidoc:5</li>"
            + "  <li>\"/properties/public-status\"= \"released\" <br/> filter selected resource type which are released</li>"
            + "<li>" + EXAMPLE_QUERY
            + "<br/> filter selected resource type that created by user account with the id exuser1</li>" + "</ul>";

    public static final String TIP = "Hint";

    public static final String NO_RESULT = "No Result";

    public static final String PURGE = "Purge";

    public static final String INFO = "Info";

    public static final String FILTERED_RESOURCES = "Filtered Resources";

    public static final String NAME = "Name";

    public static final String ID = "Id";

    public static final String TYPE = "Type";

    public static final String IMPORT_CONTENT_MODEL = "Import Content Model Set";

    public static final String URL = "URL: ";

    public static final String IMPORT = "Import";

    public static final String EXPORT = "Export";

    public static final String FILTER = "Filter";

    public static final String DIRECT_MEMBERS = "Direct Members";

    /* ItemDeleteConfirmation */
    public static final String DELETE_RESOURCE_WINDOW_NAME = "Do you really want to delete this resource!?";

    public static final String QUESTION_DELETE_RESOURCE = "Are you confident to delete this resource!?";

    public static final String ERR_BELONGS_TO_NONDELETABLE_PARENT =
        "Cannot remove the resource as it belongs to a resource which is not deletable";

    public static final String DEFAULT_CONTENT_MODEL_URI =
        "https://www.escidoc.org/smw/images/5/5c/ESciDoc-Generic-Content-Models.zip";

    public static final String PURGE_WARNING_MESSAGE =
        "Purging resources can cause inconsitencies in the repository. Please use delete instead of purge. Continue with purging?";

    public static final String WARNING = "Warning";

    public static final String YES = "Yes";

    public static final String NO = "No";

    public static final String OK = "Ok";

    public static final String ERROR_CREATING_RESOURCE = "Error creating resource";

    public static final String EDIT_PROFILE = "Edit personal profile";

    public static final String ORG_UNITS = "Organizational Units";

    public static final String ERROR_UPDATING_USER = "Error updating user profile";

    public static final String ERROR_CREATING_USER_PREFERENCE = "Error creating user profile preference";

    // Container
    public static final String DESC_LOCKSTATUS = "lockstatus";

    public static final String ADD_CHILD = "Add Child";

    public static final String DELETE_RESOURCE_CONFIRMATION = "Are you confident to delete this resource!?";

    public static final String DESC_STATUS = "status";

    public static final String DESC_HEADER = "header";

    public static final String SUBWINDOW_EDIT = "Add Comment to the Edit operation";

    public static final String FULLWIDHT_STYLE_NAME = "fullwidth";

    public static final String RESOURCE_NAME_CONTAINER = "Container: ";

    // Context
    public static final String RESOURCE_NAME_CONTEXT = "Workspace: ";

    public static final String CONTEXT_TYPE = "Context type is ";

    // ActionHandlerImpl
    public static final String CANNOT_REMOVE_CONTEXT_NOT_IN_STATUS_CREATED =
        "Cannot remove this context since it is not in status created";

    // UserPreferencesTable
    public static final String THE_PREFERENCE_REMOVED =
        "The preference was removed successfully from this users profile";

    public static final String PREFERENCE_REMOVE = "Preference removed";

    public static final String ADMINDESCRIPTION_REMOVE = "AdminDescription removed";

    public static final String ADMINDESCRIPTION_REMOVED =
        "The admin description was removed successfully from this context";

    public static final String MD_REMOVE = "Metadata removed";

    public static final String MD_REMOVED = "The metadata was removed successfully";

    public static final String ADDED_SUCCESSFULLY = "The resource was added successfully";

    public static final String CONTENT_MODELS = "Content Models";

    public static final String ADD_NEW_META_DATA = "Add New MetaData";

    public static final String UPLOAD_A_WELLFORMED_XML_FILE_TO_CREATE_METADATA =
        "Upload a wellformed XML file to create metadata!";

    public static final String ADD_ORGANIZATIONAL_UNIT_S_METADATA = "Add Organizational Unit's Metadata";

    public static final String PARENTS = "Parents";

    public static final String FLOAT_RIGHT = "floatright";

    public static final String ATTRIBUTE_REMOVED = "Attribute Removed";

    public static final String ATTRIBUTE_REMOVED_MESSAGE =
        "The attribute was removed successfully from this users profile";

    public static final String CANCEL = "Cancel";

    public static final String SAVE = "Save";

    public static final String DELETE_CONTENT_MODEL = "Delete Content Model";

    public static final String DELETE_USER_ACCOUNT = "Delete User Account";

    public static final String CLOSE = "Close";

    public static final String ADD = "Add";

    public static final String PROPERTY_NAME = "name";

    public static final String PROPERTY_VALUE = "value";

    public static final String PROPERTY_LINK = "link";

    public static final Object SYSADMIN = "sysadmin";

}
