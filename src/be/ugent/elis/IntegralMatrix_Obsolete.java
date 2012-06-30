package be.ugent.elis;

import java.util.Arrays;


/**
 * @deprecated
 */
public class IntegralMatrix_Obsolete {
	public int[][] elements = null; // [row][col]
	int rowCount = -1;
	int columnCount = -1;
	
	public boolean isInternalDataConsistent() {
		if ((rowCount == -1) && (columnCount == -1) && (elements == null)) return true;
		if ((rowCount < 0) || (columnCount < 0) || (elements == null)) return false;
		
		if (elements.length != rowCount) return false;
		
		for (int q = 0; q < rowCount; q++) {
			if ((elements[q] == null) || (elements[q].length != columnCount)) return false;
		}
		
		return true;
	}
	
	public int getRowCount() { return rowCount; }
	public int getColumnCount() { return columnCount; }
	
	public void setDimensions(int newRowCount, int newColumnCount) {
		assert ((newRowCount == -1) && (newColumnCount == -1)) 
		       || ((newRowCount >= 0) && (newColumnCount >= 0));
		
		rowCount = newRowCount;
		columnCount = newColumnCount;
		
		elements = new int[rowCount][columnCount];
	}
	
	public IntegralMatrix_Obsolete(int iRowCount, int iColumnCount) {
		setDimensions(iRowCount, iColumnCount);
	}	
	
	public void clearElements() {
		for (int r = 0; r < rowCount; r++) Arrays.fill(elements[r], 0);
	}
	
	public static IntegralMatrix_Obsolete multiply(IntegralMatrix_Obsolete l, IntegralMatrix_Obsolete r) {
		assert l.isInternalDataConsistent() && r.isInternalDataConsistent()
		       && (l.getColumnCount() == r.getRowCount());
		
		IntegralMatrix_Obsolete result = new IntegralMatrix_Obsolete(l.getRowCount(), r.getColumnCount());
		
		int sumCount = l.getColumnCount();
		for (int a = 0; a < result.getRowCount(); a++) {
			for (int b = 0; b < result.getColumnCount(); b++) {
				result.elements[a][b] = 0;
				for (int q = 0; q < sumCount; q++) {
					result.elements[a][b] += l.elements[a][q] * r.elements[q][b];
				}
			}
		}
		
		return result;
	}
	
	public int[] multiplyWithVector(int[] v) {
		assert ((v != null) && (v.length == getColumnCount()));
		
		int[] result = new int[getRowCount()];
		
		for (int r = 0; r < getRowCount(); r++) {
			result[r] = 0;
			for (int c = 0; c < getColumnCount(); c++) result[r] += v[c] * elements[r][c];
		}
			
		return result;
	}

	public static IntegralMatrix_Obsolete identityMatrix(int dimension) {
		IntegralMatrix_Obsolete result = new IntegralMatrix_Obsolete(dimension, dimension);
		
		result.clearElements();
		
		for (int d = 0; d < dimension; d++) result.elements[d][d] = 1;
		
		return result;
	}

	public static IntegralMatrix_Obsolete diagonalMatrix(IntegralMatrix_Obsolete[] matrices) {
		// determine dimension of result
		int columnCount = 0;
		int rowCount = 0;
		for (int m = 0; m < matrices.length; m++) {
			rowCount += matrices[m].getRowCount();
			columnCount += matrices[m].getColumnCount();
		}
		
		IntegralMatrix_Obsolete result = new IntegralMatrix_Obsolete(rowCount, columnCount);
		result.clearElements();

		// Copy matrices into result
		int columnOffset = 0;
		int rowOffset = 0;
		for (int m = 0; m < matrices.length; m++) {
			IntegralMatrix_Obsolete matrix = matrices[m];
			for (int r = 0; r < matrix.getRowCount(); r++) {
				for (int c = 0; c < matrix.getColumnCount(); c++) {
					result.elements[rowOffset + r][columnOffset + c] = matrix.elements[r][c];
				}
			}
			
			rowOffset += matrix.getRowCount();
			columnOffset += matrix.getColumnCount();
		}
		
		return result;
	}

	public static IntegralMatrix_Obsolete augmentVertically(IntegralMatrix_Obsolete[] matrices) {
		// determine dimension of result
		int rowCount = 0;
		int columnCount = -1;
		for (int m = 0; m < matrices.length; m++) {
			rowCount += matrices[m].getRowCount();
			if (m == 0) columnCount = matrices[m].getColumnCount();
			assert (columnCount == matrices[m].getColumnCount());
		}
		
		IntegralMatrix_Obsolete result = new IntegralMatrix_Obsolete(rowCount, columnCount);

		// Copy matrices into result
		int rowOffset = 0;
		for (int m = 0; m < matrices.length; m++) {
			IntegralMatrix_Obsolete matrix = matrices[m];
			for (int r = 0; r < matrix.getRowCount(); r++) {
				for (int c = 0; c < matrix.getColumnCount(); c++) {
					result.elements[rowOffset + r][c] = matrix.elements[r][c];
				}
			}
			
			rowOffset += matrix.getRowCount();
		}
		
		return result;
	}
}
