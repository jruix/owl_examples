package shahadkudama.owl.tools;

import com.sun.deploy.util.StringUtils;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OwlDataExtrators {

    static public String getLabel(OWLClass owlClass, OWLOntology ontology)
    {
        Set<OWLAnnotation> owlAnnotations = owlClass.getAnnotations(ontology);

        for (Iterator<OWLAnnotation> it = owlAnnotations.iterator(); it.hasNext(); ) {
            OWLAnnotation owlAnnotation = it.next();
            if (owlAnnotation.getProperty().getIRI().getFragment().equals("label")){
                OWLLiteral val = (OWLLiteral) owlAnnotation.getValue();
                return val.getLiteral();
            }
        }
        return "";
    }

    static public String getAttribute(String attribute, OWLClass owlClass, OWLOntology ontology)
    {
        String result = "";
        Set<OWLAnnotation> owlAnnotations = owlClass.getAnnotations(ontology);

        for (Iterator<OWLAnnotation> it = owlAnnotations.iterator(); it.hasNext(); ) {
            OWLAnnotation owlAnnotation = it.next();

            if (owlAnnotation.getProperty().getIRI().getFragment().equals(attribute) && owlAnnotation.getValue() instanceof OWLLiteral){
                OWLLiteral val = (OWLLiteral) owlAnnotation.getValue();
                result = val.getLiteral() + "&&";
            }
        }
        if(result.endsWith("&&")) {
            result = result.substring(0, result.length() - 2);
        }
        return result;
    }

    static public Set<String> getIdsFromString(String attribute)
    {
        Set<String> idsSet = new HashSet<String>();
        Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(attribute);
        while(m.find()) {
            idsSet.add(m.group().replace("(","").replace(")","").replace(":","_"));
        }

        return idsSet;
    }
}
