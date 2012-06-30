package be.ugent.elis.sare;

import java.util.Vector;

import org.jscience.mathematics.numbers.LargeInteger;

import be.elis.ugent.math.Vect;
import be.ugent.elis.AffineModuleRelation;
import be.ugent.elis.align.TestCaseExtended;

public class TestAffineModuleRelation_ConjunctionOperation extends TestCaseExtended {
	LargeInteger _li = LargeInteger.valueOf(0);
	AffineModuleRelation.ConjunctionOperation<LargeInteger> conjunction = new AffineModuleRelation.ConjunctionOperation<LargeInteger>(_li);
	
	public Vect<LargeInteger> getVectLargeInteger(int v[]) {
		LargeInteger[] resultArray = new LargeInteger[v.length];
		
		for (int q = 0; q < v.length; q++) resultArray[q] = LargeInteger.valueOf(v[q]);
		
		return new Vect<LargeInteger>(resultArray);
	}

	// First three tests: practically copies of TestAffineModuleRelation_IntersectionOepration
	public void testConjunction_1() {
		Vector<Vect<LargeInteger>> vA = new Vector<Vect<LargeInteger>>();
		Vector<Vect<LargeInteger>> vB = new Vector<Vect<LargeInteger>>();
		Vector<Vect<LargeInteger>> vC = new Vector<Vect<LargeInteger>>();
		
		vA.add(getVectLargeInteger(new int[]{1, 0,  0, 1}));
		vA.add(getVectLargeInteger(new int[]{0, 1,  0, 0}));
		vA.add(getVectLargeInteger(new int[]{0, 0,  1, 0}));
		
		vB.add(getVectLargeInteger(new int[]{1, 0,  0, 1}));
		vB.add(getVectLargeInteger(new int[]{0, 1, -1, 0}));
		
		vC.add(getVectLargeInteger(new int[]{1, 0,  0, 1}));
		vC.add(getVectLargeInteger(new int[]{0, 1, -1, 0}));
		
		AffineModuleRelation<LargeInteger> mA = new AffineModuleRelation<LargeInteger>(_li, vA, 2, 2, 0);
		AffineModuleRelation<LargeInteger> mB = new AffineModuleRelation<LargeInteger>(_li, vB, 2, 2, 0);
		AffineModuleRelation<LargeInteger> mC = new AffineModuleRelation<LargeInteger>(_li, vC, 2, 2, 0);
		
		assert mC.equals(conjunction.getResult(mA, mB));		
	}
	
	public void testConjunction_2() {
		Vector<Vect<LargeInteger>> vA = new Vector<Vect<LargeInteger>>();
		Vector<Vect<LargeInteger>> vB = new Vector<Vect<LargeInteger>>();
		Vector<Vect<LargeInteger>> vC = new Vector<Vect<LargeInteger>>();
		
		vA.add(getVectLargeInteger(new int[]{1, 0,  0,  1}));
		vA.add(getVectLargeInteger(new int[]{0, 1,  0,  0}));
		vA.add(getVectLargeInteger(new int[]{0, 0,  1,  0}));
		
		vB.add(getVectLargeInteger(new int[]{1, 0,  0, -1}));
		vB.add(getVectLargeInteger(new int[]{0, 1, -1,  0}));
		
		vC.add(getVectLargeInteger(new int[]{0, 1, -1,  0}));
		
		AffineModuleRelation<LargeInteger> mA = new AffineModuleRelation<LargeInteger>(_li, vA, 2, 1, 1);
		AffineModuleRelation<LargeInteger> mB = new AffineModuleRelation<LargeInteger>(_li, vB, 2, 1, 1);
		AffineModuleRelation<LargeInteger> mC = new AffineModuleRelation<LargeInteger>(_li, vC, 2, 1, 1);
		
		assert mC.equals(conjunction.getResult(mA, mB));		
	}
	
	public void testConjunction_3() {
		Vector<Vect<LargeInteger>> vA = new Vector<Vect<LargeInteger>>();
		Vector<Vect<LargeInteger>> vB = new Vector<Vect<LargeInteger>>();
		Vector<Vect<LargeInteger>> vC = new Vector<Vect<LargeInteger>>();
		
		vA.add(getVectLargeInteger(new int[]{1, 0,  0,  1}));
		vA.add(getVectLargeInteger(new int[]{0, 1,  1,  0}));
		
		vB.add(getVectLargeInteger(new int[]{1, 0,  0, -1}));
		vB.add(getVectLargeInteger(new int[]{0, 1, -1,  0}));
		
		AffineModuleRelation<LargeInteger> mA = new AffineModuleRelation<LargeInteger>(_li, vA, 2, 0, 2);
		AffineModuleRelation<LargeInteger> mB = new AffineModuleRelation<LargeInteger>(_li, vB, 2, 0, 2);
		AffineModuleRelation<LargeInteger> mC = new AffineModuleRelation<LargeInteger>(_li, vC, 2, 0, 2);
		
		assert mC.equals(conjunction.getResult(mA, mB));		
	}

}
