package be.ugent.elis.cg;

import java.util.Vector;

import be.elis.ugent.math.Vect;
import be.elis.ugent.math.structures.RingComparable;
import be.elis.ugent.math.structures.Ring_;
import be.elis.ugent.graphLibrary.Edge;
import be.elis.ugent.graphLibrary.Graph;
import be.elis.ugent.graphLibrary.Vertex;
import be.ugent.elis.AffineModuleRelation;
import be.ugent.elis.AffineTransformation;
import be.ugent.elis.sare.domains.ParametrizedIntegerPolyhederDomain;

public class RDMGraph<R extends RingComparable<R>> extends Graph<PolyhedralDomainVertexData<R>, AffineTransformation<R>> {
	R _R, zero, one;
	Vector<Vertex<PolyhedralDomainVertexData<R>>> computationVertices = new Vector<Vertex<PolyhedralDomainVertexData<R>>>();
	Vector<Vertex<PolyhedralDomainVertexData<R>>> dataVertices = new Vector<Vertex<PolyhedralDomainVertexData<R>>>();
	
	public RDMGraph(R i_R) {
		_R = i_R;
		zero = _R.getZero();
		one = _R.getOne();
	}
	
    public Vertex<PolyhedralDomainVertexData<R>> addComputationVertex(PolyhedralDomainVertexData<R> data) {
        Vertex<PolyhedralDomainVertexData<R>> v = new Vertex<PolyhedralDomainVertexData<R>>(this, data);
        
        computationVertices.add(v);
         
        return v;
    }
    
    public Vertex<PolyhedralDomainVertexData<R>> addDataVertex(PolyhedralDomainVertexData<R> data) {
        Vertex<PolyhedralDomainVertexData<R>> v = new Vertex<PolyhedralDomainVertexData<R>>(this, data);
        
        dataVertices.add(v);
        
        return v;
    }

