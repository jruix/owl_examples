package shahadkudama.owl.JsonUMLSJoinOwlHp;

import shahadkudama.owl.JsonUMLSJoinOwlHp.implementations.*;
import shahadkudama.owl.JsonUMLSJoinOwlHp.interfaces.Comparator;
import shahadkudama.owl.tools.OwlDataExtrators;
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


public class Launcher {
    private static OWLOntology owlOntology = null;
    private static JSONArray umlsOntologyJsonArray = null;

    private static OWLOntology loadOwlFile(String file_path)
    {
        try {

            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            File file = new File(file_path);
            // Now load the local copy
            Launcher.owlOntology = manager.loadOntologyFromOntologyDocument(file);

        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        }

        return Launcher.owlOntology;
    }

    private static JSONArray loadJsonFile(String file_path)
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

        return Launcher.umlsOntologyJsonArray;
    }

    private static Set<String> getMatches(JSONArray jsonArray , OWLOntology owlOntology, Comparator comparator)
    {
        HashSet<String> result = new HashSet<String>();

        int counter = 0;
        Iterator<JSONObject> jsonArrayIterator = jsonArray.iterator();
        while(jsonArrayIterator.hasNext()) {
            System.out.println("Iteration: " + counter++ + "/" + jsonArray.size() + " of " + comparator.getClass().getSimpleName());
            JSONObject jsonObject = jsonArrayIterator.next();
            String jsonConcept = (String) jsonObject.get("concept");
            String jsonAnnotationsIds = "";
            JSONArray jsonAnnotationsArray = (JSONArray) jsonObject.get("annotations");
            Iterator<JSONObject> jsonAnnotationsArrayIterator = jsonAnnotationsArray.iterator();
            while(jsonAnnotationsArrayIterator.hasNext()) {
                JSONObject jsonAnnottation = jsonAnnotationsArrayIterator.next();
                String jsonAnnotationIdentifier = (String) jsonAnnottation.get("identifier");
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

    private static void saveMatches(Set<String> matches, String path){
        try {
            PrintWriter writer = new PrintWriter(path, "UTF-8");
            Iterator<String> matchesIterator = matches.iterator();
            writer.println("Total:" + matches.size() );
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

    public static void main (String [] args)
    {

        // Load HP (Human Phenotype) OWL Document
        loadOwlFile("src/main/resources/hp.owl");

        // Load UMLS Json file
        loadJsonFile("src/main/resources/umls_results_1.json");

        // Get matches
        System.out.println("Loking for matches using rule");
        Set<String> matches = Launcher.getMatches(Launcher.umlsOntologyJsonArray, Launcher.owlOntology, new OwlLabelMatch());
        Launcher.saveMatches(matches, "src/main/resources/matches.txt");

        // Get matches
        System.out.println("Loking for matches using rule1");
        Set<String> matches1 = Launcher.getMatches(Launcher.umlsOntologyJsonArray, Launcher.owlOntology, new OwlDefinitionMatch());
        Launcher.saveMatches(matches1, "src/main/resources/matches1.txt");

        // Get matches
        System.out.println("Loking for matches using rule2");
        Set<String> matches2 = Launcher.getMatches(Launcher.umlsOntologyJsonArray, Launcher.owlOntology, new OwlDefinitionAnnotationMatch());
        Launcher.saveMatches(matches2, "src/main/resources/matches2.txt");

        // Get matches
        System.out.println("Loking for matches using rule3");
        Set<String> matches3 = Launcher.getMatches(Launcher.umlsOntologyJsonArray, Launcher.owlOntology, new OwlClassAxiomsMatch());
        Launcher.saveMatches(matches3, "src/main/resources/matches3.txt");

        // Get matches
        System.out.println("Loking for matches using rule4");
        Set<String> matches4 = Launcher.getMatches(Launcher.umlsOntologyJsonArray, Launcher.owlOntology, new OwlAnnotatedTargetMatch());
        Launcher.saveMatches(matches4, "src/main/resources/matches4.txt");


    }
}
