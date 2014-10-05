package shahadkudama.owl.JsonUMLSJoinOwlHp.implementations;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import shahadkudama.owl.JsonUMLSJoinOwlHp.interfaces.Comparator;
import shahadkudama.owl.tools.OwlDataExtrators;
import shahadkudama.owl.tools.SetsOperations;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class OwlClassAxiomsMatch implements Comparator {

    @Override
    public boolean compare(JSONObject jsonObject, OWLClass owlClass, OWLOntology owlOntology)
    {
        Set<OWLClass> allClassesInAxiomsRelated = new HashSet<OWLClass>();
        JSONArray jsonAnnotationsArray = (JSONArray) jsonObject.get("annotations");

        Set<OWLAxiom> owlAxiomSet = owlClass.getReferencingAxioms(owlOntology);
        Iterator<OWLAxiom> owlAxiomSetIterator = owlAxiomSet.iterator();
        while (owlAxiomSetIterator.hasNext()) {
            OWLAxiom owlAxiom=owlAxiomSetIterator.next();
            Set<OWLClass> owlClassesInAxiom = owlAxiom.getClassesInSignature();
            allClassesInAxiomsRelated.addAll(owlClassesInAxiom);
        }

        Set<String> owlClassesIds = new HashSet<String>();
        Iterator<OWLClass> allClassesInAxiomsRelatedIterator = allClassesInAxiomsRelated.iterator();
        while (allClassesInAxiomsRelatedIterator.hasNext()) {
            OWLClass currentClass = allClassesInAxiomsRelatedIterator.next();
            owlClassesIds.add(OwlDataExtrators.getAttribute("id", currentClass, owlOntology).replace(":", "_"));
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
            if(idsSet.containsAll(owlClassesIds)) {
                return true;
            }
        }

       return false;
    }
}
