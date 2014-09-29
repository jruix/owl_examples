package es.jonathanruiz.owl.JsonUMLSJoinOwlHp.interfaces;

import org.json.simple.JSONObject;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * Created by jruiz on 29/09/14.
 */
public interface Comparator {
    public boolean compare(JSONObject jsonObject, OWLClass owlClass, OWLOntology owlOntology);
}
