package shahadkudama.owl.JsonUMLSJoinOwlHp.implementations;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import shahadkudama.owl.JsonUMLSJoinOwlHp.interfaces.Comparator;
import shahadkudama.owl.tools.OwlDataExtrators;
import shahadkudama.owl.tools.SetsOperations;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class OwlDefinitionAnnotationMatch implements Comparator {

    @Override
    public boolean compare(JSONObject jsonObject, OWLClass owlClass, OWLOntology owlOntology)
    {
        String jsonConcept = (String) jsonObject.get("concept");
        String classDefinition = OwlDataExtrators.getAttribute("IAO_0000115", owlClass, owlOntology);
        Set<String> IdsInDefinition = OwlDataExtrators.getIdsFromString(classDefinition);
        JSONArray jsonAnnotationsArray = (JSONArray) jsonObject.get("annotations");

        Set<String> wordsInConceptSet = new HashSet<String>(Arrays.asList(jsonConcept.split(" ")));
        if (wordsInConceptSet.size() != IdsInDefinition.size()) {
            return false;
        }

        Set<String> jsonAnnotationsIdsSet = new HashSet<String>();
        Iterator<JSONObject> jsonAnnotationsArrayIterator = jsonAnnotationsArray.iterator();
        while(jsonAnnotationsArrayIterator.hasNext()) {
            JSONObject jsonAnnottation = jsonAnnotationsArrayIterator.next();
            String jsonAnnotationIdentifier = (String) jsonAnnottation.get("identifier");
            jsonAnnotationsIdsSet.add(jsonAnnotationIdentifier);

        }

        Set< Set<String> > jsonAnnotationsIdPowerSet = SetsOperations.powerSet(jsonAnnotationsIdsSet);
        Iterator < Set<String> > jsonAnnotationsIdPowerSetIterator = jsonAnnotationsIdPowerSet.iterator();
        while (jsonAnnotationsIdPowerSetIterator.hasNext()) {
            Set<String> idsSet = jsonAnnotationsIdPowerSetIterator.next();
            if(idsSet.containsAll(IdsInDefinition)) {
                return true;
            }
        }


        return false;
    }
}
