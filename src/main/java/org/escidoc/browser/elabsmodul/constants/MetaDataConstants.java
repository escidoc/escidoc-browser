package org.escidoc.browser.elabsmodul.constants;

public class MetaDataConstants {
    public static final String INSTRUMENT =
        "<el:Instrument xmlns:el=\"http://escidoc.org/ontologies/bw-elabs/re#\">"
            + "<dc:title xmlns:dc=\"http://purl.org/dc/elements/1.1/\">FRS Instrument 01</dc:title>"
            + "<dc:description xmlns:dc=\"http://purl.org/dc/elements/1.1/\">An example instrument for eLabs development.</dc:description>"
            + "<el:identity-number></el:identity-number>"
            + "<el:requires-configuration>no</el:requires-configuration>"
            + "<el:requires-calibration>no</el:requires-calibration>"
            + "<el:esync-endpoint>http://my.es/ync/endpoint</el:esync-endpoint>"
            + "<el:monitored-folder>C:\\tmp\\monitored0001</el:monitored-folder>"
            + "<el:result-mime-type>FMF</el:result-mime-type>"
            + "<el:responsible-person xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" rdf:resource=\"escidoc:42\"></el:responsible-person>"
            + "<el:institution xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" rdf:resource=\"escidoc:1001\"></el:institution>"
            + "</el:Instrument>";

    public String getVariable(String name) {
        return name;
    }
}
