package be.ugent.elis;

import be.ugent.elis.sare.Dependence;

/**
 * @deprecated
 */
public class AffineTransformation_Obsolete implements Dependence {
    private int sourceDimension = -1;
    private int destinationDimension = -1;
    
    public IntegralMatrix_Obsolete transformationMatrix = null;
    
    public AffineTransformation_Obsolete(int iSourceDimension, int iDestinationDimension) {
    	setDimensions(iSourceDimension, iDestinationDimension);
    }
    
    public AffineTransformation_Obsolete(IntegralMatrix_Obsolete iTransformationMatrix) {
    	transformationMatrix = iTransformationMatrix;
    	
    	sourceDimension = transformationMatrix.getColumnCount() - 1;
    	destinationDimension = transformationMatrix.getRowCount() - 1;
    }
    
    public boolean isInternalDataConsistent() {
    	return transformationMatrix.isInternalDataConsistent()
    	       && ((sourceDimension + 1) == transformationMatrix.getColumnCount())
    	       && (destinationDimension == transformationMatrix.getRowCount());
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
        		transformationMatrix = new IntegralMatrix_Obsolete(destinationDimension + 1, sourceDimension + 1);
        	} else transformationMatrix = null;
    	}
    }
    
    public static AffineTransformation_Obsolete newTranslation(int[] translationVector) {
    	int dimension = translationVector.length;
    	AffineTransformation_Obsolete at = new AffineTransformation_Obsolete(dimension, dimension);
    	
    	// Identity matrix
    	for (int d = (dimension - 1); d >= 0; d--) {
    		for (int s = (dimension - 1); s >= 0; s--) {
        		at.transformationMatrix.elements[d][s] = ((d == s) ? 1 : 0);
        	}
    	}
    	
    	// Translation vector
    	for (int q = (dimension - 1); q >= 0; q--) {
    		at.transformationMatrix.elements[q][dimension] = translationVector[q];
    	}
    	
    	// Homogeneous Coordinate row
    	for (int q = (dimension - 1); q >= 0; q--) {
    		at.transformationMatrix.elements[dimension][q] = (q == dimension) ? 1 : 0;
    	}
    	
    	return at;
    }
    
    public static AffineTransformation_Obsolete newSimpleProjection(int sourceDimension, int[] projectedDimensions) {
    	int destinationDimension = projectedDimensions.length;
    	AffineTransformation_Obsolete at = new AffineTransformation_Obsolete(sourceDimension, destinationDimension);
    	
    	// Projection matrix
    	for (int d = 0; d < destinationDimension; d++) {
    		for (int s = 0; s < sourceDimension; s++) {
        		at.transformationMatrix.elements[d][s] = ((projectedDimensions[d] == s) ? 1 : 0);
        	}
    	}
    	
    	// Translation vector
    	for (int d = 0; d < destinationDimension; d++) {
    		at.transformationMatrix.elements[d][sourceDimension] = 0;
    	}    	
    	
    	return at;
    }
    
    public static AffineTransformation_Obsolete newConstantDestinationDependence(int sourceDimension, int[] destination) {
    	int destinationDimension = destination.length;
    	AffineTransformation_Obsolete at = new AffineTransformation_Obsolete(sourceDimension, destinationDimension);
    	
    	// Projection matrix
    	for (int d = 0; d < destinationDimension; d++) {
    		for (int s = 0; s < sourceDimension; s++) {
        		at.transformationMatrix.elements[d][s] = 0;
        	}
    	}
    	
    	// Translation vector
    	for (int d = 0; d < destinationDimension; d++) {
    		at.transformationMatrix.elements[d][sourceDimension] = destination[d];
    	}    	
    	
    	return at;
    }
    
    public int[] transform(int[] source) {
    	return transformationMatrix.multiplyWithVector(source);
    }
    
    private String getTransformedIndexesAsTextForDestinationDimension(int d, String[] indexVariables, String[] parameterVariables) {
    	StringBuilder sb = new StringBuilder();
    	boolean stringStarted = false;
    	
    	// Source indices
    	for (int s = 0; s < sourceDimension; s++) {
    		int factor = transformationMatrix.elements[d][s]; 
    		if (factor != 0) {
    			if (!stringStarted) {
    				if (factor < 0) sb.append("-");
    			} else sb.append((factor > 0) ? " + " : " - ");
    			
    			factor = Math.abs(factor);
    			if (factor != 1) {
        			sb.append(String.format("%d%s", factor, indexVariables[s]));
    			} else sb.append(String.format("%s", indexVariables[s]));
    			
				stringStarted = true;
    		}
    	}
    	
    	// Constant translation
		int offset = transformationMatrix.elements[d][sourceDimension]; 
    	if (offset != 0) {
			if (!stringStarted) {
				if (offset < 0) sb.append("-");
			} else sb.append((offset > 0) ? " + " : " - ");
			
    		sb.append(String.format("%d", Math.abs(offset)));
    		
			stringStarted = true;
    	}
    	
    	// In case nothing is written into the string thusfar
    	if (!stringStarted) sb.append("0");
    	
    	return sb.toString();
    }
    
	public String getTransformedIndexesAsText(String[] indexVariables, String[] parameterVariables) {
		StringBuilder sb = new StringBuilder();
		
		for (int d = 0; d < destinationDimension; d++) {
			if (d != 0) sb.append(", ");
			sb.append(getTransformedIndexesAsTextForDestinationDimension(d, indexVariables, parameterVariables));
		}
		
		return sb.toString();
	}
	
	/**
	 * Concatenates transformations:
	 *   The source vector of the resulting transformation is the concatenation of the source vectors of the 
	 *   input transformations.  Same thing for the destination vectors. 
	 */
	public static AffineTransformation_Obsolete concatenateTransformations(AffineTransformation_Obsolete[] transformations) {
		// determine dimensions of result
		int sourceDimension = 0;
		int destinationDimension = 0;
		for (int m = 0; m < transformations.length; m++) {
			destinationDimension += transformations[m].getDestinationDimension();
			sourceDimension += transformations[m].getSourceDimension();
		}
		
		AffineTransformation_Obsolete result = new AffineTransformation_Obsolete(destinationDimension, sourceDimension);
		result.transformationMatrix.clearElements();

		// Copy transformations into result
		int columnOffset = 0;
		int rowOffset = 0;
		int[][] resultElements = result.transformationMatrix.elements;
		for (int m = 0; m < transformations.length; m++) {
			IntegralMatrix_Obsolete matrix = transformations[m].transformationMatrix;
			
			// Linear portion
			for (int r = 0; r < matrix.getRowCount() - 1; r++) {
				for (int c = 0; c < matrix.getColumnCount() - 1; c++) {
					resultElements[rowOffset + r][columnOffset + c] = matrix.elements[r][c];
				}
			}
			
			// Translation portion
			for (int r = 0; r < matrix.getRowCount() - 1; r++) {
				resultElements[rowOffset + r][sourceDimension] = matrix.elements[r][matrix.getColumnCount() - 1];
			}
			
			rowOffset += matrix.getRowCount() - 1;
			columnOffset += matrix.getColumnCount() - 1;
		}
		resultElements[destinationDimension][sourceDimension] = 1;
		
		return result;
	}

	public static AffineTransformation_Obsolete newIdentityTransformation(int dimension) {
		AffineTransformation_Obsolete result = new AffineTransformation_Obsolete(dimension, dimension);
		
		result.transformationMatrix = IntegralMatrix_Obsolete.identityMatrix(dimension + 1);
		
		return result;
	}
}

