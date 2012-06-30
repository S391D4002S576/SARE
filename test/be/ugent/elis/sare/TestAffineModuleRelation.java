package be.ugent.elis.sare;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TestAffineModuleRelation {

	public static Test suite() {
		TestSuite suite = new TestSuite("AffineModuleRelation class");
		//$JUnit-BEGIN$
		suite.addTestSuite(TestAffineModuleRelation_AfterOperation.class);
		suite.addTestSuite(TestAffineModuleRelation_ConjunctionOperation.class);
		//$JUnit-END$
		return suite;
	}

}
