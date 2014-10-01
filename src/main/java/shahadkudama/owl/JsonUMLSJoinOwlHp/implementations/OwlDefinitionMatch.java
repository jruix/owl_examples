package shahadkudama.owl.JsonUMLSJoinOwlHp.implementations;

import org.json.simple.JSONObject;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import shahadkudama.owl.JsonUMLSJoinOwlHp.interfaces.Comparator;
import shahadkudama.owl.tools.OwlDataExtrators;


public class OwlDefinitionMatch implements Comparator {

    @Override
    public boolean compare(JSONObject jsonObject, OWLClass owlClass, OWLOntology owlOntology)
    {
        String jsonConcept = (String) jsonObject.get("concept");
        String classDefinition = OwlDataExtrators.getAttribute("IAO_0000115", owlClass, owlOntology);

        return jsonConcept.equals(classDefinition);
    }
}