    /**
     * Calculates the affinely approximated reduced symmetric dependence graph from the assymetric dependences
     */
	public RSDGraph<R> calculateAffinelyApproximatedRSDG() {
		int globalParameterCount = -1;
		for (int v = 0; v < computationVertices.size(); v++) {
			Vertex<PolyhedralDomainVertexData<R>> computationVertex = computationVertices.get(v);
			ParametrizedIntegerPolyhederDomain<R> compDomain = computationVertex.data.domain;
			assert (v == 0) || (globalParameterCount == compDomain.getParameterCount()); // We need to implement the hierarchical parameter-mapping before we can remove this
			globalParameterCount = compDomain.getParameterCount();
		}
		
		// Calculate vertex-pair spaces (result stored in new graph)
		RSDGraph<R> rsdg = new RSDGraph<R>(_R);
		for (int v = 0; v < computationVertices.size(); v++) {			
			rsdg.addVertex(computationVertices.get(v).data);
		}
		for (int v = 0; v < computationVertices.size(); v++) {
			Vertex<PolyhedralDomainVertexData<R>> vxV = computationVertices.get(v);
			Vertex<PolyhedralDomainVertexData<R>> resultVxV = rsdg.getVertex(v);
			ParametrizedIntegerPolyhederDomain<R> vxVCompDomain = vxV.data.domain; 
			for (int w = v; w < computationVertices.size(); w++) {
				Vertex<PolyhedralDomainVertexData<R>> vxW = computationVertices.get(w);
				Vertex<PolyhedralDomainVertexData<R>> resultVxW = rsdg.getVertex(w);
				ParametrizedIntegerPolyhederDomain<R> vxWCompDomain = vxW.data.domain;
				
				int localUnknownSpaceDim = vxVCompDomain.getDimension() + vxWCompDomain.getDimension();
				int localSpaceDim = vxVCompDomain.getDimension() + vxWCompDomain.getDimension() + globalParameterCount + 1;
				
				// Calculate solution-space for a partition for vertex-pair (v, w)
				Vector<AffineModuleRelation<R>> accessPairModules = new Vector<AffineModuleRelation<R>>();
				for (int a = 0; a < dataVertices.size(); a++) {
					Vertex<PolyhedralDomainVertexData<R>> vxData = dataVertices.get(a);
					
					Vector<Edge<AffineTransformation<R>>> edgesVToData = getEdges(vxV, vxData);
					Vector<Edge<AffineTransformation<R>>> edgesWToData = getEdges(vxW, vxData);
					for (int e = 0; e < edgesVToData.size(); e++) {
						AffineTransformation<R> atE = edgesVToData.get(e).data;
						assert (atE.getDestinationDimension() == vxData.data.domain.getDimension())
						     : String.format("Affine transformation destination dimension (%d) != dataVertex domain dimension (%d)",
						    		 atE.getDestinationDimension(), vxData.data.domain.getDimension()) ;
	
						for (int f = 0; f < edgesWToData.size(); f++) if ((v != w) || (e < f)) {
							AffineTransformation<R> atF = edgesWToData.get(f).data;
							assert (atE != atF);
							
							// Fill in null-space vectors obtained through data-access-equation
							Vector<Vect<R>> generatingVertices = new Vector<Vect<R>>();
							for (int d = 0; d < vxData.data.domain.getDimension(); d++) {
								R[] elms = (R[]) new RingComparable[localSpaceDim];
								
								// Domain
								copyTrafoDimToVertex(elms, 0, atE, 
										            d, 0, vxV.data.domain.getDimension());
								copyTrafoDimToVertex(elms, vxV.data.domain.getDimension(), atF, 
										            d, 0, vxW.data.domain.getDimension());
								
								// Parameter-domain & the constant
								copyTrafoDimToVertex(elms, localUnknownSpaceDim, atE, 
										            d, vxV.data.domain.getDimension(), vxVCompDomain.getParameterCount() + 1); 
								substractTrafoDimToVertex(elms, localUnknownSpaceDim, atF, 
										            d, vxW.data.domain.getDimension(), vxWCompDomain.getParameterCount() + 1);
								
								// XXX: We may want to avoid unused dimensions for computational efficiency
								
								generatingVertices.add(new Vect<R>(elms));
							}
							rsdg.addEdge(resultVxV, resultVxW,
								new AffineModuleRelation<R>(_R, generatingVertices,
										vxV.data.domain.getDimension(), vxW.data.domain.getDimension(),
										globalParameterCount + 1));
							
							// Now for the other direction... XXX: This should be avoidable
							generatingVertices = new Vector<Vect<R>>();
							for (int d = 0; d < vxData.data.domain.getDimension(); d++) {
								R[] elms = (R[]) new RingComparable[localSpaceDim];
								
								// Domain
								copyTrafoDimToVertex(elms, 0, atF, 
										            d, 0, vxW.data.domain.getDimension());
								copyTrafoDimToVertex(elms, vxW.data.domain.getDimension(), atE, 
										            d, 0, vxV.data.domain.getDimension());
								
								// Parameter-domain & the constant
								copyTrafoDimToVertex(elms, localUnknownSpaceDim, atF, 
										            d, vxW.data.domain.getDimension(), vxWCompDomain.getParameterCount() + 1); 
								substractTrafoDimToVertex(elms, localUnknownSpaceDim, atE, 
										            d, vxV.data.domain.getDimension(), vxVCompDomain.getParameterCount() + 1);
								
								// XXX: We may want to avoid unused dimensions for computational efficiency
								
								generatingVertices.add(new Vect<R>(elms));
							}
							rsdg.addEdge(resultVxW, resultVxV,
								new AffineModuleRelation<R>(_R, generatingVertices, 
										vxW.data.domain.getDimension(), vxV.data.domain.getDimension(),
										globalParameterCount + 1));

						}
					}
				}		
				
				if (v == w) { // Intersect accessPairModules with coefficient equality constraints
					// THINK: So how can this be done more efficiently?
					// THINK: And what about the constants?  Should the unknown dimensions be treated equal to the constants above for v = w?
					Vector<Vect<R>> vvR = new Vector<Vect<R>>();
					for (int d = 0; d < vxV.data.domain.getDimension(); d++) {
						R[] elms = (R[]) new RingComparable[localSpaceDim];
						for (int i = 0; i < elms.length; i++) elms[i] = zero;
						
						elms[d] = one;
						elms[vxV.data.domain.getDimension() + d] = one;
						
						vvR.add(new Vect<R>(elms));
					}
					
					rsdg.addBiDirEdge(resultVxV, resultVxW, 
							new AffineModuleRelation<R>(_R, vvR, 
										vxV.data.domain.getDimension(), vxW.data.domain.getDimension(),
										globalParameterCount + 1));
				}
			}
		}
		
		return rsdg;
	}

	private void copyTrafoDimToVertex(R[] vertex, int vertexOffset, AffineTransformation<R> affineTrafo, int dimension, 
			int transformationOffset, int transformationLength) {
		for (int p = 0; p < transformationLength; p++) {
			vertex[vertexOffset + p]
			     = affineTrafo.transformationMatrix.get(dimension, transformationOffset + p);
		}
	}

	private void substractTrafoDimToVertex(R[] vertex, int vertexOffset, AffineTransformation<R> affineTrafo, int dimension, 
			int transformationOffset, int transformationLength) {		
		for (int p = 0; p < transformationLength; p++) {
			vertex[vertexOffset + p]
			     = vertex[vertexOffset + p].minus(affineTrafo.transformationMatrix.get(dimension, transformationOffset + p));
		}
	}
	
	/*
	 * Affinely approximated RSDG calculation followed by transitive closure 
	 */
	public RSDGraph<R> fastSpacePartitioning() {
		return (RSDGraph<R>) calculateAffinelyApproximatedRSDG().calcTransitiveClosure();
	}
}
