package org.escidoc.browser.elabsmodul.constants;

public class MetaDataConstants {
    public static final String INSTRUMENT =
        "<el:Instrument xmlns:el=\"http://escidoc.org/ontologies/bw-elabs/re#\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">"
            + "<dc:title>FRS Instrument 01</dc:title>"
            + "<dc:description>An example instrument for eLabs development.</dc:description>"
            + "<el:identity-number>0001</el:identity-number>"
            + "<el:requires-configuration>no</el:requires-configuration>"
            + "<el:requires-calibration>no</el:requires-calibration>"
            + "<el:esync-endpoint>http://my.es/ync/endpoint</el:esync-endpoint>"
            + "<el:monitored-folder>C:\\tmp\\monitored0001</el:monitored-folder>"
            + "<el:result-mime-type>application/octet-stream</el:result-mime-type>"
            + "<el:responsible-person rdf:resource=\"escidoc:42\"/>"
            + "<el:institution rdf:resource=\"escidoc:1001\"/>" + "</el:Instrument>";

    public static final String STUDY =
        "<el:Study xmlns:el=\"http://escidoc.org/ontologies/bw-elabs/re#\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">"
            + "<dc:title>Study Default Title</dc:title>"
            + "<dc:description>An example Study for eLabs development.</dc:description>"
            + "<el:motivating-publication rdf:resource=\"http://escidoc.org/some.pdf\"/>"
            + "<el:resulting-publication rdf:resource=\"http://escidoc.org/some-other.pdf\"/>" + "</el:Study>";

    public static final String RIG =
        "<el:Rig xmlns:el=\"http://escidoc.org/ontologies/bw-elabs/re#\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">"
            + "<dc:title>FRS Rig 01</dc:title>"
            + "<dc:description>An example Rig for eLabs development.</dc:description>"
            + "<el:instrument rdf:resource=\"escidoc:16002\"/>"
            + "<el:instrument rdf:resource=\"escidoc:16003\"/>"
            + "</el:Rig>";

    public static final String INVESTIGATIONSERIES =
        "<el:InvestigationSeries xmlns:el=\"http://escidoc.org/ontologies/bw-elabs/re#\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\"     xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">"
            + "<dc:title>Investigation Series 01</dc:title>"
            + "<dc:description>An example Investigation Series for eLab development.</dc:description>"
            + "</el:InvestigationSeries>";

    public static final String INVESTIGATION =
        "<el:Investigation xmlns:el=\"http://escidoc.org/ontologies/bw-elabs/re#\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">"
            + "<dc:title>Investigation 01</dc:title>"
            + "<dc:description>An example Investigation for eLab development.</dc:description>"
            + "<el:max-runtime>84000</el:max-runtime>"
            + "<el:deposit-endpoint>http://my.es/ync/endpoint</el:deposit-endpoint>"
            + "<el:rig rdf:resource=\"escidoc:16004\"/>"
            + "<el:instrument rdf:resource=\"escidoc:16002\">"
            + "<el:monitored-folder>c:\\tmp\\i1</el:monitored-folder>"
            + "</el:instrument>"
            + "<el:instrument rdf:resource=\"escidoc:16003\">"
            + "<el:monitored-folder>c:\\tmp\\i2</el:monitored-folder>" + "</el:instrument>" + "</el:Investigation>";

}
