package be.ugent.elis;

import java.util.Vector;

import be.elis.ugent.graphLibrary.Graph;
import be.elis.ugent.math.Module;
import be.elis.ugent.math.Vect;
import be.elis.ugent.math.operations.classes.BinaryOperationClass;
import be.elis.ugent.math.operations.interfaces.BinaryOperation;
import be.elis.ugent.math.structures.Ring_;

public class AffineModuleRelation<R extends Ring_<R>> extends Module<R> {
	public int pDimension, qDimension, constantDimension;
	
	public String toString() {
		return String.format("P-dim: %d, Q-dim: %d, constant-dim: %d\n%s", pDimension, qDimension, constantDimension, super.toString());
	}
	
	public boolean equals(AffineModuleRelation<R> other) {
		return (pDimension == other.pDimension) && (qDimension == other.qDimension) && (constantDimension == other.constantDimension)
			   && super.equals(other);
	}
	
	public boolean isInternalDataConsistent() {
		boolean generatingVxOK = true;
		for (int q = 0; q < generatingVertices.size(); q++) {
			generatingVxOK = generatingVxOK && generatingVertices.get(q).elements.length == superSpaceDimension;
		}
		
		return generatingVxOK && (superSpaceDimension == pDimension + qDimension + constantDimension);
	}
	
	/** The following layout of the dimensions in the vectors is assumed:
	 *  P-Dimensions ++ Q-Dimensions ++ Difference of constants dimensions (Parametric constants go first) 
	 */
	public AffineModuleRelation(R iGenericInstance, Vector<Vect<R>> iGeneratingVertices, 
			int iPDimension, int iQDimension, int iConstantDimension) {
		super(iGenericInstance, iGeneratingVertices);
		
		pDimension = iPDimension;
		qDimension = iQDimension;
		constantDimension = iConstantDimension;
		
		superSpaceDimension = pDimension + qDimension + constantDimension;
		
		assert isInternalDataConsistent();
	}
	
	public static class ConjunctionOperation<G extends Ring_<G>> extends BinaryOperationClass<AffineModuleRelation<G>> {
		Module.IntersectionOperation<G> moduleIntersection;
		
		public ConjunctionOperation(G i_G) { 
			moduleIntersection = new Module.IntersectionOperation<G>(i_G);
		}
		
		public AffineModuleRelation<G> getResult(AffineModuleRelation<G>[] arguments) {
			Module<G> intersection = moduleIntersection.getResult(arguments);
			
			int pDim = (arguments.length > 0) ? arguments[0].pDimension : 0;
			int qDim = (arguments.length > 0) ? arguments[0].qDimension : 0;
			int constDim = (arguments.length > 0) ? arguments[0].constantDimension : 0;
			
			return new AffineModuleRelation<G>(intersection._R, intersection.generatingVertices, pDim, qDim, constDim);
		}
	}
	
	public static class AfterOperation<G extends Ring_<G>> extends BinaryOperationClass<AffineModuleRelation<G>> {
		G _G, zero, one;
		Module.IntersectionOperation<G> moduleIntersection;		
		
		public AfterOperation(G i_G) { 
			_G = i_G;
			zero = _G.getZero();
			one = _G.getOne();
			 moduleIntersection = new Module.IntersectionOperation<G>(_G);
		}
		
