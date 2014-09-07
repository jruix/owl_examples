package es.jonathanruiz.owl.tools;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.io.File;

/**
 * Created by jruiz on 31/08/14.
 */
public class OwlFileLoader {
    private OWLOntologyManager manager;
    private OWLOntology ontology;

    public void load(String owlFilePath) throws OWLOntologyCreationException {
        this.manager = OWLManager.createOWLOntologyManager();
        File file = new File(owlFilePath);
        this.ontology = manager.loadOntologyFromOntologyDocument(file);
        this.manager.removeOntology(this.ontology);
    }

    public void unload() {
        this.manager.removeOntology(this.ontology);
    }

    public OWLOntology getOntology() {
        return this.ontology;
    }
    public OWLOntologyManager getManager() { return this.manager; }
}
