package be.ugent.elis.cg;

import java.util.Vector;

import org.jscience.mathematics.numbers.Rational;
import org.jscience.mathematics.structures.Field_;

import be.elis.ugent.graphLibrary.Graph;
import be.elis.ugent.graphLibrary.Vertex;
import be.ugent.elis.AffineTransformation;

import be.ugent.elis.feautrierscheduler.GDGFeautrierScheduler;
import be.ugent.elis.piplibnative.PipProblem;
import be.ugent.elis.piplibnative.PipQuast;
import be.ugent.elis.piplibnative.PipProblem.USolutionType;
import be.ugent.elis.sare.domains.ParametrizedIntegerPolyhederDomain;

public class CGraph<VD, ED> extends Graph<VD, ED> {
	Vector<CGComputationVertex> computationVertices = new Vector<CGComputationVertex>();
	Vector<CGDataVertex> dataVertices = new Vector<CGDataVertex>();
	
    @Override public CGVertex<VD> addVertex(VD iData) {
        return new CGVertex<VD>(this, iData);
    }
	  
    public CGComputationVertex addComputationVertex() {
        return new CGComputationVertex(this);
    }
    
    public CGDataVertex addDataVertex() {
        return new CGDataVertex(this);
    }
    
    public CGVertex<VD> addVertex(VD iData, String iName) {
        return new CGVertex<VD>(this, iData, iName);
    }
	  
    public CGComputationVertex addComputationVertex(String iName) {
        return new CGComputationVertex(this, iName);
    }
    
    public CGDataVertex addDataVertex(String iName) {
        return new CGDataVertex(this, iName);
    }
    
    @Override public CGEdge addEdge(Vertex<VD> fromVertex, Vertex<VD> toVertex) {
        return new CGEdge(this, fromVertex, toVertex);
    }
    
    @Override public CGVertex getVertex(int index) {
    	return (CGVertex) super.getVertex(index);
    }
    
    @Override public CGEdge getEdge(int index) {
    	return (CGEdge) super.getEdge(index);
    }
    
    public boolean isInternalDataConsistent() {
    	return false;
    }    
    
    public boolean isExactAffineCG() {
    	for (int v = 0; v < getVertexCount(); v++) {
    		CGVertex vertex = getVertex(v);
    		
    		if (!vertex.isExactAffine()) return false;
    	}
    	
    	for (int e = 0; e < getEdgeCount(); e++) {
    		CGEdge edge = getEdge(e);
    		
    		if (!edge.isExactAffine()) return false;
    	}
    	
    	return true;
    }
}
