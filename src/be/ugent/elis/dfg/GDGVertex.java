package be.ugent.elis.dfg;

import be.elis.ugent.graphLibrary.Graph;
import be.elis.ugent.graphLibrary.Vertex;
import be.ugent.elis.sare.domains.Domain;
import be.ugent.elis.sare.domains.DomainTransformation;

public class GDGVertex extends Vertex {
    String name = null;
    public Domain computationDomain = null;
    public DomainTransformation timeSchedule = null;
    
    /**
     * Determines whether the operations represented by this vertex have already completed at time 0
     * (which implies we don't need to schedule them)
     */
    public boolean isReady = false;
  
    public GDGVertex(Graph iGraph) {
	    super(iGraph);
    }
    
    public GDGVertex(Graph iGraph, String iName) {
	    super(iGraph);
	    
	    name = iName;
    }
    
    public String getName() {
    	return (name != null) ? name : String.format("Vertex %d", getIndex());
    }
}
