package be.ugent.elis;

import org.jscience.mathematics.numbers.Rational;
import org.jscience.mathematics.structures.Field_;
import org.jscience.mathematics.vectors.DenseMatrix;
import org.jscience.mathematics.vectors.DenseVector;
import org.jscience.mathematics.vectors.Matrix;
import org.jscience.mathematics.vectors.Vector;

import be.elis.ugent.math.structures.Ring_;
import be.ugent.elis.sare.Dependence;

public class AffineTransformation<F extends Ring_<F>> implements Dependence {
    private int sourceDimension = -1;
    private int destinationDimension = -1;
    
    public Matrix<F> transformationMatrix = null;
    private F zero = null;
    private F one = null;
    
    public AffineTransformation(int iSourceDimension, int iDestinationDimension, F iZero, F iOne) {
    	setDimensions(iSourceDimension, iDestinationDimension);
    	
    	zero = iZero;
    	one = iOne;
    }
    
    public AffineTransformation(Matrix<F> iTransformationMatrix, F iZero, F iOne) {
    	transformationMatrix = iTransformationMatrix;
    	
    	sourceDimension = transformationMatrix.getNumberOfColumns() - 1;
    	destinationDimension = transformationMatrix.getNumberOfRows() - 1;
    	
    	zero = iZero;
    	one = iOne;
    }
    
    public boolean isInternalDataConsistent() {
    	return ((sourceDimension + 1) == transformationMatrix.getNumberOfColumns())
    	       && ((destinationDimension + 1) == transformationMatrix.getNumberOfRows());
    }
    
    public int getSourceDimension() {
    	return sourceDimension;
    }
    
    public void setSourceDimension(int newSourceDimension) {
    	setDimensions(newSourceDimension, destinationDimension);
    }
    
    public int getDestinationDimension() {
    	return destinationDimension;
    }
    
    public void setDestinationDimension(int newDestinationDimension) {
    	setDimensions(sourceDimension, newDestinationDimension);
    }
    
    public void setDimensions(int newSourceDimension, int newDestinationDimension) {
    	if ((sourceDimension != newSourceDimension) || (destinationDimension != newDestinationDimension)) {
    		assert ((sourceDimension == -1) && (destinationDimension == -1))
    		       || ((sourceDimension >= 0) && (destinationDimension >= 0));
    		
        	sourceDimension = newSourceDimension;
        	destinationDimension = newDestinationDimension;
        	
        	if ((destinationDimension >= 0) && (sourceDimension >= 0)) {
//        		transformationMatrix = DenseMatrix.newInstance(destinationDimension + 1, sourceDimension + 1);
        	} else transformationMatrix = null;
    	}
    }
    
    public AffineTransformation<F> rightMultiply(Matrix<F> transformation) {
    	return new AffineTransformation<F>(((DenseMatrix<F>) transformationMatrix).times(transformation, zero), zero, one);
    }
    
    public AffineTransformation<F> leftMultiply(Matrix<F> transformation) {
    	return new AffineTransformation<F>(((DenseMatrix<F>) transformation).times(transformationMatrix, zero), zero, one);
    }
    
    public static <F extends Field_<F>> AffineTransformation<F> newTranslation(F[] translationVector, F zero, F one) {
    	int dimension = translationVector.length;
    	AffineTransformation<F> at = new AffineTransformation<F>(dimension, dimension, zero, one);
    	
    	// Identity matrix
    	F[][] elements = (F[][]) new Field_[dimension + 1][dimension + 1]; 
    	for (int d = (dimension - 1); d >= 0; d--) {
    		for (int s = (dimension - 1); s >= 0; s--) {
    			elements[d][s] = (d == s) ? one  : zero;
        	}
    	}
    	
    	// Translation vector
    	for (int q = (dimension - 1); q >= 0; q--) {
    		elements[q][dimension] = translationVector[q];
    	}
    	
    	// Homogeneous Coordinate row
    	for (int q = (dimension - 1); q >= 0; q--) {
    		elements[dimension][q] = zero;
    	}
    	elements[dimension][dimension] = one;
    	
    	at.transformationMatrix = DenseMatrix.valueOf(elements);
    	assert at.transformationMatrix.isInternalDataConsistent();
    	
    	return at;
    }

    public AffineTransformation<F> after(AffineTransformation<F> priorTransformation) {
    	Matrix<F> resultTrafoM = ((DenseMatrix<F>) transformationMatrix).times(priorTransformation.transformationMatrix, zero);
    	
    	return new AffineTransformation<F>(resultTrafoM, zero, one);
    }
    
