package be.ugent.elis.cg;

import be.elis.ugent.graphLibrary.Edge;
import be.elis.ugent.graphLibrary.Graph;
import be.elis.ugent.graphLibrary.Vertex;
import be.ugent.elis.AffineTransformation;
import be.ugent.elis.sare.Dependence;
import be.ugent.elis.sare.domains.Domain;
import be.ugent.elis.sare.domains.ParametrizedIntegerPolyhederDomain;

/**
 * @author svdesmet
 * DFGEdge: Goes from producer to consumer
 */
public class CGEdge extends Edge {
	public Dependence dataDependence = null;
	
    public CGEdge(Graph iGraph, Vertex iFromVertex, Vertex iToVertex) {
		super(iGraph, iFromVertex, iToVertex);
	}
    
    @Override public CGVertex getToVertex() { return (CGVertex) super.getToVertex(); }
    
    @Override public CGVertex getFromVertex() { return (CGVertex) super.getFromVertex(); }
    
    public boolean isTriviallySatisfied() { return getFromVertex().isReady; }
    
    public boolean isExactAffine() {
    	return (dataDependence != null) && (dataDependence instanceof AffineTransformation);
    }
    
}
