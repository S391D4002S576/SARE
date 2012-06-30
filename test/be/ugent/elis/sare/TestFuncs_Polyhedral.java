package be.ugent.elis.sare;

import java.util.Vector;

import org.jscience.mathematics.numbers.LargeInteger;
import org.jscience.mathematics.numbers.Rational;
import org.jscience.mathematics.vectors.DenseMatrix;
import org.jscience.mathematics.vectors.Matrix;

import be.elis.ugent.graphLibrary.Graph;
import be.elis.ugent.math.Vect;
import be.ugent.elis.AffineModuleRelation;
import be.ugent.elis.AffineTransformation;
import be.ugent.elis.align.TestCaseExtended;
import be.ugent.elis.sare.domains.ParametrizedIntegerPolyhederDomain;

public class TestFuncs_Polyhedral extends TestCaseExtended {
	static LargeInteger _li = LargeInteger.ZERO;
	
	public static Matrix<Rational> convertIntTableToMatrixRational(int[][] i) {
		Rational[][] elms = new Rational[i.length][];
		int columnCount = -1;
		
		for (int r = 0; r < i.length; r++) {
			if (r == 0) columnCount = i[r].length;
			assert (columnCount == i[r].length);
			
			elms[r] = new Rational[columnCount];
			for (int c = 0; c < columnCount; c++) {
				elms[r][c] = Rational.valueOf(i[r][c], 1);
			}
		}
		
		return DenseMatrix.valueOf(elms);
	}
	
	public static Matrix<LargeInteger> convertIntTableToMatrixLargeInteger(int[][] i) {
		LargeInteger[][] elms = new LargeInteger[i.length][];
		int columnCount = -1;
		
		for (int r = 0; r < i.length; r++) {
			if (r == 0) columnCount = i[r].length;
			assert (columnCount == i[r].length);
			
			elms[r] = new LargeInteger[columnCount];
			for (int c = 0; c < columnCount; c++) {
				elms[r][c] = LargeInteger.valueOf(i[r][c]);
			}
		}
		
		return DenseMatrix.valueOf(elms);
	}
	
	
	public static ParametrizedIntegerPolyhederDomain<Rational> convertIntTableToPIPDRational(int[][] domain, int[][] context) {
		return new ParametrizedIntegerPolyhederDomain<Rational>( 
				convertIntTableToMatrixRational(domain), convertIntTableToMatrixRational(context), 
                Rational.ZERO, Rational.ONE);
	}
	
	public static ParametrizedIntegerPolyhederDomain<LargeInteger> convertIntTableToPIPDLargeInteger(int[][] domain, int[][] context) {
		return new ParametrizedIntegerPolyhederDomain<LargeInteger>( 
				convertIntTableToMatrixLargeInteger(domain), convertIntTableToMatrixLargeInteger(context), 
				LargeInteger.ZERO, LargeInteger.ONE);
	}
	
	
	public static AffineTransformation<Rational> convertIntTableToAffineTrafoRational(int[][] i) {
		return new AffineTransformation<Rational>(convertIntTableToMatrixRational(i), Rational.ZERO, Rational.ONE);
	}

	public static AffineTransformation<LargeInteger> convertIntTableToAffineTrafoLargeInteger(int[][] i) {
		return new AffineTransformation<LargeInteger>(convertIntTableToMatrixLargeInteger(i), LargeInteger.ZERO, LargeInteger.ONE);
	}
	
	
	public static AffineModuleRelation<LargeInteger> getAffineModuleRelationFromGeneratingMatrix(int[][] i,
					int pDimension, int qDimension, int constantDimension) {
		Vector<Vect<LargeInteger>> v = new Vector<Vect<LargeInteger>>();
		
		for (int q = 0; q < i.length; q++) v.add(getVectLargeInteger(i[q]));

		return new AffineModuleRelation<LargeInteger>(_li, v, pDimension, qDimension, constantDimension);
	}

	public static Vect<LargeInteger> getVectLargeInteger(int v[]) {
		LargeInteger[] resultArray = new LargeInteger[v.length];
		
		for (int q = 0; q < v.length; q++) resultArray[q] = LargeInteger.valueOf(v[q]);
		
		return new Vect<LargeInteger>(resultArray);
	}
	
	
}