    public AffineTransformation<F> subTransformation(int[] retainedDimensions) {
    	F[][] elms = (F[][]) new Field_[retainedDimensions.length][sourceDimension + 1];

    	for (int r = 0; r < retainedDimensions.length; r++) {
    		for (int c = 0; c <= sourceDimension; c++) {
    			elms[r][c] = transformationMatrix.get(retainedDimensions[r], c);
    		}
    	}
    	
    	return new AffineTransformation<F>(DenseMatrix.valueOf(elms), zero, one);
    }
    
    public AffineTransformation<F> subTransformation(int rangeOffset, int rangeCount) {
    	int[] retainedDimensions = new int[rangeCount];
    	for (int q = 0; q < rangeCount; q++) retainedDimensions[q] = rangeOffset + q;
    	
    	return subTransformation(retainedDimensions);
    }
    
    public static <F extends Field_<F>> AffineTransformation<F> newSimpleProjection(int sourceDimension, int[] projectedDimensions, F zero, F one) {
    	int destinationDimension = projectedDimensions.length;
    	AffineTransformation<F> at = new AffineTransformation<F>(sourceDimension, destinationDimension, zero, one);
    	
    	// Projection matrix
    	F[][] elements = (F[][]) new Field_[destinationDimension + 1][sourceDimension + 1]; 
    	for (int d = 0; d < destinationDimension; d++) {
    		for (int s = 0; s < sourceDimension; s++) {
        		elements[d][s] = (projectedDimensions[d] == s) ? one : zero;
        	}
    	}
    	
    	// Translation vector
    	for (int d = 0; d < destinationDimension; d++) {
    		elements[d][sourceDimension] = zero;
    	}    	
    	
    	// Homogeneous Coordinate row
    	for (int q = (sourceDimension - 1); q >= 0; q--) {
    		elements[destinationDimension][q] = zero;
    	}
    	elements[destinationDimension][sourceDimension] = one;
    	
    	at.transformationMatrix = DenseMatrix.valueOf(elements);
    	
    	assert at.transformationMatrix.isInternalDataConsistent();
    	return at;
    }
    
    public static <F extends Field_<F>> AffineTransformation<F> newConstantDestinationDependence(int sourceDimension, F[] destination, F zero, F one) {
    	int destinationDimension = destination.length;
    	AffineTransformation<F> at = new AffineTransformation<F>(sourceDimension, destinationDimension, zero, one);
    	
    	// Projection matrix
    	F[][] elements = (F[][]) new Field_[destinationDimension + 1][sourceDimension + 1]; 
    	for (int d = 0; d < destinationDimension; d++) {
    		for (int s = 0; s < sourceDimension; s++) {
        		elements[d][s] = zero;
        	}
    	}
    	
    	// Translation vector
    	for (int d = 0; d < destinationDimension; d++) {
    		elements[d][sourceDimension] = destination[d];
    	}    	

    	// Homogeneous Coordinate row
    	for (int q = (sourceDimension - 1); q >= 0; q--) {
    		elements[destinationDimension][q] = zero;
    	}
    	elements[destinationDimension][sourceDimension] = one;
    	
    	at.transformationMatrix = DenseMatrix.valueOf(elements);
    	
    	assert at.transformationMatrix.isInternalDataConsistent();    	
    	return at;
    }
    
    public F[] transform(F[] source) {
    	Vector<F> sourceV = DenseVector.valueOf(source);
    	Vector<F> destinationV = transformationMatrix.times(sourceV);

    	F[] result = (F[]) new Object[destinationV.getDimension()];
    	
    	for (int q = 0; q < result.length; q++) result[q] = destinationV.get(q);
    	
    	return result;
    }
    
    public Vector<F> transform(Vector<F> source) {
    	return transformationMatrix.times(source);
    }
    
    public String getTransformedIndexesAsTextForDestinationDimension(int d, String[] indexVariables) {
    	StringBuilder sb = new StringBuilder();
    	boolean stringStarted = false;
    	
    	// Source + Translation indices
    	for (int s = 0; s <= sourceDimension; s++) {
    		F factor = transformationMatrix.get(d, s);
    		if (!factor.equals(zero)) {
    			sb.append(((Rational) factor).asString(stringStarted, (s == sourceDimension)));
        		if (s < sourceDimension) sb.append(indexVariables[s]);
        		stringStarted = true;
    		}
    	}
    	
    	// In case nothing is written into the string thusfar
    	if (!stringStarted) sb.append("0");
    	
    	return sb.toString();
    }
    
