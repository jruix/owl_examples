package es.jonathanruiz.owl.JsonUMLSJoinOwlHp.implementations;

import es.jonathanruiz.owl.JsonUMLSJoinOwlHp.interfaces.Comparator;
import es.jonathanruiz.owl.tools.OwlDataExtrators;
import org.json.simple.JSONObject;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * Created by jruiz on 29/09/14.
 */
public class OwlLabelMatch implements Comparator {

    @Override
    public boolean compare(JSONObject jsonObject, OWLClass owlClass, OWLOntology owlOntology)
    {
        String jsonConcept = (String) jsonObject.get("concept");
        String classLabel = OwlDataExtrators.getLabel(owlClass, owlOntology);

        System.out.println(jsonConcept + "==" + classLabel);
        return jsonConcept.equals(classLabel);
    }
}
