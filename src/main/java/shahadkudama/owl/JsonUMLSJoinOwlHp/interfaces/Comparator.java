package shahadkudama.owl.JsonUMLSJoinOwlHp.interfaces;

import org.json.simple.JSONObject;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;

public interface Comparator {
    public boolean compare(JSONObject jsonObject, OWLClass owlClass, OWLOntology owlOntology);
}
