package org.escidoc.browser.elabsmodul.constants;

public class MetaDataConstants {
    public static final String INSTRUMENT =
        "<el:Instrument xmlns:el=\"http://escidoc.org/ontologies/bw-elabs/re#\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">"
            + "<dc:title>FRS Instrument 01</dc:title>"
            + "<dc:description></dc:description>"
            + "<el:identity-number></el:identity-number>"
            + "<el:requires-configuration></el:requires-configuration>"
            + "<el:requires-calibration></el:requires-calibration>"
            + "<el:esync-endpoint></el:esync-endpoint>"
            + "<el:monitored-folder></el:monitored-folder>"
            + "<el:result-mime-type>application/octet-stream</el:result-mime-type>"
            + "<el:responsible-person rdf:resource=\"\"/>" + "<el:institution rdf:resource=\"\"/>" + "</el:Instrument>";

    public static final String STUDY =
        "<el:Study xmlns:el=\"http://escidoc.org/ontologies/bw-elabs/re#\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">"
            + "<dc:title>Study Default Title</dc:title>"
            + "<dc:description></dc:description>"
            + "<el:motivating-publication rdf:resource=\"\"/>"
            + "<el:resulting-publication rdf:resource=\"\"/>"
            + "</el:Study>";

    public static final String RIG =
        "<el:Rig xmlns:el=\"http://escidoc.org/ontologies/bw-elabs/re#\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">"
            + "<dc:title>FRS Rig 01</dc:title>"
            + "<dc:description></dc:description>"
            + "<el:instrument rdf:resource=\"\"/>" + "<el:instrument rdf:resource=\"\"/>" + "</el:Rig>";

    public static final String INVESTIGATIONSERIES =
        "<el:InvestigationSeries xmlns:el=\"http://escidoc.org/ontologies/bw-elabs/re#\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\"     xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">"
            + "<dc:title>Investigation Series 01</dc:title>"
            + "<dc:description></dc:description>"
            + "</el:InvestigationSeries>";

    public static final String INVESTIGATION =
        "<el:Investigation xmlns:el=\"http://escidoc.org/ontologies/bw-elabs/re#\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">"
            + "<dc:title>Investigation 01</dc:title>"
            + "<dc:description></dc:description>"
            + "<el:max-runtime>84000</el:max-runtime>"
            + "<el:deposit-endpoint></el:deposit-endpoint>"
            + "<el:rig rdf:resource=\"\"/>"
            + "<el:instrument rdf:resource=\"\">"
            + "<el:monitored-folder></el:monitored-folder>"
            + "</el:instrument>"
            + "<el:instrument rdf:resource=\"\">"
            + "<el:monitored-folder></el:monitored-folder>" + "</el:instrument>" + "</el:Investigation>";

}
