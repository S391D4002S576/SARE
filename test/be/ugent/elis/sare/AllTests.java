package be.ugent.elis.sare;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("be.ugent.elis.sare");
		//$JUnit-BEGIN$
		suite.addTestSuite(TestRADGGraph_TransitiveClosure.class);
		suite.addTest(TestAffineModuleRelation.suite());
		suite.addTestSuite(TestFourierMotzkinProjection.class);
		suite.addTestSuite(TestRDMG_calculateRSDG.class);
		suite.addTestSuite(TestFastSpacePartitioner.class);
		suite.addTestSuite(TestExperimental.class);
		//$JUnit-END$
		return suite;
	}

}
