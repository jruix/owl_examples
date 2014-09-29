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


    public void tranverseOntoloty(OWLReasoner reasoner, OWLClass cls, OWLOntology ontology, JSONArray jsonArray, Set<String> matches) {

        String classLabel = OwlDataExtrators.getLabel(cls, ontology);
        String classDescription = OwlDataExtrators.getAttribute("IAO_0000115",cls,ontology);
        String classExactSynonym = OwlDataExtrators.getAttribute("hasExactSynonym",cls,ontology);
        String classDbXref = OwlDataExtrators.getAttribute("hasDbXref",cls,ontology);


        if (! classDescription.isEmpty()) {
            Iterator<JSONObject> jsonArrayIterator = jsonArray.iterator();
            while (jsonArrayIterator.hasNext()) {
                JSONObject jsonObject = jsonArrayIterator.next();
                String jsonConcept = (String) jsonObject.get("concept");

                if (classDescription.equals(jsonConcept)) {

                    matches.add("{ 'type': 'rule 1' , 'label':'" + classLabel + "'," + "'Synonyms':'" + classExactSynonym + "'," + "'description':'" + classDescription + "'," + jsonObject.toJSONString());
                    System.out.println("{ 'type': 'rule 1' , 'label':'" + classLabel + "'," + "'Synonyms':'" + classExactSynonym + "'," + "'description':'" + classDescription + "'," + jsonObject.toJSONString());
                }
            }
        }

        if (! classExactSynonym.isEmpty()) {

            Set<String> exactSynonymsSet = new HashSet<String>(Arrays.asList(classExactSynonym.split("&&")));
            Iterator<JSONObject> jsonArrayIterator = jsonArray.iterator();
            while (jsonArrayIterator.hasNext()) {
                JSONObject jsonObject = jsonArrayIterator.next();
                String jsonConcept = (String) jsonObject.get("concept");

                if (exactSynonymsSet.contains(jsonConcept)) {

                    matches.add("{ 'type': 'rule 2' , 'label':'" + classLabel + "'," + "'Synonyms':'" + classExactSynonym  + "'," + "'description':'" + classDescription + "'," + jsonObject.toJSONString());
                    System.out.println("{ 'type': 'rule 1' , 'label':'" + classLabel + "'," + "'Synonyms':'" + classExactSynonym  + "'," + "'description':'" + classDescription + "'," + jsonObject.toJSONString());
                }

            }

        }

        if (! classDbXref.isEmpty()) {


            Set<String> idsInJSonSet = new HashSet<String>();

            Set<String> dbXrefSet = new HashSet<String>(Arrays.asList(classDbXref.split("&&")));
            Iterator<String> dbXrefIterator = dbXrefSet.iterator();
            while (dbXrefIterator.hasNext()) {
                String dbXref = dbXrefIterator.next();

                if (dbXref.contains("UMLS:")) {
                    matches.add("{ 'type': 'rule 3' , 'label':'" + classLabel + "'," + "'Synonyms':'" + classExactSynonym  + "'," + "'description':'" + classDescription + "', 'UMLS':" + dbXref);
                    System.out.println("{ 'type': 'rule 3' , 'label':'" + classLabel + "'," + "'Synonyms':'" + classExactSynonym  + "'," + "'description':'" + classDescription + "', 'UMLS':" + dbXref);
                }
                idsInJSonSet.clear();

            }

        }

        if (! classDescription.isEmpty()) {

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

                    if (idsInDescriptionSet.containsAll(idsInJSonSet)) {

                        matches.add("{ 'type': 'rule 4' , 'label':'" + classLabel + "'," + "'description':'" + classDescription + "'," + jsonObject.toJSONString());
                        System.out.println("Rule 4 Match!" + "{ 'label':'" + classLabel + "'," + "'description':'" + classDescription + "'," + jsonObject.toJSONString());
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

    public static void main (String [] args) {
        System.out.println("Getting data...");
        Launcher ex = new Launcher();
        try {

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
