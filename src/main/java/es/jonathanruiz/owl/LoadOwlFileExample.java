package es.jonathanruiz.owl;

import es.jonathanruiz.owl.parser.UmlsParser;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.apibinding.OWLManager;

import java.io.File;

public class LoadOwlFileExample {
    public OWLOntologyManager manager;

    public void Load(String owlFilePath) throws OWLOntologyCreationException {
        manager = OWLManager.createOWLOntologyManager();
        File file = new File(owlFilePath);
        OWLOntology hp = manager.loadOntologyFromOntologyDocument(file);
        System.out.println("Loaded ontology: " + hp);

        manager.removeOntology(hp);
    }

    public static void main (String [] args) {
        LoadOwlFileExample loadOwlFileExample = new LoadOwlFileExample();
        try {
            loadOwlFileExample.Load(args[1]);
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        }
    }

}
