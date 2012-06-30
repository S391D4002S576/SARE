package be.ugent.elis.cg;

import be.elis.ugent.graphLibrary.Graph;

public class CGComputationVertex extends CGVertex {
	int computationVertexIndex = -1;
	
	public CGComputationVertex(CGraph iCGraph) {
		super(iCGraph);
		
		computationVertexIndex = iCGraph.computationVertices.size();
		iCGraph.computationVertices.add(this);
	}
	
	public CGComputationVertex(CGraph iCGraph, String iName) {
	    super(iCGraph);
	    
	    name = iName;
	    
		computationVertexIndex = iCGraph.computationVertices.size();
		iCGraph.computationVertices.add(this);
    }
}
