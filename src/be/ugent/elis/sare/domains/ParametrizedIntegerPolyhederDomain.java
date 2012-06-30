package be.ugent.elis.sare.domains;

import java.util.LinkedList;
import java.util.List;

import org.jscience.mathematics.numbers.Rational;
import org.jscience.mathematics.structures.Field_;
import org.jscience.mathematics.vectors.DenseMatrix;
import org.jscience.mathematics.vectors.Matrix;

import be.elis.ugent.math.derivedsets.Fraction;
import be.elis.ugent.math.derivedsets.Interval;
import be.elis.ugent.math.structures.RingComparable;
import be.elis.ugent.math.structures.Ring_;
import be.ugent.elis.AffineTransformation;
import be.ugent.elis.sare.ConditionalSolutionPartitionTree;

public class ParametrizedIntegerPolyhederDomain<R extends RingComparable<R>> extends Domain implements PolyhedralIntegralLattice {
	public Matrix<R> domainConstraintMatrix = null; // (domainConstraintCount) x (unknownCount + parameterCount + 1 (additive constant))
	public Matrix<R> parameterConstraintMatrix = null; // (parameterConstraintCount) x (parameterCount + 1 (additive constant))
	
	R zero;
	R one;
	
	ParametrizedIntegerPolyhederDomain<R> parentPIPD;
	
	@Override public int getDimension() {
		return domainConstraintMatrix.getNumberOfColumns() - 1 - getParameterCount();
	}
	
	public boolean isInternalDataConsistent() {
		return (domainConstraintMatrix != null) && (parameterConstraintMatrix != null);
	}
	
	public int getDomainConstraintCount() {
		return domainConstraintMatrix.getNumberOfRows();
	}
	
	public int getParameterConstraintCount() {
		return parameterConstraintMatrix.getNumberOfRows();
	}
	
	public static <R extends Ring_<R>> Matrix<R> fromMatrixDiagonalBlockAffine(Matrix<R>[] diagonalBlocksAffine, R zero, R one) {
		Matrix<R>[] diagonalBlocks = (Matrix<R>[]) new Matrix[diagonalBlocksAffine.length];
		Matrix<R>[] homogeneousCoordBlocks = (Matrix<R>[]) new Matrix[diagonalBlocksAffine.length];
		
		// Fill arrays
		for (int q = 0; q < diagonalBlocksAffine.length; q++) {
			diagonalBlocks[q] = diagonalBlocksAffine[q].subMatrix(0, diagonalBlocksAffine[q].getNumberOfRows(), 
					                                              0, (diagonalBlocksAffine[q].getNumberOfColumns() - 1));
			homogeneousCoordBlocks[q] = diagonalBlocksAffine[q].subMatrix(0, diagonalBlocksAffine[q].getNumberOfRows(), 
					                                                      (diagonalBlocksAffine[q].getNumberOfColumns() - 1), 1);
		}
		
		Matrix<R> linearM = Matrix.<R>fromBlockDiagonalMatrix(diagonalBlocks, zero);
		Matrix<R> homogeneousM = Matrix.<R>augmentVertically(homogeneousCoordBlocks);
		
		Matrix<R>[][] blocks
			= (Matrix<R>[][]) new Matrix[][]{{linearM, homogeneousM},
                             {Matrix.<R>zeroMatrix(1, linearM.getNumberOfColumns(), zero), Matrix.<R>unitMatrix(1, 1, zero, one)}};
		
		return Matrix.<R>fromBlockMatrix(blocks).getHomogenizedMatrix(zero, one);
	}

    public Matrix<R> getCombinedConstraintMatrix() {
    	return fromMatrixDiagonalBlockAffine((Matrix<R>[]) new Matrix[]{domainConstraintMatrix, parameterConstraintMatrix}, zero, one);
    }
	
	public ParametrizedIntegerPolyhederDomain(Matrix<R> iDomainConstraintMatrix, Matrix<R> iParameterConstraintMatrix, R iZero, R iOne) {
		domainConstraintMatrix = iDomainConstraintMatrix;
		parameterConstraintMatrix = iParameterConstraintMatrix;
		
		zero = iZero;
		one = iOne;
		
		if (parameterConstraintMatrix == null) { // Make an empty constraint matrix
			parameterConstraintMatrix = DenseMatrix.valueOf((R[][]) new Ring_[0][0]);
		}
		
		assert isInternalDataConsistent();
	}	
	
	public int getParameterCount() {
		return Math.max(parameterConstraintMatrix.getNumberOfColumns() - 1, 0);
	}

