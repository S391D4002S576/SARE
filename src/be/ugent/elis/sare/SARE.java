package be.ugent.elis.sare;

import java.util.Vector;

import be.ugent.elis.AffineTransformation;
import be.ugent.elis.dfg.GDGEdge;
import be.ugent.elis.dfg.GDGraph;
import be.ugent.elis.dfg.GDGVertex;
import be.ugent.elis.sare.domains.Domain;

public class SARE {
    Vector<Variable> variables = new Vector<Variable>();
    
    public Variable addVariable() {
    	Variable v = new Variable(variables.size());
    	
    	variables.add(v);
    	
    	return v; 
    }
    
    public Variable addVariable(String iName) {
    	Variable result = addVariable();
    	
    	result.name = iName;
    	
    	return result;
    }
    
    public GDGraph generateDFGGraph() {
    	GDGraph result = new GDGraph();
    	
    	// Each variable translates to vertex
    	for (int v = 0; v < variables.size(); v++) {
    		Variable variable = variables.get(v);
    		GDGVertex vertex = result.addVertex(variable.name);
    		
    		vertex.computationDomain = variable.computationDomain; // XXX: Should we clone the domain?
    		vertex.isReady = variable.isReady;
    	}
    	
    	// Each variableDependence translates to an edge
    	for (int v = 0; v < variables.size(); v++) {
    		Variable variable = variables.get(v);
    		GDGVertex vertex = result.getVertex(v);
    		
    		Operation operation = variable.producingOperation;
    		if (operation != null) {
        		for (int d = 0; d < operation.consumedVariableDependencies.length; d++) {
        			VariableDependence varDep = operation.consumedVariableDependencies[d];
        			
        			GDGEdge edge = result.addEdge(vertex, result.getVertex(varDep.consumedVariable.getIndex()));
        			edge.dataDependence = varDep.dependenceTransformation;
        			edge.producedDomain = varDep.producedVariableDependenceDomain;
        		}
    		}
    	}
    	
    	return result;
    }
    
    public String convertToTextualRepresentation() {
    	StringBuilder sb = new StringBuilder();
    	String[] indexVariables = new String[]{"i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u"};
    	String[] parameterVariables = new String[]{"v", "w", "x", "y", "z", "a", "b", "c", "d", "e", "f", "g", "h"};
    	
    	// List Variables
    	sb.append(String.format("Variables (%d): {\n", variables.size()));
    	for (int q = 0; q < variables.size(); q++) {
    		Variable var = variables.get(q);
    		sb.append(String.format("  %s: (\n", var.name));
    		
    		Domain compDomain = var.computationDomain;
    		if (compDomain != null) {
    			String sourcePoint = "";
    			for (int f = 0; f < compDomain.getDimension(); f++) {
    				if (f != 0) sourcePoint += ", ";
    				sourcePoint += indexVariables[f]; 
    			}
        		sb.append(String.format("    %s(%s) = %s(", var.name, sourcePoint, var.producingOperation.operator.getName()));
        		
        		VariableDependence[] varDeps = var.producingOperation.consumedVariableDependencies;
        		for (int p = 0; p < varDeps.length; p++) {
        			if (p != 0) sb.append(", ");
        			
        			if (varDeps[p].dependenceTransformation.getClass() == AffineTransformation.class) {
        				AffineTransformation affineDepTrafo = (AffineTransformation) varDeps[p].dependenceTransformation;
            			sb.append(String.format("%s(%s)", varDeps[p].consumedVariable.name,
            					affineDepTrafo.getTransformedIndexesAsText(indexVariables, parameterVariables)));
        			} else {
            			sb.append(String.format("%s(?)", varDeps[p].consumedVariable.name));
        			}
        		}
    		} else sb.append("Computation Domain not specified!");
    		
    		sb.append(String.format("  )\n", variables.get(q).name));
    	}
    	sb.append("}\n");
    	
    	return sb.toString();
    }
}
