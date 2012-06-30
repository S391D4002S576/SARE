package be.ugent.elis.sare;

import java.util.Vector;

import junit.framework.Test;

import org.jscience.mathematics.numbers.LargeInteger;

import be.elis.ugent.math.Vect;
import be.ugent.elis.AffineModuleRelation;
import be.ugent.elis.align.TestCaseExtended;
import be.ugent.elis.graphLibrary.GraphTesting;

public class TestAffineModuleRelation_AfterOperation extends GraphTesting {
	LargeInteger _li = LargeInteger.valueOf(0);
	AffineModuleRelation.AfterOperation<LargeInteger> after = new AffineModuleRelation.AfterOperation<LargeInteger>(_li);

	/*
	 * Without constants
	 */
	public void testAfter_1() {
		Vector<Vect<LargeInteger>> vA = new Vector<Vect<LargeInteger>>();
		Vector<Vect<LargeInteger>> vB = new Vector<Vect<LargeInteger>>();
		Vector<Vect<LargeInteger>> vC = new Vector<Vect<LargeInteger>>();
		
		vA.add(TestFuncs_Polyhedral.getVectLargeInteger(new int[]{1, 0,  0, 1}));
		vA.add(TestFuncs_Polyhedral.getVectLargeInteger(new int[]{0, 1,  0, 0}));
		vA.add(TestFuncs_Polyhedral.getVectLargeInteger(new int[]{0, 0,  1, 0}));
		
		vB.add(TestFuncs_Polyhedral.getVectLargeInteger(new int[]{1, 0,  0, 1}));
		
		vC.add(TestFuncs_Polyhedral.getVectLargeInteger(new int[]{1, 0,  0,  0,  0, 1}));
		vC.add(TestFuncs_Polyhedral.getVectLargeInteger(new int[]{0, 1,  0,  0,  0, 0}));
		vC.add(TestFuncs_Polyhedral.getVectLargeInteger(new int[]{0, 0,  1,  0,  0, 0}));
		
		AffineModuleRelation<LargeInteger> mA = new AffineModuleRelation<LargeInteger>(_li, vA, 3, 1, 0);
		AffineModuleRelation<LargeInteger> mB = new AffineModuleRelation<LargeInteger>(_li, vB, 1, 3, 0);
		AffineModuleRelation<LargeInteger> mC = new AffineModuleRelation<LargeInteger>(_li, vC, 3, 3, 0);
		
		assert mC.equals(after.getResult(mA, mB));		
	}
	
	/*
	 * Without constants
	 */
	public void testAfter_2() {
		Vector<Vect<LargeInteger>> vA = new Vector<Vect<LargeInteger>>();
		Vector<Vect<LargeInteger>> vB = new Vector<Vect<LargeInteger>>();
		Vector<Vect<LargeInteger>> vC = new Vector<Vect<LargeInteger>>();
		
		vA.add(TestFuncs_Polyhedral.getVectLargeInteger(new int[]{1, 0,  0,  1}));
		vA.add(TestFuncs_Polyhedral.getVectLargeInteger(new int[]{0, 1,  0,  0}));
		vA.add(TestFuncs_Polyhedral.getVectLargeInteger(new int[]{0, 0,  1,  0}));
		
		vB.add(TestFuncs_Polyhedral.getVectLargeInteger(new int[]{1, 0,  0, -1}));
		vB.add(TestFuncs_Polyhedral.getVectLargeInteger(new int[]{0, 1, -1,  0}));
		
		vC.add(TestFuncs_Polyhedral.getVectLargeInteger(new int[]{0,  0,   0, -1}));
		vC.add(TestFuncs_Polyhedral.getVectLargeInteger(new int[]{1,  0,  -1,  0}));
		vC.add(TestFuncs_Polyhedral.getVectLargeInteger(new int[]{0,  1,   0,  0}));
		
		AffineModuleRelation<LargeInteger> mA = new AffineModuleRelation<LargeInteger>(_li, vA, 2, 2, 0);
		AffineModuleRelation<LargeInteger> mB = new AffineModuleRelation<LargeInteger>(_li, vB, 2, 2, 0);
		AffineModuleRelation<LargeInteger> mC = new AffineModuleRelation<LargeInteger>(_li, vC, 2, 2, 0);
		
		assert mC.equals(after.getResult(mA, mB));		
	}
	
	/*
	 * Without constants
	 */
	public void testAfter_3() {
		Vector<Vect<LargeInteger>> vA = new Vector<Vect<LargeInteger>>();
		Vector<Vect<LargeInteger>> vB = new Vector<Vect<LargeInteger>>();
		Vector<Vect<LargeInteger>> vC = new Vector<Vect<LargeInteger>>();
		
		vA.add(TestFuncs_Polyhedral.getVectLargeInteger(new int[]{0, 1,  1,  1}));
		
		vB.add(TestFuncs_Polyhedral.getVectLargeInteger(new int[]{1, 0,  0, -1}));
		vB.add(TestFuncs_Polyhedral.getVectLargeInteger(new int[]{0, 1, -1,  0}));
		
		vC.add(TestFuncs_Polyhedral.getVectLargeInteger(new int[]{0, 1, -1, -1}));
		
		AffineModuleRelation<LargeInteger> mA = new AffineModuleRelation<LargeInteger>(_li, vA, 2, 2, 0);
		AffineModuleRelation<LargeInteger> mB = new AffineModuleRelation<LargeInteger>(_li, vB, 2, 2, 0);
		AffineModuleRelation<LargeInteger> mC = new AffineModuleRelation<LargeInteger>(_li, vC, 2, 2, 0);
	
		assert mC.equals(after.getResult(mA, mB));		
	}
	