	public ParametrizedIntegerPolyhederDomain<R> getTransformedPolyheder(AffineTransformation<R> transformation) {
		assert domainConstraintMatrix.isInternalDataConsistent() && transformation.transformationMatrix.isInternalDataConsistent();
		
		Matrix<R> T = transformation.transformationMatrix;
		Matrix<R> extendedTransformationMatrix 
			= Matrix.<R>augmentVertically((Matrix<R>[]) new Matrix[]{
					T.subMatrix(0, (T.getNumberOfRows() - 1), 0, T.getNumberOfColumns()),
					Matrix.<R>augmentHorizontally((Matrix<R>[]) new Matrix[]{
						Matrix.<R>zeroMatrix(getParameterCount() + 1, getDimension(), zero),
						Matrix.<R>unitMatrix(getParameterCount() + 1, getParameterCount() + 1, zero, one)
					})
			});
		
		return new ParametrizedIntegerPolyhederDomain<R>(domainConstraintMatrix.times(extendedTransformationMatrix, zero),
				                  parameterConstraintMatrix, zero, one);
	}
	
	public static <R extends RingComparable<R>> ParametrizedIntegerPolyhederDomain<R> intersection(ParametrizedIntegerPolyhederDomain<R>[] domains, R zero, R one) {
		assert domains.length > 0; // XXX: We could make this work for empty domains too 
		
		int dim = domains[0].getDimension();
		int parCount = domains[0].getParameterCount();
		
		// Copy constraints
		Matrix<R>[] domainConstraintMatrices = (Matrix<R>[]) new Matrix[domains.length];
		Matrix<R>[] parameterConstraintMatrices = (Matrix<R>[]) new Matrix[domains.length];
		for (int d = 0; d < domains.length; d++) {
			assert (dim == domains[d].getDimension()) && (parCount == domains[d].getParameterCount());
			domainConstraintMatrices[d] = domains[d].getDomainConstraintMatrix();
			parameterConstraintMatrices[d] = domains[d].getParameterConstraintMatrix();
		}		
		
		//result.parameterConstraintMatrix = augmentVertically(parameterConstraintMatrices);
		System.err.println("What to do with parameter constraints on polyheder intersection?");

		Matrix<Rational> intersectionM = (Matrix<Rational>) Matrix.<R>augmentVertically(domainConstraintMatrices);
		
		return new ParametrizedIntegerPolyhederDomain<R>( 
					(Matrix<R>) eliminateConstraints(intersectionM, true), null, zero, one);
	}

	public void simplifyDomainConstraintMatrix() {
		domainConstraintMatrix = (Matrix<R>) eliminateConstraints((Matrix<Rational>) domainConstraintMatrix, true);
	}
	
	private static Matrix<Rational> eliminateConstraints(Matrix<Rational> constraints, boolean eliminateRedundantConstraints) {
		// Find constraints with the same linear part, and keep the least constraining ones
		// XXX: Speedup by sorting and avoiding the copying above
		int[] domainConstraints = new int[constraints.getNumberOfRows()];
		int numberOfRowsRetained = 0;
		int numberOfColumns = constraints.getNumberOfColumns();
		for (int q = 0; q < domainConstraints.length; q++) {
			boolean similarConstraintFound = false;
			int similarConstraint = -1;
			for (int f = 0; f < numberOfRowsRetained; f++) { // Try to find a constraint (in those we retained) with the same linear part
				boolean constraintIsSimilar = true;
				for (int c = 0; c < numberOfColumns - 1; c++) {
					if (!constraints.get(q, c).equals(constraints.get(domainConstraints[f], c))) {
						constraintIsSimilar = false;
						break;
					}
				}
				
				if (constraintIsSimilar) {
					similarConstraint = f;
					break;
				}
			}
				
			if (similarConstraint >= 0) { // Then keep the least constraining constraint
				Rational qOffset = (Rational) constraints.get(q, numberOfColumns - 1); 
				Rational fOffset = (Rational) constraints.get(domainConstraints[similarConstraint], numberOfColumns - 1);
				if (eliminateRedundantConstraints) {
					if (qOffset.isLessThan(fOffset)) domainConstraints[similarConstraint] = q; 
				} else { // Eliminate non-redundant ones (for exfrusion e.g.)
					if (qOffset.isGreaterThan(fOffset))	domainConstraints[similarConstraint] = q; 
				}
				
				// Eliminate equivalent constraints regardless of eliminateRedundantConstraints
				if (qOffset.equals(fOffset)) domainConstraints[similarConstraint] = q;
				
				similarConstraintFound = true;
				break; // At most one constraint with the same linear part should be found
			}
			
			if (!similarConstraintFound) domainConstraints[numberOfRowsRetained++] = q; // Add this constraint
		}
		
		// Now copy the constraints into a new matrix
		Rational[][] elms = new Rational[numberOfRowsRetained][numberOfColumns];
		for (int r = 0; r < numberOfRowsRetained; r++) {
			int originalRow = domainConstraints[r];
			for (int c = 0; c < numberOfColumns; c++) {
				elms[r][c] = constraints.get(originalRow, c);
			}
		}		
		
		return DenseMatrix.valueOf(elms);
	}
	
