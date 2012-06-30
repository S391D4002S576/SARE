package be.ugent.elis.sare;

import be.elis.ugent.math.derivedsets.Fraction;
import be.elis.ugent.math.derivedsets.Interval;
import be.ugent.elis.sare.domains.ParametrizedIntegerPolyhederDomain;
import org.jscience.mathematics.numbers.Rational;

public class TestFourierMotzkinProjection extends TestFuncs_Polyhedral {
	public String getName() { return "Fourier Motzkin Projection"; }
	
	public void testProjection1() {
		/*
		 * x - y >= -2
		 * x - y <=  2
		 * x + y <=  8
		 * x + y >=  4
		 */
		ParametrizedIntegerPolyhederDomain<Rational> p 
			= convertIntTableToPIPDRational(new int[][]{
				{ 1, -1,  2},
				{-1,  1,  2},
				{-1, -1,  8},
				{ 1,  1, -4}
		}, new int[][]{
		});
		
		ParametrizedIntegerPolyhederDomain<Rational> pProjX = p.calcFourierMotzkinProject(0);
		ParametrizedIntegerPolyhederDomain<Rational> pProjY = p.calcFourierMotzkinProject(1);
		
		Interval<Rational> pProjXInt = pProjX.asInterval();
		Interval<Rational> pProjYInt = pProjY.asInterval();

		Rational rone = Rational.valueOf(1, 1);
		Rational rfive = Rational.valueOf(5, 1);
		Interval<Rational> expectedInterval 
			= new Interval<Rational>(new Fraction<Rational>(rone, rone, Rational.ZERO, Rational.ONE),
									new Fraction<Rational>(rfive, rone, Rational.ZERO, Rational.ONE));
		
		assert pProjXInt.isEqualTo(expectedInterval);
		assert pProjYInt.isEqualTo(expectedInterval);
	}
	
	/*
	 * Test 1 with slight distortions in the input, but the same result
	 */
	public void testProjection2() {
		ParametrizedIntegerPolyhederDomain<Rational> p 
			= convertIntTableToPIPDRational(new int[][]{
				{ 2, -2,  4},
				{-1,  1,  2},
				{-1, -1,  8},
				{-1, -1, 20},
				{ 1,  1, -4}
		}, new int[][]{
		});
		
		ParametrizedIntegerPolyhederDomain<Rational> pProjX = p.calcFourierMotzkinProject(0);
		ParametrizedIntegerPolyhederDomain<Rational> pProjY = p.calcFourierMotzkinProject(1);
		
		Interval<Rational> pProjXInt = pProjX.asInterval();
		Interval<Rational> pProjYInt = pProjY.asInterval();

		Rational rone = Rational.valueOf(1, 1);
		Rational rfive = Rational.valueOf(5, 1);
		Interval<Rational> expectedInterval 
			= new Interval<Rational>(new Fraction<Rational>(rone, rone, Rational.ZERO, Rational.ONE),
									new Fraction<Rational>(rfive, rone, Rational.ZERO, Rational.ONE));
		
		assert pProjXInt.isEqualTo(expectedInterval);
		assert pProjYInt.isEqualTo(expectedInterval);
	}
	
	/*
	 * Test 1 but domain is unbounded in the positive directions
	 */
	public void testProjection3() {
		ParametrizedIntegerPolyhederDomain<Rational> p 
			= convertIntTableToPIPDRational(new int[][]{
				{ 1, -1,  2},
				{-1,  1,  2},
				{ 1,  1, -4}
		}, new int[][]{
		});
		
		ParametrizedIntegerPolyhederDomain<Rational> pProjX = p.calcFourierMotzkinProject(0);
		ParametrizedIntegerPolyhederDomain<Rational> pProjY = p.calcFourierMotzkinProject(1);
		
		Interval<Rational> pProjXInt = pProjX.asInterval();
		Interval<Rational> pProjYInt = pProjY.asInterval();

		Rational rone = Rational.valueOf(1, 1);
		Interval<Rational> expectedInterval 
			= new Interval<Rational>(new Fraction<Rational>(rone, rone, Rational.ZERO, Rational.ONE),
									null);
		
		assert pProjXInt.isEqualTo(expectedInterval);
		assert pProjYInt.isEqualTo(expectedInterval);
	}
}