		public AffineModuleRelation<G> getResult(AffineModuleRelation<G> a, AffineModuleRelation<G> b) {
			assert (a.qDimension == b.pDimension);
			assert (a.constantDimension == b.constantDimension); // This may have to be changed if the relations only carry coefficients for the relevant parameters
			
			// Create new modules which add the co-dimensions
			int imdDim = a.pDimension + a.qDimension + a.constantDimension + b.qDimension + b.constantDimension; 
			Vector<Vect<G>> aXVects = new Vector<Vect<G>>(); 
			for (int i = 0; i < a.generatingVertices.size(); i++) { // A
				G[] orgV = a.generatingVertices.get(i).elements;
				G[] newV = (G[]) new Ring_[imdDim]; // Layout: see imdDim expression
				
				for (int d = 0; d < a.superSpaceDimension; d++) newV[d] = orgV[d]; // Copy all of A
				for (int z = a.superSpaceDimension; z < newV.length; z++) newV[z] = zero; // Fill remaining dims with zeroes
				
				aXVects.add(new Vect<G>(newV));
			}
			for (int q = 0; q < b.qDimension + b.constantDimension; q++) { // New degree of freedom for each co-dimension
				G[] newV = (G[]) new Ring_[imdDim]; // Layout: see imdDim expression
				
				for (int d = 0; d < imdDim; d++) {
					newV[d] = (d - q- a.superSpaceDimension == 0) ? one : zero;
				}
				
				aXVects.add(new Vect<G>(newV));
			}
			
			Vector<Vect<G>> bXVects = new Vector<Vect<G>>();
			for (int i = 0; i < b.generatingVertices.size(); i++) { // B
				G[] orgV = b.generatingVertices.get(i).elements;
				G[] newV = (G[]) new Ring_[imdDim]; // Layout: see imdDim expression
				
				for (int q = 0; q < a.pDimension; q++) newV[q] = zero; // Fill a-P with zeroes
				int dOfs = a.pDimension;
				for (int p = 0; p < b.pDimension; p++) newV[dOfs + p] = orgV[p]; // Copy P
				for (int q = a.pDimension + a.qDimension; q < a.superSpaceDimension; q++) newV[q] = zero; // Fill a-Constants with zeroes
				dOfs = a.superSpaceDimension;
				int sOfs = b.pDimension;
				for (int q = 0; q < b.qDimension + b.constantDimension; q++) newV[dOfs + q] = orgV[sOfs + q]; // Copy Q & Constants
				
				bXVects.add(new Vect<G>(newV));
			}
			for (int q = 0; q < a.pDimension; q++) { // New degree of freedom for each co-dimension
				G[] newV = (G[]) new Ring_[imdDim]; // Layout: see imdDim expression
				
				for (int d = 0; d < imdDim; d++) {
					newV[d] = (d - q == 0) ? one : zero;
				}
				
				bXVects.add(new Vect<G>(newV));
			}
			for (int q = 0; q < a.constantDimension; q++) { // New degree of freedom for each co-dimension
				G[] newV = (G[]) new Ring_[imdDim]; // Layout: see imdDim expression
				
				for (int d = 0; d < imdDim; d++) {
					newV[d] = (d - q - (a.pDimension + a.qDimension) == 0) ? one : zero;
				}
				
				bXVects.add(new Vect<G>(newV));
			}

			// Intersect these module to get the three-way relation
			Module<G> intersection = moduleIntersection.getResult((Module<G>[]) new Module<?>[]{
											new Module<G>(_G, aXVects), new Module<G>(_G, bXVects)
										}); 

			// Project the intersection onto the relevant dimensions & extract the constant difference dimensions
			Vector<Vect<G>> resultVs = new Vector<Vect<G>>();
			int resultDim = a.pDimension + b.qDimension + b.constantDimension;
			for (int i = 0; i < intersection.generatingVertices.size(); i++) {
				G[] orgV = intersection.generatingVertices.get(i).elements;
				G[] newV = (G[]) new Ring_[resultDim];
				
				for (int p = 0; p < a.pDimension; p++) newV[p] = orgV[p];
				int dOfs = a.pDimension;
				int sOfs = a.superSpaceDimension;
				for (int q = 0; q < b.qDimension; q++) newV[dOfs + q] = orgV[sOfs + q];
				
				/* Since cA[i] = cAl[i] - cAr[i]  // How to calculate the constant diffs from the constant diffs    
				 *       cB[i] = cBl[i] - cBr[i]
				 *       cR[i] = cRl[i] - cRr[i]
				 *       cAr = cBl
				 *       cRl = cAl
				 *       cBr = cRr
				 * we have cA[i] = cRl[i] - cAr[i] 
				 *         cB[i] = cAr[i] - cRr[i]
				 * and finally cR[i] = cA[i] + cB[i]	        
				 */ 
				dOfs += b.qDimension;
				int sOfsA = a.pDimension + a.qDimension;
				int sOfsB = a.superSpaceDimension + b.qDimension;
				for (int c = 0; c < b.constantDimension; c++) newV[dOfs + c] = orgV[sOfsA + c].plus(orgV[sOfsB + c]);
				
				resultVs.add(new Vect<G>(newV));
			}
			
			return new AffineModuleRelation<G>(_G, resultVs, a.pDimension, b.qDimension, b.constantDimension);
		}
	}
}