	public static <R extends RingComparable<R>> ParametrizedIntegerPolyhederDomain<R> exfrusion(ParametrizedIntegerPolyhederDomain<R>[] domains, R zero, R one) {
		assert domains.length > 0; // XXX: We could make this work for empty domains too 
		
		int dim = domains[0].getDimension();
		int parCount = domains[0].getParameterCount();
		
		// Count constraints
		int domConstraintCount = 0;
		int parConstraintCount = 0;
		for (int d = 0; d < domains.length; d++) {
			domConstraintCount += domains[d].getConstraintCount();
			parConstraintCount += domains[d].getParameterConstraintCount();
			
			assert dim == domains[d].getDimension();
			assert parCount == domains[d].getParameterCount();
		}
		
		// Copy constraints
		Matrix<R>[] domainConstraintMatrices = (Matrix<R>[]) new Matrix[domains.length];
		Matrix<R>[] parameterConstraintMatrices = (Matrix<R>[]) new Matrix[domains.length];
		for (int d = 0; d < domains.length; d++) {
			domainConstraintMatrices[d] = domains[d].getDomainConstraintMatrix();
			parameterConstraintMatrices[d] = domains[d].getParameterConstraintMatrix();
		}		
		
		System.err.println("What to do with parameter constraints on polyheder exfrusion?");
		
		Matrix<Rational> intersection = Matrix.<Rational>augmentVertically((Matrix<Rational>[]) domainConstraintMatrices);
		
		return new ParametrizedIntegerPolyhederDomain<R>( 
					(Matrix<R>) eliminateConstraints(intersection, false), 
					Matrix.<R>augmentVertically(parameterConstraintMatrices), zero, one);
	}
	
    private String getDomainConstraintDescriptionAsText(int r, String[] indexVariables, String[] parameterVariables) {
    	StringBuilder sb = new StringBuilder();
    	boolean stringStarted = false;
    	
    	// Source indices
    	int parametrizedDimension = getDimension() + getParameterCount();
    	for (int s = 0; s <= parametrizedDimension; s++) {
    		R factor = domainConstraintMatrix.get(r, s);
    		if (!factor.equals(zero)) {
    			sb.append(((Rational) factor).asString(stringStarted, (s == parametrizedDimension)));
    			if (s < parametrizedDimension) sb.append(indexVariables[s]);
    			
				stringStarted = true;
    		}
    	}
    	
    	// In case nothing is written into the string thusfar
    	if (!stringStarted) sb.append("0");
    	
    	return sb.toString();
    }
    
	public String getDomainConstraintsDescriptionAsText(String[] indexVariables, String[] parameterVariables) {
		StringBuilder sb = new StringBuilder();
		
		for (int r = 0; r < domainConstraintMatrix.getNumberOfRows(); r++) {
			if (r != 0) sb.append("\n");
			sb.append(getDomainConstraintDescriptionAsText(r, indexVariables, parameterVariables));
			sb.append(" >= 0");
		}
		
		return sb.toString();
	}
	
	public Matrix<R> getHomogenizedDomainFarkamMatrix() {
		return domainConstraintMatrix.getHomogenizedMatrix(zero, one);
	}
	
	public Matrix<R> getHomogenizedCombinedDomainFarkamMatrix() {
		return getCombinedConstraintMatrix().getHomogenizedMatrix(zero, one);
	}
	
	public Matrix<R> getDomainConstraintMatrix() {
		return domainConstraintMatrix;
	}
	
	public Matrix<R> getParameterConstraintMatrix() {
		return parameterConstraintMatrix;
	}

	public int getConstraintCount() {
		return getDomainConstraintCount();
	}
	
	class FourierMotzkinSubProblem {
		LinkedList<R[]> zeroList; // list with constraints with zero-coefficient for projecting dimension
		LinkedList<R[]> posList; // list with constraints with positive coefficient for projecting dimension
		LinkedList<R[]> negList; // list with constraints with negative coefficient for projecting dimension
		
		/*public ConditionalSolutionPartitionTree<R> solve() {
			
		}
		
		public ConditionalSolutionPartitionTree<R> splitProblem() {
			
		}*/
	}
	