	/*
	 * With constants
	 */
	public void testAfter_withConstants_2() {
		Vector<Vect<LargeInteger>> vA = new Vector<Vect<LargeInteger>>();
		Vector<Vect<LargeInteger>> vB = new Vector<Vect<LargeInteger>>();
		Vector<Vect<LargeInteger>> vC = new Vector<Vect<LargeInteger>>();
		
		vA.add(TestFuncs_Polyhedral.getVectLargeInteger(new int[]{1, 0,  0,  1, 11,  7}));
		vA.add(TestFuncs_Polyhedral.getVectLargeInteger(new int[]{0, 1,  0,  0,  3,  5}));
		vA.add(TestFuncs_Polyhedral.getVectLargeInteger(new int[]{0, 0,  1,  0,  9, 13}));
		
		vB.add(TestFuncs_Polyhedral.getVectLargeInteger(new int[]{1, 0,  0, -1, 78,  9}));
		vB.add(TestFuncs_Polyhedral.getVectLargeInteger(new int[]{0, 1, -1,  0, 17, -6}));
		
		vC.add(TestFuncs_Polyhedral.getVectLargeInteger(new int[]{0,  0,   0, -1, 87, 22}));
		vC.add(TestFuncs_Polyhedral.getVectLargeInteger(new int[]{1,  0,  -1,  0, 28,  1}));
		vC.add(TestFuncs_Polyhedral.getVectLargeInteger(new int[]{0,  1,   0,  0,  3,  5}));
		
		AffineModuleRelation<LargeInteger> mA = new AffineModuleRelation<LargeInteger>(_li, vA, 2, 2, 2);
		AffineModuleRelation<LargeInteger> mB = new AffineModuleRelation<LargeInteger>(_li, vB, 2, 2, 2);
		AffineModuleRelation<LargeInteger> mC = new AffineModuleRelation<LargeInteger>(_li, vC, 2, 2, 2);
		
		assert mC.equals(after.getResult(mA, mB));		
	}

	/*
	 * With constants
	 */
	public void testAfter_withConstants_3() { // Modification of testAfter_3
		Vector<Vect<LargeInteger>> vA = new Vector<Vect<LargeInteger>>();
		Vector<Vect<LargeInteger>> vB = new Vector<Vect<LargeInteger>>();
		Vector<Vect<LargeInteger>> vC = new Vector<Vect<LargeInteger>>();
		
		vA.add(TestFuncs_Polyhedral.getVectLargeInteger(new int[]{0, 1,  1,  1,  7}));
		
		vB.add(TestFuncs_Polyhedral.getVectLargeInteger(new int[]{1, 0,  0, -1,  3}));
		vB.add(TestFuncs_Polyhedral.getVectLargeInteger(new int[]{0, 1, -1,  0,  2}));
		
		vC.add(TestFuncs_Polyhedral.getVectLargeInteger(new int[]{0, 1, -1, -1, 12}));
		
		AffineModuleRelation<LargeInteger> mA = new AffineModuleRelation<LargeInteger>(_li, vA, 2, 2, 1);
		AffineModuleRelation<LargeInteger> mB = new AffineModuleRelation<LargeInteger>(_li, vB, 2, 2, 1);
		AffineModuleRelation<LargeInteger> mC = new AffineModuleRelation<LargeInteger>(_li, vC, 2, 2, 1);
	
		assert mC.equals(after.getResult(mA, mB));		
	}

	/*
	 * With constants
	 */
	public void testAfter_4() {
		Vector<Vect<LargeInteger>> vA = new Vector<Vect<LargeInteger>>();
		Vector<Vect<LargeInteger>> vB = new Vector<Vect<LargeInteger>>();
		Vector<Vect<LargeInteger>> vC = new Vector<Vect<LargeInteger>>();
		
		vA.add(TestFuncs_Polyhedral.getVectLargeInteger(new int[]{0, 1,  1,  1, 7}));
		
		vB.add(TestFuncs_Polyhedral.getVectLargeInteger(new int[]{1, 0,  0, -1, 3}));
		vB.add(TestFuncs_Polyhedral.getVectLargeInteger(new int[]{0, 1, -1,  0, 2}));
		
		vC.add(TestFuncs_Polyhedral.getVectLargeInteger(new int[]{0, 1, -1, -1, 12}));
		
		AffineModuleRelation<LargeInteger> mA = new AffineModuleRelation<LargeInteger>(_li, vA, 2, 2, 1);
		AffineModuleRelation<LargeInteger> mB = new AffineModuleRelation<LargeInteger>(_li, vB, 2, 2, 1);
		AffineModuleRelation<LargeInteger> mC = new AffineModuleRelation<LargeInteger>(_li, vC, 2, 2, 1);
	
		assert mC.equals(after.getResult(mA, mB));		
	}
}
