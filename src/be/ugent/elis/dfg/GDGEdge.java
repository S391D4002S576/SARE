package be.ugent.elis.dfg;

import be.elis.ugent.graphLibrary.Edge;
import be.elis.ugent.graphLibrary.Graph;
import be.elis.ugent.graphLibrary.Vertex;
import be.ugent.elis.sare.Dependence;
import be.ugent.elis.sare.domains.Domain;

/**
 * @author svdesmet
 * DFGEdge: Goes from producer to consumer
 */
public class GDGEdge extends Edge {
	public Dependence dataDependence = null;
	public Domain producedDomain = null; // Domain of the produced vertex at which the dependence exists 
	
    public GDGEdge(Graph iGraph, Vertex iFromVertex, Vertex iToVertex) {
		super(iGraph, iFromVertex, iToVertex);
	}
    
    @Override public GDGVertex getToVertex() { return (GDGVertex) super.getToVertex(); }
    
    @Override public GDGVertex getFromVertex() { return (GDGVertex) super.getFromVertex(); }
    
    public boolean isTriviallySatisfied() { return getToVertex().isReady; }
}
