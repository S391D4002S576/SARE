package be.ugent.elis.dfg;

import java.util.Vector;

import org.jscience.mathematics.numbers.Rational;
import org.jscience.mathematics.structures.Field_;

import be.elis.ugent.graphLibrary.Graph;
import be.elis.ugent.graphLibrary.Vertex;
import be.elis.ugent.math.structures.RingComparable;
import be.ugent.elis.AffineTransformation;

import be.ugent.elis.feautrierscheduler.GDGFeautrierScheduler;
import be.ugent.elis.piplibnative.PipProblem;
import be.ugent.elis.piplibnative.PipQuast;
import be.ugent.elis.piplibnative.PipProblem.USolutionType;
import be.ugent.elis.sare.domains.ParametrizedIntegerPolyhederDomain;

public class GDGraph extends Graph {
    @Override public GDGVertex addVertex() {
        return new GDGVertex(this);
    }
	  
    @Override public GDGEdge addEdge(Vertex fromVertex, Vertex toVertex) {
        return new GDGEdge(this, fromVertex, toVertex);
    }
    
    @Override public GDGVertex getVertex(int index) {
    	return (GDGVertex) super.getVertex(index);
    }
    
    @Override public GDGEdge getEdge(int index) {
    	return (GDGEdge) super.getEdge(index);
    }
    
    public GDGVertex addVertex(String iName) {
        return new GDGVertex(this, iName);
    }
	  
    public boolean isInternalDataConsistent() {
    	return false;
    }    
    
    public void calculateFeautrierSchedule(boolean parameterDependentMode, boolean parameterContextSensitive) {
    	GDGFeautrierScheduler<Rational> scheduler 
			= new GDGFeautrierScheduler<Rational>(this, parameterDependentMode, parameterContextSensitive, null, null, Rational.ZERO, Rational.ONE);
    }
    
    public boolean isExactAffineDFG() {
    	for (int v = 0; v < getVertexCount(); v++) {
    		GDGVertex vertex = getVertex(v);
    		
    		if ((vertex.computationDomain != null) && 
    		    !(vertex.computationDomain instanceof ParametrizedIntegerPolyhederDomain))  return false;
    	}
    	
    	for (int e = 0; e < getEdgeCount(); e++) {
    		GDGEdge edge = getEdge(e);
    		
    		if ((edge.producedDomain != null) && (edge.dataDependence != null)) {
        		if (!(edge.producedDomain instanceof ParametrizedIntegerPolyhederDomain)) return false;
        		if (!(edge.dataDependence instanceof AffineTransformation)) return false;
    		}
    	}
    	
    	return true;
    }

    public boolean allSchedulesAreAffine() {
    	for (int v = 0; v < getVertexCount(); v++) {
    		GDGVertex vertex = getVertex(v);
    		if (!vertex.isReady) {
    			if (vertex.timeSchedule == null) return false;
    			if (!(vertex.timeSchedule instanceof AffineTransformation)) return false;
    		}
    	}
    	
    	return true;
    }
    
    public <R extends RingComparable<R>> void codeGeneration(R zero, R one) {
    	assert isExactAffineDFG();
    	assert allSchedulesAreAffine();
    	
    	// Get a vector of vertices to schedule
    	Vector<GDGVertex> schedVertex = new Vector<GDGVertex>();
    	for (int v = 0; v < getVertexCount(); v++) {
    		GDGVertex vertex = getVertex(v);
    		if (!vertex.isReady) schedVertex.add(vertex);
    	}
    	
    	// Build code for intersected domain
    	// Calculate intersected domain
    	ParametrizedIntegerPolyhederDomain<R>[] compDomains 
    		= (ParametrizedIntegerPolyhederDomain<R>[]) new ParametrizedIntegerPolyhederDomain[schedVertex.size()];
    	for (int v = 0; v < schedVertex.size(); v++) {
    		GDGVertex vertex = schedVertex.get(v);
    		compDomains[v] = (ParametrizedIntegerPolyhederDomain<R>) vertex.computationDomain;
    	}
       	ParametrizedIntegerPolyhederDomain<R> schedPipd 
       		= ParametrizedIntegerPolyhederDomain.<R>exfrusion(compDomains, zero, one);

       	System.out.println(schedPipd.getDomainConstraintsDescriptionAsText(new String[]{"i", "j", "m", "n"}, new String[]{}));
       	// Generate code
       	//for (int d = 0; d < )
    }
}
