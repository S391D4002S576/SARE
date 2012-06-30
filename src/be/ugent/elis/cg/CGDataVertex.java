package be.ugent.elis.cg;

import be.elis.ugent.graphLibrary.Graph;

public class CGDataVertex extends CGVertex {
	public CGDataVertex(CGraph iCGraph) {
		super(iCGraph);
		
		iCGraph.dataVertices.add(this);
	}
	
	public CGDataVertex(CGraph iCGraph, String iName) {
	    super(iCGraph);
	    
	    name = iName;
		
		iCGraph.dataVertices.add(this);
    }
}
