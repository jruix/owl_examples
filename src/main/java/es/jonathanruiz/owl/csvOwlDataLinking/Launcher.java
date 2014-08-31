package es.jonathanruiz.owl.csvOwlDataLinking;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jruiz on 19/08/14.
 */

public class Launcher {
    public OWLOntologyManager manager;
    public OWLOntology ontology;
    public int deep = 0;

    private String getLabel(OWLClass owlClass)
    {
        Set<OWLAnnotation> owlAnnotations = owlClass.getAnnotations(this.ontology);

        for (Iterator<OWLAnnotation> it = owlAnnotations.iterator(); it.hasNext(); ) {
            OWLAnnotation owlAnnotation = it.next();
            if (owlAnnotation.getProperty().getIRI().getFragment().equals("label")){
                OWLLiteral val = (OWLLiteral) owlAnnotation.getValue();
                return val.getLiteral();
            }
        }
        return "";
    }

    private String getAttribute(String attribute, OWLClass owlClass)
    {
        Set<OWLAnnotation> owlAnnotations = owlClass.getAnnotations(this.ontology);

        for (Iterator<OWLAnnotation> it = owlAnnotations.iterator(); it.hasNext(); ) {
            OWLAnnotation owlAnnotation = it.next();

            if (owlAnnotation.getProperty().getIRI().getFragment().equals(attribute) && owlAnnotation.getValue() instanceof OWLLiteral){
                OWLLiteral val = (OWLLiteral) owlAnnotation.getValue();
                return val.getLiteral();
            }
        }
        return "";
    }

    private Set<String> getIdsFromString(String attribute)
    {
        Set<String> idsSet = new HashSet<String>();
        Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(attribute);
        while(m.find()) {
            idsSet.add(m.group().replace("(","").replace(")",""));
        }

        return idsSet;
    }

    public void tranverseOntoloty(OWLReasoner reasoner, OWLClass cls, OWLDataFactory df) {
        // This is a recursive function

        String indentation = "";
        for (int i = 0; i < this.deep; i++)
            indentation = indentation + "\t";

        String classLabel = this.getLabel(cls);
        String classDescription = this.getAttribute("IAO_0000115",cls);

        String idsInDescription = "";
        Set<String> idsInDescriptionSet = this.getIdsFromString(classDescription);
        for (Iterator<String> it = idsInDescriptionSet.iterator(); it.hasNext();) {
            String idInString = it.next();
            idsInDescription = idsInDescription + idInString.replace(":","_") + ",";
        }

        if (! (classLabel.isEmpty() || classDescription.isEmpty() || idsInDescription.isEmpty()) ) {
            Set <OWLAnnotationAssertionAxiom> annotationAssertionAxioms = cls.getAnnotationAssertionAxioms(ontology);

            String dbXrefCsv = "";
            for (Iterator<OWLAnnotationAssertionAxiom> it = annotationAssertionAxioms.iterator(); it.hasNext(); ) {
                OWLAnnotationAssertionAxiom owlAnnotationAssertionAxiom = it.next();
                String iriFragmentAxiomAnnotation = owlAnnotationAssertionAxiom.getProperty().getIRI().getFragment();
                if (owlAnnotationAssertionAxiom.getValue() instanceof OWLLiteral && (iriFragmentAxiomAnnotation.equals("hasDbXref"))) {
                    OWLLiteral val = (OWLLiteral) owlAnnotationAssertionAxiom.getValue();
                    dbXrefCsv = dbXrefCsv + val.getLiteral() + ",";
                }
            }

            System.out.println(classLabel + "|" + classDescription + "|" + idsInDescription);
        }

        if (cls.isBottomEntity())
            return;

        // Directly into the root of the child node
        NodeSet<OWLClass> directChildren = reasoner.getSubClasses( cls, true);
        Set<OWLClass> childSet = directChildren.getFlattened();
        deep ++;
        for (OWLClass child : childSet) {
            tranverseOntoloty(reasoner, child, df);
        }
        deep --;

    }

    public void loadOWL(String filepath)
            throws OWLOntologyCreationException {
        this.manager = OWLManager.createOWLOntologyManager();
        File file = new File(filepath);
        if (!file.exists()) {
            // If the file does not exist
            System.out.println("no such file!");
            return;
        }
        OWLDataFactory df = this.manager.getOWLDataFactory();

        this.ontology = this.manager.loadOntologyFromOntologyDocument(file);

        System.out.println("Loaded");
		/*
		 * Test traversal class hierarchy
		One could also use a complete reasoner like HermiT
		 * */
        OWLReasonerFactory reasonFactory = new StructuralReasonerFactory();

        OWLReasoner reasoner = reasonFactory.createReasoner(this.ontology);

        OWLClass top_clas = reasoner.getTopClassNode().getRepresentativeElement();


        tranverseOntoloty(reasoner, top_clas, df);

    }

    public static void main (String [] args) {
        System.out.println("Getting data...");
        Launcher ex = new Launcher();
        try {
            ex.loadOWL("src/main/resources/hp.owl");
            /*
             *  Test traversal class hierarchy
             *  One could also use a complete reasoner like HermiT
             * */

            OWLDataFactory df = ex.manager.getOWLDataFactory();
            OWLReasonerFactory reasonFactory = new StructuralReasonerFactory();

            OWLReasoner reasoner = reasonFactory.createReasoner(ex.ontology);

            OWLClass top_clas = reasoner.getTopClassNode().getRepresentativeElement();
            ex.tranverseOntoloty(reasoner, top_clas, df);

            ex.tranverseOntoloty(reasoner, top_clas, df);
        } catch (OWLOntologyCreationException e) {
            System.out.println("[Error] Exception throw:");
            e.printStackTrace();
        }
    }

}
