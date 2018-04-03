package com.etu;

import org.apache.jena.rdf.model.*;
import org.apache.jena.util.FileManager;

import java.io.*;

public class App {

    public static void main(String[] args) {
        init();
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
