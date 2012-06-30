package be.ugent.elis.cg;

import java.util.Vector;

import org.jscience.mathematics.functions.ComparatorClass;
import org.jscience.mathematics.numbers.Rational;

import be.elis.ugent.graphLibrary.Edge;
import be.elis.ugent.math.Module;
import be.elis.ugent.math.VectorOfModules;
import be.elis.ugent.math.Vect;
import be.elis.ugent.math.structures.RingComparable;
import be.elis.ugent.math.structures.Ring_;
import be.ugent.elis.AffineTransformation;
import be.ugent.elis.sare.domains.ParametrizedIntegerPolyhederDomain;

public class CGSpacePartitioner<R extends RingComparable<R>> {
	R genericInstance;
	
	CGraph cGraph;
	
	public int verbosity = 0;
	
	public Module<R> spacePartitionSpace;
	
	public CGSpacePartitioner(R iGenericInstance, CGraph iCGraph, ComparatorClass<R> order, int iVerbosity) {
		genericInstance = iGenericInstance;
		
		cGraph = iCGraph;
		
		verbosity = iVerbosity;
		
		partition(order);		
	}
	
	void partition(ComparatorClass<R> order) {
		assert cGraph.isExactAffineCG() : "Graph is not an exact affine CG";

		R zero = genericInstance.getAddition().getIdentityElement();
		R one = genericInstance.getMultiplication().getIdentityElement();
		
		int globalParameterCount = -1;
		
		int[] compVertexUnknownOffset = new int[cGraph.computationVertices.size()];
		int unknownCount = 0;
		for (int v = 0; v < cGraph.computationVertices.size(); v++) {
			CGComputationVertex computationVertex = cGraph.computationVertices.get(v);
			compVertexUnknownOffset[v] = unknownCount;
			ParametrizedIntegerPolyhederDomain compDomain = (ParametrizedIntegerPolyhederDomain) computationVertex.domain;
			unknownCount += compDomain.getDimension() + compDomain.getParameterCount() + 1;
			assert (v == 0) || (globalParameterCount == compDomain.getParameterCount()); // We need to implement the hierarchical parameter-mapping before we can remove this
			globalParameterCount = compDomain.getParameterCount();
		}
		int spaceDim = unknownCount;
		
		// Calculate vertex-pair spaces 
		VectorOfModules<R> vertexPairSpaces = new VectorOfModules<R>(genericInstance);
		for (int v = 0; v < cGraph.computationVertices.size(); v++) {
			CGComputationVertex vxV = cGraph.computationVertices.get(v);
			for (int w = v; w < cGraph.computationVertices.size(); w++) {
				CGComputationVertex vxW = cGraph.computationVertices.get(w);

				ParametrizedIntegerPolyhederDomain<R> vxVCompDomain = (ParametrizedIntegerPolyhederDomain<R>) vxV.domain; 
				ParametrizedIntegerPolyhederDomain<R> vxWCompDomain = (ParametrizedIntegerPolyhederDomain<R>) vxW.domain;
				int vxVParmOffs = vxVCompDomain.getDimension();
				int vxWParmOffs = vxWCompDomain.getDimension();
				int vxWUnknOffs = vxVParmOffs + vxVCompDomain.getParameterCount() + 1;				
				
				// Calculate solution-space for a partition for vertex-pair (v, w)
				VectorOfModules<R> accessPairModules = new VectorOfModules<R>(genericInstance);
				accessPairModules.verbosity = verbosity;
				int localSpaceDim = vxWUnknOffs + (vxWCompDomain.getDimension() + vxWCompDomain.getParameterCount() + 1); 
				for (int a = 0; a < cGraph.dataVertices.size(); a++) {
					CGDataVertex dataVertex = cGraph.dataVertices.get(a);
					
					Vector<Edge> compToDataEdgesV = cGraph.getEdges(vxV, dataVertex);
					Vector<Edge> compToDataEdgesW = cGraph.getEdges(vxW, dataVertex);
					
					for (int e = 0; e < compToDataEdgesV.size(); e++) {
						CGEdge edgeE = (CGEdge) compToDataEdgesV.get(e);
						AffineTransformation<R> atE = (AffineTransformation<R>) (edgeE.dataDependence);
						assert (atE.getDestinationDimension() == dataVertex.domain.getDimension())
						     : String.format("Affine transformation destination dimension (%d) != dataVertex domain dimension (%d)",
						    		 atE.getDestinationDimension(), dataVertex.domain.getDimension()) ;

						for (int f = 0; f < compToDataEdgesW.size(); f++) if ((v != w) || (e < f)) {
							CGEdge edgeF = (CGEdge) compToDataEdgesW.get(f);
							AffineTransformation<R> atF = (AffineTransformation<R>) (edgeF.dataDependence);
							assert (atE != atF);
							
							Module<R> solutionSpace = new Module<R>(genericInstance, localSpaceDim);
							
							// Fill in null-space vectors obtained through data-access-equation
							for (int d = 0; d < dataVertex.domain.getDimension(); d++) {
								R[] elms = (R[]) new Ring_[localSpaceDim];
								for (int i = 0; i < elms.length; i++) elms[i] = zero;
								
								// Domain
								addTrafoDimToVertex(elms, 0, atE, 
										            d, 0, vxV.domain.getDimension());
								addTrafoDimToVertex(elms, vxWUnknOffs, atF, 
										            d, 0, vxW.domain.getDimension());
								
								// Parameter-domain & the constant
								addTrafoDimToVertex(elms, 0, atE, 
										            d, vxVParmOffs, vxVCompDomain.getParameterCount() + 1); 
								addTrafoDimToVertex(elms, vxWUnknOffs, atF, 
										            d, vxWParmOffs, vxWCompDomain.getParameterCount() + 1); 
								
								solutionSpace.generatingVertices.add(new Vect<R>(elms));
							}
							
							// Fill in nullspace-vectors resulting from the equality of a subdomain of the parameter & constant vectorspace
							// XXX: So, it should only be done for the constant and the common parameters, not like we're doing it now -- this will need special care when we build the hierarchical parameter handling
							for (int d = 0; d <= globalParameterCount; d++) {
								R[] elms = (R[]) new Ring_[localSpaceDim];
								for (int i = 0; i < elms.length; i++) elms[i] = zero;
								
								elms[vxVParmOffs + d] = one;
								elms[vxWUnknOffs + vxWParmOffs + d] = one;
								
								solutionSpace.generatingVertices.add(new Vect<R>(elms));
							}
							
							accessPairModules.add(solutionSpace);
						}
					}
				}		
				
				if (v == w) { // Intersect accessPairModules with coëfficient equality constraints
					Module<R> solutionSpace = new Module<R>(genericInstance, localSpaceDim);
					
					for (int d = 0; d < vxWUnknOffs; d++) {
						R[] elms = (R[]) new Ring_[localSpaceDim];
						for (int i = 0; i < elms.length; i++) elms[i] = zero;
						
						elms[d] = one;
						elms[vxWUnknOffs + d] = one;
						
						solutionSpace.generatingVertices.add(new Vect<R>(elms));
					}
					
					accessPairModules.add(solutionSpace);
				}
				
				Module<R> accessPairIntersection = accessPairModules.calculateIntersection(order);
				Module<R> globalAccessPairIntersection = new Module<R>(genericInstance, spaceDim); 
				
				// Generate global (all vertex) space for the constraints of this vertex-pair
				// Copy vertexPairSpace basis to global space basis
				for (int g = 0; g < accessPairIntersection.generatingVertices.size(); g++) {
					Vect<R> genVect = accessPairIntersection.generatingVertices.get(g);
					R[] elms = (R[]) new Ring_[spaceDim];
					for (int i = 0; i < elms.length; i++) elms[i] = zero;

					for (int u = 0; u < vxVCompDomain.getDimension() + vxVCompDomain.getParameterCount() + 1; u++) {
						elms[compVertexUnknownOffset[vxV.computationVertexIndex] + u] = genVect.elements[u]; 
					}
					
					if (v != w) {
						for (int u = 0; u < vxWCompDomain.getDimension() + vxWCompDomain.getParameterCount() + 1; u++) {
							elms[compVertexUnknownOffset[vxW.computationVertexIndex] + u] = genVect.elements[vxWUnknOffs + u]; 
						}
					}
					
					Vect<R> nullVect = new Vect<R>(elms);
					globalAccessPairIntersection.generatingVertices.add(nullVect);
				}
				
				// Add generating basis for space outside vertexpair space
				for (int u = 0; u < cGraph.computationVertices.size(); u++) {
					if ((u != v) && (u != w)) {
						CGComputationVertex vertexU = cGraph.computationVertices.get(u);
						ParametrizedIntegerPolyhederDomain vertexUCompDomain = (ParametrizedIntegerPolyhederDomain) vertexU.domain;
						for (int d = 0; d < vertexUCompDomain.getDimension() + vertexUCompDomain.getParameterCount() + 1; d++) {
							R[] elms = (R[]) new Ring_[spaceDim];
							for (int i = 0; i < elms.length; i++) elms[i] = zero;
							
							elms[compVertexUnknownOffset[vertexU.computationVertexIndex] + d]
							     = one;
							
							Vect<R> nullVect = new Vect<R>(elms);
							globalAccessPairIntersection.generatingVertices.add(nullVect);
						}
					}
				}
				
				vertexPairSpaces.add(globalAccessPairIntersection);
			}
		}
		vertexPairSpaces.verbosity = verbosity;
		spacePartitionSpace = vertexPairSpaces.calculateIntersection(order);
	}

	private void addTrafoDimToVertex(R[] vertex, int vertexOffset, 
			AffineTransformation<R> affineTrafo, int dimension, 
			int transformationOffset, int transformationLength) {
		
		for (int p = 0; p < transformationLength; p++) {
			vertex[vertexOffset + transformationOffset + p]
			     = vertex[vertexOffset + transformationOffset + p].plus(
			    		 affineTrafo.transformationMatrix.get(dimension, transformationOffset + p)
			       );
		} // Plus, since otherwise we would get a basis for Omega_{V} concat -Omega_{W}
	}
}