	public String getTransformedIndexesAsText(String[] indexVariables, boolean algebraic) {
		StringBuilder sb = new StringBuilder();
		
		for (int d = 0; d < destinationDimension; d++) {
			if (d != 0) sb.append(", ");
			sb.append(getTransformedIndexesAsTextForDestinationDimension(d, indexVariables));
		}
		
		return sb.toString();
	}
	
	public String convertToString(String[] unknownNames, String[] parameterNames) {
		StringBuilder sb = new StringBuilder();
		
		boolean firstTerm = true;
		for (int d = 0; d <= destinationDimension; d++) {
			boolean offset = (d == destinationDimension);
			String currentDestinationTransform = getTransformedIndexesAsTextForDestinationDimension(d, parameterNames);
			if (!currentDestinationTransform.equals("0") || (firstTerm && offset)) {
				if (!firstTerm) sb.append(" + ");
				
				if (!currentDestinationTransform.equals("1") || offset) {
					sb.append((offset && firstTerm) ? currentDestinationTransform : ("(" + currentDestinationTransform + ")"));
				}
				if (!offset) sb.append(unknownNames[d]);
				firstTerm = false;
			}
		}
		
		return sb.toString();
	}
	
	/**
	 * Concatenates transformations:
	 *   The source vector of the resulting transformation is the concatenation of the source vectors of the 
	 *   input transformations.  Same thing for the destination vectors. 
	 */
	public static <F extends Field_<F>> AffineTransformation<F> concatenateTransformations(AffineTransformation<F>[] transformations, F zero, F one) {
		// determine dimensions of result
		int sourceDimension = 0;
		int destinationDimension = 0;
		for (int m = 0; m < transformations.length; m++) {
			destinationDimension += transformations[m].getDestinationDimension();
			sourceDimension += transformations[m].getSourceDimension();
		}
		
		AffineTransformation<F> result = new AffineTransformation<F>(destinationDimension, sourceDimension, zero, one);

		// Copy transformations into result
		int columnOffset = 0;
		int rowOffset = 0;
		F[][] resultElements = (F[][]) new Field_[destinationDimension + 1][sourceDimension + 1]; 
		for (int m = 0; m < transformations.length; m++) {
			Matrix<F> matrix = transformations[m].transformationMatrix;
			assert matrix.isInternalDataConsistent();
			
			// Linear portion
			for (int r = 0; r < matrix.getNumberOfRows() - 1; r++) {
				int rowIx = rowOffset + r;
				// Zero part left of linear portion
				for (int c = 0; c < columnOffset; c++) resultElements[rowIx][c] = zero;
				
				// Linear portion
				for (int c = 0; c < matrix.getNumberOfColumns() - 1; c++) {
					resultElements[rowIx][columnOffset + c] = matrix.get(r, c);
				}
				
				// Zero part right of linear portion
				for (int c = columnOffset + (matrix.getNumberOfColumns() - 1); c < sourceDimension; c++) {
					resultElements[rowIx][c] = zero;
				}
				
				// Translation portion
				resultElements[rowIx][sourceDimension] = matrix.get(r, matrix.getNumberOfColumns() - 1);
			}
			
			rowOffset += matrix.getNumberOfRows() - 1;
			columnOffset += matrix.getNumberOfColumns() - 1;
		}
		
		for (int c = 0; c < sourceDimension; c++) resultElements[destinationDimension][c] = zero;
		resultElements[destinationDimension][sourceDimension] = one;
		
		result.transformationMatrix = DenseMatrix.valueOf(resultElements);
		
		assert result.transformationMatrix.isInternalDataConsistent();		
		return result;
	}

	public static <F extends Field_<F>> AffineTransformation<F> newIdentityTransformation(int dimension, F zero, F one) {
		F[] zeroV = (F[]) new Field_[dimension];
		
		for (int q = 0; q < zeroV.length; q++) zeroV[q] = zero;
		
		return newTranslation(zeroV, zero, one);
	}

	public boolean isZero() {
		boolean nonZeroValueFound = false;
		for (int r = 0; r < transformationMatrix.getNumberOfRows(); r++) {
			for (int c = 0; c < transformationMatrix.getNumberOfColumns(); c++) {
				if (!((Rational) transformationMatrix.get(r, c)).equals(Rational.ZERO)) {
					nonZeroValueFound = true;
				}
			}
		}
			
		return !nonZeroValueFound;
	}
}

