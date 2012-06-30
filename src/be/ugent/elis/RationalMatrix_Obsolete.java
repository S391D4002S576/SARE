package be.ugent.elis;

import java.util.Arrays;


/**
 * @deprecated
 */
public class RationalMatrix_Obsolete {
	public Rational_Obsolete[][] elements = null; // [row][col]
	int rowCount = -1;
	int columnCount = -1;
	
	public boolean isInternalDataConsistent() {
		if ((rowCount == -1) && (columnCount == -1) && (elements == null)) return true;
		if ((rowCount < 0) || (columnCount < 0) || (elements == null)) return false;
		
		if (elements.length != rowCount) return false;
		
		for (int q = 0; q < rowCount; q++) {
			if ((elements[q] == null) || (elements[q].length != columnCount)) return false;
			for (int c = 0; c < columnCount; c++) if (elements[q][c] == null) return false;
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
		
		elements = new Rational_Obsolete[rowCount][columnCount];
	}
	
	public RationalMatrix_Obsolete(int iRowCount, int iColumnCount) {
		setDimensions(iRowCount, iColumnCount);
	}	
	
	public RationalMatrix_Obsolete(IntegralMatrix_Obsolete integralMatrix) {
		setDimensions(integralMatrix.getRowCount(), integralMatrix.getColumnCount());
		
		for (int r = 0; r < rowCount; r++) {
			for (int c = 0; c < columnCount; c++) {
				elements[r][c] = new Rational_Obsolete(integralMatrix.elements[r][c]);
			}
		}
	}	
	
	public void clearElements() {
		for (int r = 0; r < rowCount; r++) {
			for (int c = 0; c < columnCount; c++) {
				elements[r][c] = new Rational_Obsolete(0);
			}
		}
	}
	
	public static RationalMatrix_Obsolete multiply(RationalMatrix_Obsolete l, RationalMatrix_Obsolete r) {
		assert l.isInternalDataConsistent() && r.isInternalDataConsistent()
		       && (l.getColumnCount() == r.getRowCount());
		
		RationalMatrix_Obsolete result = new RationalMatrix_Obsolete(l.getRowCount(), r.getColumnCount());
		
		int sumCount = l.getColumnCount();
		for (int a = 0; a < result.getRowCount(); a++) {
			for (int b = 0; b < result.getColumnCount(); b++) {
				result.elements[a][b] = new Rational_Obsolete(0);
				for (int q = 0; q < sumCount; q++) {
					result.elements[a][b].add(Rational_Obsolete.multiply(l.elements[a][q], r.elements[q][b]));
				}
			}
		}
		
		return result;
	}

	public RationalMatrix_Obsolete transpose() {
		RationalMatrix_Obsolete result = new RationalMatrix_Obsolete(getColumnCount(), getRowCount());
		
		for (int r = 0; r < result.rowCount; r++) {
			for (int c = 0; c < result.columnCount; c++) {
				result.elements[r][c] = elements[c][r].clone();
			}
		}
		
		return result;
	}
	
	public Rational_Obsolete[] multiplyWithVector(int[] v) {
		assert ((v != null) && (v.length == getColumnCount()));
		
		Rational_Obsolete[] result = new Rational_Obsolete[getRowCount()];
		
		for (int r = 0; r < getRowCount(); r++) {
			result[r] = new Rational_Obsolete(0);
			for (int c = 0; c < getColumnCount(); c++) {
				result[r].add(Rational_Obsolete.multiply(v[c], elements[r][c]));
			}
		}
			
		return result;
	}

	public static RationalMatrix_Obsolete identityMatrix(int dimension) {
		RationalMatrix_Obsolete result = new RationalMatrix_Obsolete(dimension, dimension);
		
		result.clearElements();
		
		for (int d = 0; d < dimension; d++) result.elements[d][d] = new Rational_Obsolete(1);
		
		return result;
	}

	public static RationalMatrix_Obsolete diagonalMatrix(RationalMatrix_Obsolete[] matrices) {
		// determine dimension of result
		int columnCount = 0;
		int rowCount = 0;
		for (int m = 0; m < matrices.length; m++) {
			rowCount += matrices[m].getRowCount();
			columnCount += matrices[m].getColumnCount();
		}
		
		RationalMatrix_Obsolete result = new RationalMatrix_Obsolete(rowCount, columnCount);
		result.clearElements();

		// Copy matrices into result
		int columnOffset = 0;
		int rowOffset = 0;
		for (int m = 0; m < matrices.length; m++) {
			RationalMatrix_Obsolete matrix = matrices[m];
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

	public static RationalMatrix_Obsolete augmentVertically(RationalMatrix_Obsolete[] matrices) {
		// determine dimension of result
		int rowCount = 0;
		int columnCount = -1;
		for (int m = 0; m < matrices.length; m++) {
			rowCount += matrices[m].getRowCount();
			if (m == 0) columnCount = matrices[m].getColumnCount();
			assert (columnCount == matrices[m].getColumnCount());
		}
		
		RationalMatrix_Obsolete result = new RationalMatrix_Obsolete(rowCount, columnCount);

		// Copy matrices into result
		int rowOffset = 0;
		for (int m = 0; m < matrices.length; m++) {
			RationalMatrix_Obsolete matrix = matrices[m];
			for (int r = 0; r < matrix.getRowCount(); r++) {
				for (int c = 0; c < matrix.getColumnCount(); c++) {
					result.elements[rowOffset + r][c] = matrix.elements[r][c];
				}
			}
			
			rowOffset += matrix.getRowCount();
		}
		
		return result;
	}
	
	public String convertToString(int elementLength) {
		StringBuilder sb = new StringBuilder();
		
		for (int r = 0; r < rowCount; r++) {
			sb.append("[");
			for (int c = 0; c < columnCount; c++) {
				sb.append(elements[r][c].convertToString(elementLength, true));
			}
			sb.append("]\n");
		}
		
		return sb.toString();
	}
}
