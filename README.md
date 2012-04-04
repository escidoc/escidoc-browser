# eSciDoc Browser - An eSciDoc.org Solution

The eSciDoc Browser is a Rich Internet Application that allows browsing of digital assets which are stored in an eSciDoc Core infrastructure. It also provides means to create and manipulate content resources.  
== Installation ==

* Copy the file browser.war into a Java web container, such as, Apache Tomcat.
* Configure the web container and JAVA_OPTS to use your local proxy settings. Please refer to your web container documentation how to configure the proxy settings. For example: <pre>JAVA_OPTS=-Dhttp.proxyHost=proxy.fiz-karlsruhe.de -Dhttp.proxyPort=8888 -Dhttp.nonProxyHosts=escidev6|escidev4|localhost|127.0.0.1|141.66.11.*|*.fiz-karlsruhe.de|www.escidoc.org|www.escidoc.de|escidev6.fiz-karlsruhe.de -Xmx1500m -XX:MaxPermSize=750m</pre>
* Start the web container.
* Open eSciDoc Browser in Webbrowser, log in as user ''sysadmin'', at left bottom click on ''Tools'', click on ''Bulk Tasks'', and ingest Content Models from https://www.escidoc.org/smw/images/5/5c/ESciDoc-Generic-Content-Models.zip

== Configuration ==
There is no configuration file for the eSciDoc Browser. All configuration is done via eSciDoc Resources.
Any functionality of the eSciDoc Browser is based on the eSciDoc Infrastructure. So, it must be connected to an eSciDoc Infrastructure for usage. 

The eSciDoc Browser can be installed independently from an eSciDoc Infrastructure but an eSciDoc Infrastructure must be accessible via HTTP. Opening the eSciDoc Browser in a web-browser a text field will appear where the URL of an eSciDoc Infrastructure must be entered. Confirming the URL the eSciDoc Browser connects to the specified eSciDoc Infrastructure using the base-url that is configured for that eSciDoc Infrastructure. The URL can be given as URL parameter ''escidocurl'' when accessing the eSciDoc Browser. If so, the text field will not appear.

Behavior and appearance of the eSciDoc Browser may be configured via Context and Content Model resources inside the eSciDoc Infrastructure. See [[#eSciDoc Content Models in eSciDoc Browser]] and [[#eSciDoc Contexts in eSciDoc Browser]].

## Official Wiki 
"eSciDoc Browser Wiki, " https://www.escidoc.org/wiki/ESciDoc_Browser

## Issue Management: eSciDoc Browser 
https://www.escidoc.org/jira/browse/GENCLIENT
