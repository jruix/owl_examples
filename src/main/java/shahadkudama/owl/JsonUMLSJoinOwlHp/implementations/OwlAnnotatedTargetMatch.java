package shahadkudama.owl.JsonUMLSJoinOwlHp.implementations;

import shahadkudama.owl.JsonUMLSJoinOwlHp.interfaces.Comparator;
import shahadkudama.owl.tools.OwlDataExtrators;
import org.json.simple.JSONObject;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;

public class OwlAnnotatedTargetMatch implements Comparator {

    @Override
    public boolean compare(JSONObject jsonObject, OWLClass owlClass, OWLOntology owlOntology)
    {
        String jsonConcept = (String) jsonObject.get("concept");
        String classLabel = OwlDataExtrators.getLabel(owlClass, owlOntology);
        String classAnnotatedTarget = OwlDataExtrators.getAttribute("annotatedTarget", owlClass, owlOntology);

        System.out.println(jsonConcept + "==" + classLabel);
        return jsonConcept.equals(classLabel);
    }
}
