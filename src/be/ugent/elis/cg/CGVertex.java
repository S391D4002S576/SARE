package be.ugent.elis.cg;

import be.elis.ugent.graphLibrary.Graph;
import be.elis.ugent.graphLibrary.Vertex;
import be.ugent.elis.sare.domains.Domain;
import be.ugent.elis.sare.domains.DomainTransformation;
import be.ugent.elis.sare.domains.ParametrizedIntegerPolyhederDomain;

public class CGVertex<VD> extends Vertex<VD> {
    String name = null;
    public Domain domain = null;
    
    /**
     * Determines whether the operations represented by this vertex have already completed at time 0
     * (which implies we don't need to schedule them)
     * -- use by the Feautrier scheduler, but not by others
     */
    public boolean isReady = false;
  
    public CGVertex(Graph<VD, ?> iGraph, VD iData) {
	    super(iGraph, iData);
    }
    
    public CGVertex(Graph iGraph, VD iData, String iName) {
	    super(iGraph, iData);
	    
	    name = iName;
    }
    
    public String getName() {
    	return (name != null) ? name : String.format("Vertex %d", getIndex());
    }
    
    public boolean isExactAffine() {
    	return ((domain != null) && (domain instanceof ParametrizedIntegerPolyhederDomain));
    }
}
