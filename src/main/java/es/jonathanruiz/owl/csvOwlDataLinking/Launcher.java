package es.jonathanruiz.owl.csvOwlDataLinking;

import es.jonathanruiz.owl.tools.OwlDataExtrators;
import es.jonathanruiz.owl.tools.OwlFileLoader;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jruiz on 19/08/14.
 */

public class Launcher {

//    private String getLabel(OWLClass owlClass)
//    {
//        Set<OWLAnnotation> owlAnnotations = owlClass.getAnnotations(this.ontology);
//
//        for (Iterator<OWLAnnotation> it = owlAnnotations.iterator(); it.hasNext(); ) {
//            OWLAnnotation owlAnnotation = it.next();
//            if (owlAnnotation.getProperty().getIRI().getFragment().equals("label")){
//                OWLLiteral val = (OWLLiteral) owlAnnotation.getValue();
//                return val.getLiteral();
//            }
//        }
//        return "";
//    }
//
//    private String getAttribute(String attribute, OWLClass owlClass)
//    {
//        Set<OWLAnnotation> owlAnnotations = owlClass.getAnnotations(this.ontology);
//
//        for (Iterator<OWLAnnotation> it = owlAnnotations.iterator(); it.hasNext(); ) {
//            OWLAnnotation owlAnnotation = it.next();
//
//            if (owlAnnotation.getProperty().getIRI().getFragment().equals(attribute) && owlAnnotation.getValue() instanceof OWLLiteral){
//                OWLLiteral val = (OWLLiteral) owlAnnotation.getValue();
//                return val.getLiteral();
//            }
//        }
//        return "";
//    }
//
//    private Set<String> getIdsFromString(String attribute)
//    {
//        Set<String> idsSet = new HashSet<String>();
//        Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(attribute);
//        while(m.find()) {
//            idsSet.add(m.group().replace("(","").replace(")",""));
//        }
//
//        return idsSet;
//    }

    public void tranverseOntoloty(OWLReasoner reasoner, OWLClass cls, OWLOntology ontology, JSONArray jsonArray, Set<String> matches) {

        String classLabel = OwlDataExtrators.getLabel(cls, ontology);
        String classDescription = OwlDataExtrators.getAttribute("IAO_0000115",cls,ontology);
        if (! classDescription.isEmpty()) {
            System.out.println("Not Empty!");
            Set<String> idsInDescriptionSet = OwlDataExtrators.getIdsFromString(classDescription);
            Set<String> idsInJSonSet = new HashSet<String>();

            if (! idsInDescriptionSet.isEmpty()) {
                Iterator<JSONObject> jsonArrayIterator = jsonArray.iterator();
                while (jsonArrayIterator.hasNext()) {
                    JSONObject jsonObject = jsonArrayIterator.next();
                    JSONArray jsonAnnotationsArray = (JSONArray) jsonObject.get("annotations");

                    Iterator<JSONObject> jsonAnnotationsArrayIterator = jsonAnnotationsArray.iterator();
                    while (jsonAnnotationsArrayIterator.hasNext()) {
                        JSONObject jsonAnnotation = jsonAnnotationsArrayIterator.next();
                        String annotationIdentifier = (String) jsonAnnotation.get("identifier");
                        idsInJSonSet.add(annotationIdentifier);
                    }

                    if (idsInJSonSet.containsAll(idsInDescriptionSet)) {

                        matches.add("{ 'label':'" + classLabel + "'," + "'description':'" + classDescription + "'," + jsonObject.toJSONString());
                        System.out.println("Match!" + "{ 'label':'" + classLabel + "'," + "'description':'" + classDescription + "'," + jsonObject.toJSONString());
                    }
                    idsInJSonSet.clear();

                }
            }
        }


        if (cls.isBottomEntity())
            return;

        // Directly into the root of the child node
        NodeSet<OWLClass> directChildren = reasoner.getSubClasses( cls, true);
        Set<OWLClass> childSet = directChildren.getFlattened();

        for (OWLClass child : childSet) {
            tranverseOntoloty(reasoner, child, ontology, jsonArray, matches);
        }


    }

//    public void loadOWL(String filepath)
//            throws OWLOntologyCreationException {
//        this.manager = OWLManager.createOWLOntologyManager();
//        File file = new File(filepath);
//        if (!file.exists()) {
//            // If the file does not exist
//            System.out.println("no such file!");
//            return;
//        }
//        OWLDataFactory df = this.manager.getOWLDataFactory();
//
//        this.ontology = this.manager.loadOntologyFromOntologyDocument(file);
//
//        System.out.println("Loaded");
//		/*
//		 * Test traversal class hierarchy
//		One could also use a complete reasoner like HermiT
//		 * */
//        OWLReasonerFactory reasonFactory = new StructuralReasonerFactory();
//
//        OWLReasoner reasoner = reasonFactory.createReasoner(this.ontology);
//
//        OWLClass top_clas = reasoner.getTopClassNode().getRepresentativeElement();
//
//
//        tranverseOntoloty(reasoner, top_clas, df);
//
//    }

    public static void main (String [] args) {
        System.out.println("Getting data...");
        Launcher ex = new Launcher();
        try {

            // Read OWL File
//            OwlFileLoader owlFileReader = new OwlFileLoader();
//            owlFileReader.load("src/main/resources/hp.owl");

            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            OWLDataFactory factory = manager.getOWLDataFactory();
            File file = new File("src/main/resources/hp.owl");
            OWLOntology hpOntology = manager.loadOntologyFromOntologyDocument(file);
            OWLReasonerFactory reasonFactory = new StructuralReasonerFactory();
            OWLReasoner reasoner = reasonFactory.createReasoner(hpOntology);
            OWLClass top_clas = reasoner.getTopClassNode().getRepresentativeElement();

            // Read JSON File
            JSONParser jsonParser = new JSONParser();
            File Jsonfile = new File("src/main/resources/umls_results_1.json");
            JSONArray jsonArray= (JSONArray) jsonParser.parse(new FileReader(Jsonfile));

            Set<String> matches = new HashSet<String>();
            ex.tranverseOntoloty(reasoner, top_clas, hpOntology,jsonArray, matches);

            PrintWriter writer = new PrintWriter("src/main/resources/matches.json", "UTF-8");
            Iterator<String> matchesIterator = matches.iterator();
            while (matchesIterator.hasNext()) {
                String matchString = matchesIterator.next();
                writer.println(matchString );
            }

            writer.close();

            System.out.println("Matches");

        } catch (OWLOntologyCreationException e) {
            System.out.println("[Error] Exception throw:");
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
