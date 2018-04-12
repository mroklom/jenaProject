package com.etu;

import org.apache.jena.rdf.model.*;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.util.FileManager;
import org.apache.jena.query.*;
import org.apache.jena.vocabulary.VCARD;

import java.io.*;

public class App {

    public static void main(String[] args) {

        init();

        Model model = read("etu.rdf", "RDF/XML");

        String queryString =
                "SELECT ?sujet ?prédicat ?objet " +
                        "WHERE { " +
                        "?sujet ?prédicat ?objet" +
                        "}";

        execSparqlQuery(model, queryString);

        Resource valentin = model.getResource("http://etu.test/MaréchalValentin");
        System.out.println("On retire la propriété NINCKNAME de valentin");
        valentin.removeAll(VCARD.NICKNAME);

        execSparqlQuery(model, queryString);

        System.out.println("On rajoute Mickey au jeu de données");
        Resource mickey = model.createResource("http://etu.test/MickeyMouse")
                .addProperty(VCARD.N, "Mickey")
                .addProperty(VCARD.FN, "Mouse")
                .addProperty(VCARD.ORG, "Dsiney");

        execSparqlQuery(model, queryString);

        System.out.println("On change l'addresse email de Valentin");
        valentin.removeAll(VCARD.EMAIL);
        valentin.addProperty(VCARD.EMAIL, "valentin.marechal@etu.univ-tours.fr");

        execSparqlQuery(model, queryString);

        System.out.println("On supprime valentin du jeu de données en supprimant toutes ses proprietés");
        valentin.removeProperties();

        execSparqlQuery(model, queryString);

        save(model, "etu_update_turtle.rdf", "TURTLE");
        save(model, "etu_update_rdfxml.rdf", "RDF/XML");

    }

    private static void execSparqlQuery (Model model, String queryString){
        Query query = QueryFactory.create(queryString);
        QueryExecution exec = QueryExecutionFactory.create(query, model);
        try {
            ResultSet resultSet = exec.execSelect();
            ResultSetFormatter.out(System.out, resultSet, query) ;
        } finally {
            exec.close();
        }
    }

    /*private static Selector queryModel(Model model, Resource subject, Property predicate, RDFNode object, boolean showResults) {
        Selector selector = new SimpleSelector(subject, predicate, object);

        if(showResults) {
            StmtIterator iter = model.listStatements(selector);
            if (iter.hasNext()) {
                // System.out.println("The database contains vcards for:");
                while (iter.hasNext()) {

                    Statement statement = iter.nextStatement();
                    System.out.println("  " + statement.getSubject().toString() + " --- " + statement.getPredicate().toString() + " ---> " + statement.getObject().toString());
                }
            } else {
                System.out.println("No Smith's were found in the database");
            }
        }

        return selector;
    }*/

    private static Model read(String inputFileName, String format) {

        // create an empty model
        Model model = ModelFactory.createDefaultModel();

        // use the FileManager to find the input file
        InputStream in = FileManager.get().open( inputFileName );
        if (in == null) throw new IllegalArgumentException("File: " + inputFileName + " not found");

        // read the RDF/XML file and return it
        // TODO: How to read from a specific format ?
        return model.read(in, format);

    }

    private static void save(Model model, String outputFile, String format) {
        try {
            // TODO: How to save in a XML format, got exception trying
            model.write(new FileOutputStream(new File(outputFile)), format);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void init() {
        Model model = ModelFactory.createDefaultModel();

        Resource resource1 = model.createResource("http://etu.test/JohannaChapman");
        Resource resource2 = model.createResource("http://etu.test/MaréchalValentin");
        Resource resource3 = model.createResource("http://etu.test/ChédinAntoine");

        resource1
                .addProperty(VCARD.FN, "Johanna")
                .addProperty(VCARD.N, "Chapman")
                .addProperty(VCARD.NICKNAME, "Jojo");

        resource2
                .addProperty(VCARD.FN, "Valentin")
                .addProperty(VCARD.N, "Maréchal")
                .addProperty(VCARD.NICKNAME, "Val")
                .addProperty(VCARD.EMAIL, "marechal.valentin@etu.univ-tours.fr");

        resource3
                .addProperty(VCARD.FN, "Antoine")
                .addProperty(VCARD.N, "Chédin");


        save(model, "etu.rdf", "RDF/XML");
    }
}
