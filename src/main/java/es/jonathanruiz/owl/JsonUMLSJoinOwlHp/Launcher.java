package es.jonathanruiz.owl.JsonUMLSJoinOwlHp;

import com.sun.org.apache.xpath.internal.operations.Bool;
import es.jonathanruiz.owl.JsonUMLSJoinOwlHp.implementations.OwlLabelMatch;
import es.jonathanruiz.owl.JsonUMLSJoinOwlHp.interfaces.Comparator;
import es.jonathanruiz.owl.tools.OwlDataExtrators;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.io.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * Created by jruiz on 29/09/14.
 */
public class Launcher {
    private static OWLOntology owlOntology = null;
    private static JSONArray umlsOntologyJsonArray = null;

    private static void loadOwlFile(String file_path)
    {
        try {

            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            File file = new File(file_path);
            // Now load the local copy
            Launcher.owlOntology = manager.loadOntologyFromOntologyDocument(file);

        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        }
    }

    private static void loadJsonFile(String file_path)
    {
        try {
            // Read JSON File
            JSONParser jsonParser = new JSONParser();
            File Jsonfile = new File(file_path);
            Launcher.umlsOntologyJsonArray = (JSONArray) jsonParser.parse(new FileReader(Jsonfile));


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Set<String> getMatches(JSONArray jsonArray , OWLOntology owlOntology, Comparator comparator)
    {
        HashSet<String> result = new HashSet<String>();

        Iterator<JSONObject> jsonArrayIterator = jsonArray.iterator();
        while(jsonArrayIterator.hasNext()) {
            JSONObject jsonObject = jsonArrayIterator.next();
            String jsonConcept = (String) jsonObject.get("concept");
            String jsonAnnotationsIds = "";
            JSONArray jsonAnnotationsArray = (JSONArray) jsonObject.get("annotations");
            Iterator<JSONObject> jsonAnnotationsArrayIterator = jsonAnnotationsArray.iterator();
            while(jsonAnnotationsArrayIterator.hasNext()) {
                JSONObject jsonAnnottation = jsonAnnotationsArrayIterator.next();
//                String jsonAnnotationDefinition = (String) jsonAnnottation.get("definition");
                String jsonAnnotationIdentifier = (String) jsonAnnottation.get("identifier");
//                String jsonAnnotationOntology = (String) jsonAnnottation.get("ontology");
//                String jsonAnnotationLabel = (String) jsonAnnottation.get("label");

                jsonAnnotationsIds = jsonAnnotationsIds + jsonAnnotationIdentifier + ", ";
            }

            Set<OWLClass> classesInSignature = owlOntology.getClassesInSignature();
            Iterator<OWLClass> classesInSignatureIterator = classesInSignature.iterator();
            while(classesInSignatureIterator.hasNext()){
                OWLClass owlClass = classesInSignatureIterator.next();
                String classLabel = OwlDataExtrators.getLabel(owlClass, owlOntology);

                if(comparator.compare(jsonObject, owlClass, owlOntology)) {
                    result.add(jsonConcept + "\t" + jsonAnnotationsIds + "\t" + classLabel);
                }

            }

        }
        return result;
    }

    public static void main (String [] args)
    {
        try {
            // Load HP (Human Phenotype) OWL Document
            loadOwlFile("src/main/resources/hp.owl");
            // Load UMLS Json file
            loadJsonFile("src/main/resources/umls_results_1.json");
            // Get matches
            Set<String> matches = Launcher.getMatches(Launcher.umlsOntologyJsonArray, Launcher.owlOntology, new OwlLabelMatch());
            // Write matches in text file
            PrintWriter writer = null;
            writer = new PrintWriter("src/main/resources/matches.txt", "UTF-8");

            Iterator<String> matchesIterator = matches.iterator();
            while (matchesIterator.hasNext()) {
                String matchString = matchesIterator.next();
                writer.println(matchString );
            }
            writer.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
