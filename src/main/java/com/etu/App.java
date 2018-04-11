package com.etu;

import org.apache.jena.rdf.model.*;
import org.apache.jena.util.FileManager;
import org.apache.jena.query.*;
import org.apache.jena.vocabulary.VCARD;

import java.io.*;

public class App {

    public static void main(String[] args) {
        Model model = read("vc-db-1.rdf", null);

        Resource johnSmith = model.getResource("http://somewhere/JohnSmith/");

        Selector selector = queryModel(model, johnSmith, null, (RDFNode) null, true);
        String queryString =
                "SELECT ?a ?b ?c " +
                "WHERE { " +
                    "?a ?b ?c" +
                "}";

        model.createResource("http://somewhere/AntoineChedin").addProperty(VCARD.FN, "Antoine Chedin");
        execSparqlQuery(model, queryString);

        Model diff = ModelFactory.createDefaultModel();
        diff.createResource("http://somewhere/AntoineChedin").addProperty(VCARD.FN, "Antoine Chedin");
        model = model.difference(diff);

        execSparqlQuery(model, queryString);

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

    private static Selector queryModel(Model model, Resource subject, Property predicate, RDFNode object, boolean showResults) {
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
    }

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
        Model catModel = ModelFactory.createDefaultModel();

        Resource catOne = catModel.createResource("http://miou/catOne");
        Resource catTwo = catModel.createResource("http://miou/catTwo");
        Resource catThree = catModel.createResource("http://miou/catThree");

        Property hasCatFriend = catModel.createProperty("hasCatFriend");
        Property isCatGender = catModel.createProperty("isCatGender");
        Property hasName = catModel.createProperty("hasName");

        catOne
                .addProperty(hasCatFriend, catTwo)
                .addProperty(hasCatFriend, catThree)
                .addProperty(isCatGender, "Female Cat")
                .addProperty(hasName, "Johanna meow");

        catTwo
                .addProperty(hasCatFriend, catOne)
                .addProperty(hasCatFriend, catThree)
                .addProperty(isCatGender, "Male Cat")
                .addProperty(hasName, "Valentin meow");

        catThree
                .addProperty(hasCatFriend, catOne)
                .addProperty(hasCatFriend, catTwo)
                .addProperty(isCatGender, "Male Cat")
                .addProperty(hasName, "Antoine meow");



        save(catModel, "cat.rdf", "N-TRIPLES");
    }
}
