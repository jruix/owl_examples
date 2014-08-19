package es.jonathanruiz.owl;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.util.OWLOntologyWalker;
import org.semanticweb.owlapi.util.OWLOntologyWalkerVisitor;

import java.io.File;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by jruiz on 19/08/14.
 */

public class GetOwlDataExample {
    public OWLOntologyManager manager;

    public void showClasses() throws OWLOntologyCreationException {
        manager = OWLManager.createOWLOntologyManager();
        OWLDataFactory factory = manager.getOWLDataFactory();


        File file = new File("/tmp/hp.owl");
        OWLOntology hpOntology = manager.loadOntologyFromOntologyDocument(file);
        System.out.println("Loaded ontology: " + hpOntology);
        Set<OWLClass> currentOntologyClasses = hpOntology.getClassesInSignature();
        System.out.println("Classes in Ontology: " + currentOntologyClasses.size());

        for (Iterator<OWLClass> it = currentOntologyClasses.iterator(); it.hasNext(); ) {
            OWLClass owlClass = it.next();
            //System.out.println(owlClass);
            Set<OWLAnnotation> annotations = owlClass.getAnnotations(hpOntology);

            for (Iterator<OWLAnnotation> itAnnotations = annotations.iterator(); itAnnotations.hasNext(); ) {
                OWLAnnotation oWLAnnotation = itAnnotations.next();
                //System.out.println(oWLAnnotation);
                System.out.println("Annotation property: " + oWLAnnotation.getProperty().toStringID());
                System.out.println("Annotation value: " + oWLAnnotation.getValue());
            }

        }

        manager.removeOntology(hpOntology);
    }




    public static void main (String [] args) {
        System.out.println("hola");
        GetOwlDataExample ex = new GetOwlDataExample();
        try {
            ex.showClasses();
        } catch (OWLOntologyCreationException e) {
            System.out.println("ha sido un error");
            e.printStackTrace();
        }
    }

}