	public ParametrizedIntegerPolyhederDomain<R> calcFourierMotzkinProject(int projectionDim) {
		assert (projectionDim >= 0) && (projectionDim < getDimension());
		
		R[][] domainConstraints = (R[][]) new RingComparable[getDomainConstraintCount()][domainConstraintMatrix.getNumberOfColumns()];
		for (int r = 0; r < domainConstraintMatrix.getNumberOfRows(); r++) {
			for (int c = 0; c < domainConstraintMatrix.getNumberOfColumns(); c++) {
				domainConstraints[r][c] = domainConstraintMatrix.get(r, c);
			}
		}		
		
		LinkedList<R[]> zeroList = new LinkedList<R[]>(); // list with constraints with zero-coefficient for projecting dimension
		LinkedList<R[]> posList = new LinkedList<R[]>(); // list with constraints with positive coefficient for projecting dimension
		LinkedList<R[]> negList = new LinkedList<R[]>(); // list with constraints with negative coefficient for projecting dimension
		int constraintLength = domainConstraintMatrix.getNumberOfColumns();
		for (int r = 0; r < domainConstraintMatrix.getNumberOfRows(); r++) {
			R pivot = domainConstraintMatrix.get(r, projectionDim);
			
			if (pivot.isEqualTo(zero)) {
				R[] constraint = (R[]) new RingComparable[constraintLength];
				for (int c = 0; c < constraintLength - 1; c++) {
					int d = (c < projectionDim) ? c : c + 1;
					constraint[c] = domainConstraintMatrix.get(r, d);
				}
				zeroList.add(constraint);
			} else {
				R[] constraint = (R[]) new RingComparable[constraintLength];
				for (int c = 0; c < constraintLength; c++) constraint[c] = domainConstraintMatrix.get(r, c);
				if (pivot.isGreaterThan(zero)) {
					posList.add(constraint);
				} else {
					negList.add(constraint);
				}
			}
		}
		
		//int totalResultConstraints // Zero-constraints + projection equality + FM pairs
		//= zeroList.size() + 2 + (posList.size() * negList.size());
		
		// Add remaining constraints to zeroList
		// A - Projection equality --> Dimension removed so these constraints are implicit
/*		R[] eqConstraint1 = (R[]) new Ring_[constraintLength];
		for (int c = 0; c < constraintLength; c++) if (c != projectionDim) eqConstraint1[c] = zero;
		eqConstraint1[projectionDim] = one;
		zeroList.add(eqConstraint1);
		
		R[] eqConstraint2 = (R[]) new Ring_[constraintLength];
		for (int c = 0; c < constraintLength; c++) if (c != projectionDim) eqConstraint2[c] = zero;
		eqConstraint1[projectionDim] = one.opposite();
		zeroList.add(eqConstraint2);*/
		
		for (int p = 0; p < posList.size(); p++) { R[] pConstr = posList.get(p);
			for (int n = 0; n < negList.size(); n++) { R[] nConstr = negList.get(n);
				R[] rConstraint = (R[]) new RingComparable[constraintLength - 1]; // Pair constraint for p and n
				for (int c = 0; c < constraintLength - 1; c++) {
					int d = (c < projectionDim) ? c : c + 1;
					rConstraint[c] = (pConstr[projectionDim].times(nConstr[d])).plus(nConstr[projectionDim].times(pConstr[d]).opposite());
				}
				zeroList.add(rConstraint);
			}		
		}		
		
		R[][] resultingDomainConstraints = (R[][]) new RingComparable[zeroList.size()][];
		for (int r = 0; r < zeroList.size(); r++) {
			resultingDomainConstraints[r] = zeroList.get(r);
		}
		
		return new ParametrizedIntegerPolyhederDomain<R>(DenseMatrix.valueOf(resultingDomainConstraints),
				     parameterConstraintMatrix, zero, one);
	}
	
	public Interval<R> asInterval() {
		if (getDimension() == 1) {
			// Find lowerbound and upperbound
			Fraction<R> lowerBound = null;
			Fraction<R> upperBound = null;
			for (int r = 0; r < domainConstraintMatrix.getNumberOfRows(); r++) {
				R coeff = domainConstraintMatrix.get(r, 0);
				R offs = domainConstraintMatrix.get(r, 1);
				if (!coeff.isEqualTo(zero)) {
				Fraction<R> newFrac = new Fraction<R>(offs.opposite(), coeff, zero, one);
					if (coeff.isGreaterThan(zero)) { // lowerBound
						lowerBound = (lowerBound == null) ? newFrac : lowerBound.minimumWith(newFrac);
					} else { // upperBound
						upperBound = (upperBound == null) ? newFrac : upperBound.maximumWith(newFrac);
					}
				}
			}
			
			return new Interval<R>(lowerBound, upperBound);
		} else return null;
	}
}
