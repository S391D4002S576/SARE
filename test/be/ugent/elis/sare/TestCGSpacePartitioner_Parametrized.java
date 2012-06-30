package be.ugent.elis.sare;

import org.jscience.mathematics.functions.ComparatorClass;
import org.jscience.mathematics.numbers.LargeInteger;
import org.jscience.mathematics.numbers.Rational;
import org.jscience.mathematics.vectors.DenseMatrix;
import org.jscience.mathematics.vectors.Matrix;

import be.elis.ugent.math.Module;
import be.elis.ugent.math.polynomials.Monom;
import be.elis.ugent.math.polynomials.Polynom;
import be.elis.ugent.math.structures.Ring_;
import be.ugent.elis.AffineTransformation;
import be.ugent.elis.align.TestCaseExtended;
import be.ugent.elis.cg.CGComputationVertex;
import be.ugent.elis.cg.CGDataVertex;
import be.ugent.elis.cg.CGEdge;
import be.ugent.elis.cg.CGSpacePartitioner;
import be.ugent.elis.cg.CGraph;
import be.ugent.elis.sare.domains.ParametrizedIntegerPolyhederDomain;


public class TestCGSpacePartitioner_Parametrized extends TestCaseExtended {
	Polynom<Rational> poly0;
	Polynom<Rational> poly1;
	
	private Monom<LargeInteger> monI(int coeff, int... i) {
		return new Monom<LargeInteger>(LargeInteger.ZERO, LargeInteger.ONE, LargeInteger.valueOf(coeff), i);
	}
	
	private Polynom<LargeInteger> polI(Monom<LargeInteger>... m) {
		return new Polynom<LargeInteger>(LargeInteger.ZERO, LargeInteger.ONE, m);
	}
	
	private Monom<Rational> monR(int coeff, int... i) {
		return new Monom<Rational>(Rational.ZERO, Rational.ONE, Rational.valueOf(coeff, 1), i);
	}
	
	private Polynom<Rational> polR(Monom<Rational>... m) {
		return new Polynom<Rational>(Rational.ZERO, Rational.ONE, m);
	}
	
	public static <R extends Ring_<R>> Matrix<R> convertRingTableToRingMatrix(R[][] i) {
		R[][] elms = (R[][]) new Ring_[i.length][];
		int columnCount = -1;
		
		for (int r = 0; r < i.length; r++) {
			if (r == 0) columnCount = i[r].length;
			assert (columnCount == i[r].length);
			
			elms[r] = (R[]) new Ring_[columnCount];
			for (int c = 0; c < columnCount; c++) {
				elms[r][c] = i[r][c];
			}
		}
		
		return DenseMatrix.valueOf(elms);
	}
	
	public static <R extends Ring_<R>> ParametrizedIntegerPolyhederDomain<R> convertRingTableToRingPIPD(R[][] domain, R[][] context, R zero, R one) {
		return new ParametrizedIntegerPolyhederDomain<R>( 
				convertRingTableToRingMatrix(domain), convertRingTableToRingMatrix(context), 
                zero, one);
	}
	
	public ParametrizedIntegerPolyhederDomain<Polynom<Rational>> convertRationalPolynomialTableToRationalPolynomialPIPD(Polynom<Rational>[][] domain, Polynom<Rational>[][] context) {
		return convertRingTableToRingPIPD(domain, context, poly0, poly1);
	}
	
	public ParametrizedIntegerPolyhederDomain<Polynom<Rational>> convertRatPolynomialTableToRationalPolynomialPIPD(Polynom[][] domain, Polynom[][] context) {
		return convertRingTableToRingPIPD((Polynom<Rational>[][]) domain, (Polynom<Rational>[][]) context, poly0, poly1);
	}
	
	public static <R extends Ring_<R>> AffineTransformation<R> convertIntTableToRingAffineTrafo(R[][] i, R zero, R one) {
		return new AffineTransformation<R>(convertRingTableToRingMatrix(i), zero, one);
	}

	public AffineTransformation<Polynom<Rational>> convertRationalPolynomialTableToRationalPolynomialAffineTrafo(Polynom<Rational>[][] i) {
		return convertIntTableToRingAffineTrafo(i, poly0, poly1);
	}
	
	public AffineTransformation<Polynom<Rational>> convertRatPolynomialTableToRationalPolynomialAffineTrafo(Polynom[][] i) {
		return convertIntTableToRingAffineTrafo((Polynom<Rational>[][]) i, poly0, poly1);
	}
	
	/**
	 * for ( i = 0 ; i <= n ; i++) {
	 * 	 for ( j = 0 ; j <= m ; j++) {
	 *     A[i][j] := A[i][j] + B[i - p][j];
	 *     B[i][j] := A[i][j - p] + B[i][j];
	 *   }
	 * }
	 * 
	 * Dependencies:
	 * - SpaceVector intersection
	 * 
	 * Expected solution
	 * [        [1[]]        [-1[]] [-1[0, 0, 1]]         [1[]]        [-1[]]            []]
	 * [           []            []        [-1[]]            []            []        [-1[]]]
	 */
	public void testPartition2() {
		CGraph cg = new CGraph();
		
		CGComputationVertex cgCV1 = cg.addComputationVertex("1");
		CGComputationVertex cgCV2 = cg.addComputationVertex("2");
		
		CGDataVertex cgDVA = cg.addDataVertex("1");
		CGDataVertex cgDVB = cg.addDataVertex("2");
		
		CGEdge e1A = cg.addEdge(cgCV1, cgDVA);
		CGEdge e1B = cg.addEdge(cgCV1, cgDVB);
		CGEdge e2A = cg.addEdge(cgCV2, cgDVA);
		CGEdge e2B = cg.addEdge(cgCV2, cgDVB);
		
		Polynom<Rational> interfaceDummy = new Polynom<Rational>(Rational.ZERO, Rational.ONE);
		poly0 = interfaceDummy.getAddition().getIdentityElement();
		poly1 = interfaceDummy.getMultiplication().getIdentityElement();
		
		Polynom<Rational> polyM = polR(monR(1, 1, 0, 0));
		Polynom<Rational> polyN = polR(monR(1, 0, 1, 0));
		Polynom<Rational> polyP = polR(monR(1, 0, 0, 1));
		
		Polynom<Rational> _poly1 = poly1.opposite();
		Polynom<Rational> _polyP = polyP.opposite();
		
		ParametrizedIntegerPolyhederDomain<Polynom<Rational>> compDomain 
			= convertRatPolynomialTableToRationalPolynomialPIPD(new Polynom[][]{ // (0..n, 0..m)
				{ poly1,  poly0,  poly0},
				{_poly1,  poly0,  polyM},
				{ poly0,  poly1,  poly0},
				{ poly0, _poly1,  polyN}
		}, new Polynom[][]{
		});

		cgCV1.domain = compDomain;
		cgCV2.domain = compDomain;
		cgDVA.domain = compDomain; // exact domain not important here, just dimension
		cgDVB.domain = compDomain;
		
		e1A.dataDependence = convertRatPolynomialTableToRationalPolynomialAffineTrafo(new Polynom[][]{
				{	poly1,   poly0,  poly0},
				{	poly0,   poly1,    poly0},
				{   poly0,   poly0,    poly1}
		});
		e1B.dataDependence = convertRatPolynomialTableToRationalPolynomialAffineTrafo(new Polynom[][]{
				{	poly1,   poly0,   _polyP},
				{	poly0,   poly1,  poly0},
				{   poly0,   poly0,  poly1}
		});
		e2A.dataDependence = convertRatPolynomialTableToRationalPolynomialAffineTrafo(new Polynom[][]{
				{	poly1,   poly0,   poly0},
				{	poly0,   poly1,   _polyP},
				{   poly0,   poly0, poly1}
		});
		e2B.dataDependence = convertRatPolynomialTableToRationalPolynomialAffineTrafo(new Polynom[][]{
				{	poly1,   poly0,   poly0},
				{	poly0,   poly1,    poly0},
				{   poly0,   poly0,   poly1}
		});
		
		ComparatorClass<Polynom<Rational>> monomOrder = Polynom.getTotalDegreeLexicoGraphicalAMC();
		
		CGSpacePartitioner<Polynom<Rational>> spacePartitioner 
			= new CGSpacePartitioner<Polynom<Rational>>(poly0, cg, monomOrder, 0);
		
		Module<Polynom<Rational>> expectedSpace
			= new Module<Polynom<Rational>>(poly0, new Polynom[][]{
					{ poly1, _poly1, _polyP,  poly1, _poly1,  poly0},
					{ poly0,  poly0, _poly1,  poly0,  poly0, _poly1}
			});
		
		assert spacePartitioner.spacePartitionSpace.isEqualTo(expectedSpace, monomOrder);
	}
	
	/**
	 * for ( k = 0 ; k <= n; k++) {
	 * 	 for ( l = 0 ; l <= n; l++) {
	 *     A[k][l] := A[k][l] + C[k + m][l + 1];
	 *     C[k][l] := C[k][l] + A[k][l];
	 *   }
	 * }
	 * 
	 * Expected result:
	 * [        [1[]]    [-1[1, 0]]            []         [1[]]    [-1[1, 0]]            []]
     * [           []            []    [-1[1, 0]]            []            []    [-1[1, 0]]]
	 */
	public void testPartition1D_Param2() {
		Polynom<Rational> interfaceDummy = new Polynom<Rational>(Rational.ZERO, Rational.ONE);
		poly0 = interfaceDummy.getAddition().getIdentityElement();
		poly1 = interfaceDummy.getMultiplication().getIdentityElement();
		
		CGraph cg = new CGraph();
		
		CGComputationVertex cgCV1 = cg.addComputationVertex("1");
		CGComputationVertex cgCV3 = cg.addComputationVertex("3");
		
		CGDataVertex cgDVA = cg.addDataVertex("A");
		CGDataVertex cgDVC = cg.addDataVertex("C");
		
		CGEdge e1A = cg.addEdge(cgCV1, cgDVA);
		CGEdge e1C = cg.addEdge(cgCV1, cgDVC);
		CGEdge e3C = cg.addEdge(cgCV3, cgDVC);
		CGEdge e3B = cg.addEdge(cgCV3, cgDVA);
		
		Polynom<Rational> polyM = polR(monR(1, 1, 0));
		Polynom<Rational> polyN = polR(monR(1, 0, 1));
		Polynom<Rational> _poly1 = poly1.opposite();
		Polynom<Rational> _polyM = polyM.opposite();
		Polynom<Rational> _polyN = polyN.opposite();
		
		ParametrizedIntegerPolyhederDomain<Polynom<Rational>> compDomain 
			= convertRatPolynomialTableToRationalPolynomialPIPD(new Polynom[][]{ // (0..n)x(0..n)
				{ poly1,  poly0,  poly0},
				{_poly1,  poly0,  polyN},
				{ poly0,  poly1,  poly0},
				{ poly0, _poly1,  polyN}
		}, new Polynom[][]{
		});

		cgCV1.domain = compDomain;
		cgCV3.domain = compDomain;
		cgDVA.domain = compDomain; // exact domain not important here, just dimension
		cgDVC.domain = compDomain;

		e1A.dataDependence = convertRatPolynomialTableToRationalPolynomialAffineTrafo(new Polynom[][]{
				{ poly1, poly0, poly0},
				{ poly0, poly1, poly0},
				{ poly0, poly0, poly1}
		});
		e1C.dataDependence = convertRatPolynomialTableToRationalPolynomialAffineTrafo(new Polynom[][]{
				{ poly1, poly0, polyM},
				{ poly0, poly1, poly1},
				{ poly0, poly0, poly1}
		});
		e3C.dataDependence = convertRatPolynomialTableToRationalPolynomialAffineTrafo(new Polynom[][]{
				{ poly1, poly0, poly0},
				{ poly0, poly1, poly0},
				{ poly0, poly0, poly1}
		});
		e3B.dataDependence = convertRatPolynomialTableToRationalPolynomialAffineTrafo(new Polynom[][]{
				{ poly1, poly0, poly0},
				{ poly0, poly1, poly0},
				{ poly0, poly0, poly1}
		});
		
		ComparatorClass<Polynom<Rational>> monomOrder = Polynom.getTotalDegreeLexicoGraphicalAMC();
		
		CGSpacePartitioner<Polynom<Rational>> spacePartitioner 
			= new CGSpacePartitioner<Polynom<Rational>>(poly0, cg, monomOrder, 0);
		
		Module<Polynom<Rational>> expectedSpace
			= new Module<Polynom<Rational>>(poly0, new Polynom[][]{
					{ poly1, _polyM,  poly0,  poly1, _polyM,  poly0},
					{ poly0,  poly0, _polyM,  poly0,  poly0, _polyM}
			});
		
		assert spacePartitioner.spacePartitionSpace.isEqualTo(expectedSpace, monomOrder);
	}
	
	/**
	 * for ( x = 0 ; x <= n; x++) {
	 * 	 for ( y = 0 ; y <= n; y++) {
	 *     A[x][y] := A[x][y] + C[x + m][y + p];
	 *     C[x][y] := C[x][y] + A[x][y];
	 *   }
	 * }
	 * 
	 *			{_polyP,  polyM,  poly0, _polyP,  polyM,  poly0},
	 *			{ poly0,  poly0,  poly1,  poly0,  poly0,  poly1}
	 */
	public void testPartition1D_Param3() {
		Polynom<Rational> interfaceDummy = new Polynom<Rational>(Rational.ZERO, Rational.ONE);
		poly0 = interfaceDummy.getAddition().getIdentityElement();
		poly1 = interfaceDummy.getMultiplication().getIdentityElement();
		
		Polynom<Rational> polyM = polR(monR(1, 1, 0, 0));
		Polynom<Rational> polyN = polR(monR(1, 0, 1, 0));
		Polynom<Rational> polyP = polR(monR(1, 0, 0, 1));
		
		Polynom<Rational> _poly1 = poly1.opposite();
		Polynom<Rational> _polyM = polyM.opposite();
		Polynom<Rational> _polyN = polyN.opposite();
		Polynom<Rational> _polyP = polyP.opposite();

		CGraph cg = new CGraph();
		
		CGComputationVertex cgCV1 = cg.addComputationVertex("1");
		CGComputationVertex cgCV3 = cg.addComputationVertex("3");
		
		CGDataVertex cgDVA = cg.addDataVertex("A");
		CGDataVertex cgDVC = cg.addDataVertex("C");
		
		CGEdge e1A = cg.addEdge(cgCV1, cgDVA);
		CGEdge e1C = cg.addEdge(cgCV1, cgDVC);
		CGEdge e3C = cg.addEdge(cgCV3, cgDVC);
		CGEdge e3B = cg.addEdge(cgCV3, cgDVA);
		
		ParametrizedIntegerPolyhederDomain<Polynom<Rational>> compDomain = convertRatPolynomialTableToRationalPolynomialPIPD(new Polynom[][]{ // (0..n)x(0..n)
				{ poly1,  poly0,  poly0},
				{_poly1,  poly0,  polyN},
				{ poly0,  poly1,  poly0},
				{ poly0, _poly1,  polyN}
		}, new Polynom[][]{
		});

		cgCV1.domain = compDomain;
		cgCV3.domain = compDomain;
		cgDVA.domain = compDomain; // exact domain not important here, just dimension
		cgDVC.domain = compDomain;

		e1A.dataDependence = convertRatPolynomialTableToRationalPolynomialAffineTrafo(new Polynom[][]{
				{ poly1, poly0, poly0},
				{ poly0, poly1, poly0},
				{ poly0, poly0, poly1}
		});
		e1C.dataDependence = convertRatPolynomialTableToRationalPolynomialAffineTrafo(new Polynom[][]{
				{ poly1, poly0, polyM},
				{ poly0, poly1, polyP},
				{ poly0, poly0, poly1}
		});
		e3C.dataDependence = convertRatPolynomialTableToRationalPolynomialAffineTrafo(new Polynom[][]{
				{ poly1, poly0, poly0},
				{ poly0, poly1, poly0},
				{ poly0, poly0, poly1}
		});
		e3B.dataDependence = convertRatPolynomialTableToRationalPolynomialAffineTrafo(new Polynom[][]{
				{ poly1, poly0, poly0},
				{ poly0, poly1, poly0},
				{ poly0, poly0, poly1}
		});
		
		ComparatorClass<Polynom<Rational>> monomOrder = Polynom.getTotalDegreeLexicoGraphicalAMC();
		
		CGSpacePartitioner<Polynom<Rational>> spacePartitioner 
			= new CGSpacePartitioner<Polynom<Rational>>(poly0, cg, monomOrder, 0);
		
		Module<Polynom<Rational>> expectedSpace
			= new Module<Polynom<Rational>>(poly0, new Polynom[][]{
					{_polyP,  polyM,  poly0, _polyP,  polyM,  poly0},
					{ poly0,  poly0,  poly1,  poly0,  poly0,  poly1}
			});

		assert spacePartitioner.spacePartitionSpace.isEqualTo(expectedSpace, monomOrder);
	}
	
	/**
	 * Lim & Lam example 2:
	 * for ( x = 0 ; x <= M; x++) {
	 *   for ( y = 0 ; y <= M; y++) {
	 *   	A[x][y] := A[x - 1][y] + A[x - 1][N - y + 1];
	 *   }
	 * }
	 * 
	 */
	public void testPartition1D_Param7() {
		Polynom<Rational> interfaceDummy = new Polynom<Rational>(Rational.ZERO, Rational.ONE);
		poly0 = interfaceDummy.getAddition().getIdentityElement();
		poly1 = interfaceDummy.getMultiplication().getIdentityElement();
		
		Polynom<Rational> polyM = polR(monR(1, 1, 0, 0));
		Polynom<Rational> polyN = polR(monR(1, 0, 1, 0));
		
		Polynom<Rational> _poly1 = poly1.opposite();
		Polynom<Rational> _polyM = polyM.opposite();
		Polynom<Rational> _polyN = polyN.opposite();
		
		CGraph cg = new CGraph();
		
		CGComputationVertex cgCV1 = cg.addComputationVertex("1");
		
		CGDataVertex cgAVC = cg.addDataVertex("C");
		
		CGEdge e1C = cg.addEdge(cgCV1, cgAVC);
		CGEdge e1C2 = cg.addEdge(cgCV1, cgAVC);
		CGEdge e1C3 = cg.addEdge(cgCV1, cgAVC);
		
		ParametrizedIntegerPolyhederDomain<Polynom<Rational>> compDomain = convertRatPolynomialTableToRationalPolynomialPIPD(new Polynom[][]{ // (0..n)x(0..n)
				{ poly1,  poly0,  poly0},
				{_poly1,  poly0,  polyN},
				{ poly0,  poly1,  poly0},
				{ poly0, _poly1,  polyN}
		}, new Polynom[][]{
		});

		ParametrizedIntegerPolyhederDomain<Polynom<Rational>> dataDomain = convertRatPolynomialTableToRationalPolynomialPIPD(new Polynom[][]{ // (0..n)x(0..n)
				{ poly1,  poly0,  poly0},
				{_poly1,  poly0,  polyN},
				{ poly0,  poly1,  poly0},
				{ poly0, _poly1,  polyN}
		}, new Polynom[][]{
		});

		cgCV1.domain = compDomain;
		cgAVC.domain = dataDomain;

		e1C.dataDependence = convertRatPolynomialTableToRationalPolynomialAffineTrafo(new Polynom[][]{
				{ poly1,  poly0,  poly0},
				{ poly0,  poly1,  poly0},
				{ poly0,  poly0,  poly1}
		});
		e1C2.dataDependence = convertRatPolynomialTableToRationalPolynomialAffineTrafo(new Polynom[][]{
				{ poly1,  poly0, _poly1},
				{ poly0,  poly1,  poly0},
				{ poly0,  poly0,  poly1}
		});
		e1C3.dataDependence = convertRatPolynomialTableToRationalPolynomialAffineTrafo(new Polynom[][]{
				{ poly1,  poly0, _poly1},
				{ poly0, _poly1,  polyN.plus(poly1)},
				{ poly0,  poly0,  poly1}
		});
		
		ComparatorClass<Polynom<Rational>> monomOrder = Polynom.getTotalDegreeLexicoGraphicalAMC();
		
		CGSpacePartitioner<Polynom<Rational>> spacePartitioner 
			= new CGSpacePartitioner<Polynom<Rational>>(poly0, cg, monomOrder, 0);
		
		Module<Polynom<Rational>> expectedSpace
			= new Module<Polynom<Rational>>(poly0, new Polynom[][]{
					{ poly0,  poly0,  poly1}
			});

		assert spacePartitioner.spacePartitionSpace.isEqualTo(expectedSpace, monomOrder);
	}
	
	/**
	 * Lim & Lam example 2:
	 * for ( x = 0 ; x <= M; x++) {
	 *   for ( y = 0 ; y <= M; y++) {
	 *   	A[x][y] := A[x - 1][y] + A[x - 2][y];
	 *   }
	 * }
	 * 
	 */
	public void testPartition1D_Param8() {
		Polynom<Rational> interfaceDummy = new Polynom<Rational>(Rational.ZERO, Rational.ONE);
		poly0 = interfaceDummy.getAddition().getIdentityElement();
		poly1 = interfaceDummy.getMultiplication().getIdentityElement();
		
		Polynom<Rational> polyM = polR(monR(1, 1, 0, 0));
		Polynom<Rational> polyN = polR(monR(1, 0, 1, 0));
		
		Polynom<Rational> _poly1 = poly1.opposite();
		Polynom<Rational> _polyM = polyM.opposite();
		Polynom<Rational> _polyN = polyN.opposite();
		
		CGraph cg = new CGraph();
		
		CGComputationVertex cgCV1 = cg.addComputationVertex("1");
		
		CGDataVertex cgAVC = cg.addDataVertex("C");
		
		CGEdge e1C = cg.addEdge(cgCV1, cgAVC);
		CGEdge e1C2 = cg.addEdge(cgCV1, cgAVC);
		CGEdge e1C3 = cg.addEdge(cgCV1, cgAVC);
		
		ParametrizedIntegerPolyhederDomain<Polynom<Rational>> compDomain = convertRatPolynomialTableToRationalPolynomialPIPD(new Polynom[][]{ // (0..n)x(0..n)
				{ poly1,  poly0,  poly0},
				{_poly1,  poly0,  polyN},
				{ poly0,  poly1,  poly0},
				{ poly0, _poly1,  polyN}
		}, new Polynom[][]{
		});

		ParametrizedIntegerPolyhederDomain<Polynom<Rational>> dataDomain = convertRatPolynomialTableToRationalPolynomialPIPD(new Polynom[][]{ // (0..n)x(0..n)
				{ poly1,  poly0,  poly0},
				{_poly1,  poly0,  polyN},
				{ poly0,  poly1,  poly0},
				{ poly0, _poly1,  polyN}
		}, new Polynom[][]{
		});

		cgCV1.domain = compDomain;
		cgAVC.domain = dataDomain;

		e1C.dataDependence = convertRatPolynomialTableToRationalPolynomialAffineTrafo(new Polynom[][]{
				{ poly1,  poly0,  poly0},
				{ poly0,  poly1,  poly0},
				{ poly0,  poly0,  poly1}
		});
		e1C2.dataDependence = convertRatPolynomialTableToRationalPolynomialAffineTrafo(new Polynom[][]{
				{ poly1,  poly0, _poly1},
				{ poly0,  poly1,  poly0},
				{ poly0,  poly0,  poly1}
		});
		e1C3.dataDependence = convertRatPolynomialTableToRationalPolynomialAffineTrafo(new Polynom[][]{
				{ poly1,  poly0, _poly1.plus(_poly1)},
				{ poly0,  poly1,  poly0},
				{ poly0,  poly0,  poly1}
		});
		
		ComparatorClass<Polynom<Rational>> monomOrder = Polynom.getTotalDegreeLexicoGraphicalAMC();
		
		CGSpacePartitioner<Polynom<Rational>> spacePartitioner 
			= new CGSpacePartitioner<Polynom<Rational>>(poly0, cg, monomOrder, 0);
		
		Module<Polynom<Rational>> expectedSpace
			= new Module<Polynom<Rational>>(poly0, new Polynom[][]{
					{ poly0,  poly1,  poly0},
					{ poly0,  poly0,  poly1}
			});

		assert spacePartitioner.spacePartitionSpace.isEqualTo(expectedSpace, monomOrder);
	}
	
	/**
	 * for ( x = 0 ; x <= m; x++) {
	 *   q := Z[x];
	 * 	 for ( y = 0 ; y <= n; y++) {
	 *     A[x][y] := 0;
	 *     for ( z = 0 ; z <= t ; z++ ) {
	 *       A[x][y] := A[x][y] + B[q*z + y];
	 *     }
	 *   }
	 * }
	 */
	public void testPartition1D_Param5() {
		Polynom<Rational> interfaceDummy = new Polynom<Rational>(Rational.ZERO, Rational.ONE);
		poly0 = interfaceDummy.getAddition().getIdentityElement();
		poly1 = interfaceDummy.getMultiplication().getIdentityElement();
		
		Polynom<Rational> polyM = polR(monR(1, 1, 0, 0, 0, 0));
		Polynom<Rational> polyN = polR(monR(1, 0, 1, 0, 0, 0));
		Polynom<Rational> polyT = polR(monR(1, 0, 0, 1, 0, 0));
		Polynom<Rational> polyQ = polR(monR(1, 0, 0, 0, 1, 0));
		Polynom<Rational> polyX = polR(monR(1, 0, 0, 0, 0, 1));
		
		Polynom<Rational> _poly1 = poly1.opposite();
		Polynom<Rational> _polyM = polyM.opposite();
		Polynom<Rational> _polyN = polyN.opposite();
		Polynom<Rational> _polyT = polyT.opposite();
		Polynom<Rational> _polyQ = polyQ.opposite();

		CGraph cg = new CGraph();
		
		CGComputationVertex cgCV1 = cg.addComputationVertex("1");
		
		CGDataVertex cgDVA = cg.addDataVertex("A");
		CGDataVertex cgDVB = cg.addDataVertex("B");
		
		CGEdge e1A = cg.addEdge(cgCV1, cgDVA);
		CGEdge e1C = cg.addEdge(cgCV1, cgDVB);
		
		ParametrizedIntegerPolyhederDomain<Polynom<Rational>> dataDomain2 = convertRatPolynomialTableToRationalPolynomialPIPD(new Polynom[][]{ // (0..n)x(0..n)
				{ poly1,  poly0,  poly0},
				{_poly1,  poly0,  polyN},
				{ poly0,  poly1,  poly0},
				{ poly0, _poly1,  polyN}
		}, new Polynom[][]{
		});

		ParametrizedIntegerPolyhederDomain<Polynom<Rational>> dataDomain1 = convertRatPolynomialTableToRationalPolynomialPIPD(new Polynom[][]{ // (0..n)x(0..n)
				{ poly1,  poly0},
				{_poly1,  polyN}
		}, new Polynom[][]{
		});

		cgCV1.domain = dataDomain2;
		cgDVA.domain = dataDomain2; // exact domain not important here, just dimension
		cgDVB.domain = dataDomain1;

		e1A.dataDependence = convertRatPolynomialTableToRationalPolynomialAffineTrafo(new Polynom[][]{
				{ poly0, poly0, polyX},
				{ poly0, poly1, poly0},
				{ poly0, poly0, poly1}
		});
		e1C.dataDependence = convertRatPolynomialTableToRationalPolynomialAffineTrafo(new Polynom[][]{
				{ poly1, polyQ,  poly0},
				{ poly0, poly0,  poly1}
		});
		
		ComparatorClass<Polynom<Rational>> monomOrder = Polynom.getTotalDegreeLexicoGraphicalAMC();
		
		CGSpacePartitioner<Polynom<Rational>> spacePartitioner 
			= new CGSpacePartitioner<Polynom<Rational>>(poly0, cg, monomOrder, 0);
		
		Module<Polynom<Rational>> expectedSpace
			= new Module<Polynom<Rational>>(poly0, new Polynom[][]{
					{ poly1,  poly0,  poly0},
					{ poly0,  poly1,  poly0},
					{ poly0,  poly0,  poly1}
			});

		assert spacePartitioner.spacePartitionSpace.isEqualTo(expectedSpace, monomOrder);
	}
	
	/**
	 * for ( g = 0 ; g <= floor(N/Q) + 1 ; g++) {
	 *   for ( h = 0 ; h <= floor(M/R) + 1 ; h++) {
	 *     for ( i = 0 ; i <= Q ; i++) {
	 * 	     for ( j = 0 ; j <= R ; j++) {
	 *         A[Q*g + i][R*h + j] := A[Q*g + i][R*h + j]     + B[Q*g + i - P][R*h + j];
	 *         B[Q*g + i][R*h + j] := A[Q*g + i][R*h + j - P] + B[Q*g + i]    [R*h + j];
	 *       }  
	 *     }    
	 *   }
	 * }
	 * 
	 * Dependencies:
	 * - SpaceVector intersection
	 * 
	 * Expected solution
	 * [        [1[]]        [-1[]] [-1[0, 0, 1]]         [1[]]        [-1[]]            []]
	 * [           []            []        [-1[]]            []            []        [-1[]]]
	 */
	public void testPartition2_Tiled() {
		CGraph cg = new CGraph();
		
		CGComputationVertex cgCV1 = cg.addComputationVertex("1");
		CGComputationVertex cgCV2 = cg.addComputationVertex("2");
		
		CGDataVertex cgDVA = cg.addDataVertex("1");
		CGDataVertex cgDVB = cg.addDataVertex("2");
		
		CGEdge e1A = cg.addEdge(cgCV1, cgDVA);
		CGEdge e1B = cg.addEdge(cgCV1, cgDVB);
		CGEdge e2A = cg.addEdge(cgCV2, cgDVA);
		CGEdge e2B = cg.addEdge(cgCV2, cgDVB);
		
		Polynom<Rational> interfaceDummy = new Polynom<Rational>(Rational.ZERO, Rational.ONE);
		poly0 = interfaceDummy.getAddition().getIdentityElement();
		poly1 = interfaceDummy.getMultiplication().getIdentityElement();
		
		Polynom<Rational> polyM = polR(monR(1, 1, 0, 0));
		Polynom<Rational> polyN = polR(monR(1, 0, 1, 0));
		Polynom<Rational> polyP = polR(monR(1, 0, 0, 1));
		
		Polynom<Rational> _poly1 = poly1.opposite();
		Polynom<Rational> _polyP = polyP.opposite();
		
		ParametrizedIntegerPolyhederDomain<Polynom<Rational>> compDomain 
			= convertRatPolynomialTableToRationalPolynomialPIPD(new Polynom[][]{ // (0..n, 0..m)
				{ poly1,  poly0,  poly0},
				{_poly1,  poly0,  polyM},
				{ poly0,  poly1,  poly0},
				{ poly0, _poly1,  polyN}
		}, new Polynom[][]{
		});

		cgCV1.domain = compDomain;
		cgCV2.domain = compDomain;
		cgDVA.domain = compDomain; // exact domain not important here, just dimension
		cgDVB.domain = compDomain;
		
		e1A.dataDependence = convertRatPolynomialTableToRationalPolynomialAffineTrafo(new Polynom[][]{
				{	poly1,   poly0,  poly0},
				{	poly0,   poly1,    poly0},
				{   poly0,   poly0,    poly1}
		});
		e1B.dataDependence = convertRatPolynomialTableToRationalPolynomialAffineTrafo(new Polynom[][]{
				{	poly1,   poly0,   _polyP},
				{	poly0,   poly1,  poly0},
				{   poly0,   poly0,  poly1}
		});
		e2A.dataDependence = convertRatPolynomialTableToRationalPolynomialAffineTrafo(new Polynom[][]{
				{	poly1,   poly0,   poly0},
				{	poly0,   poly1,   _polyP},
				{   poly0,   poly0, poly1}
		});
		e2B.dataDependence = convertRatPolynomialTableToRationalPolynomialAffineTrafo(new Polynom[][]{
				{	poly1,   poly0,   poly0},
				{	poly0,   poly1,    poly0},
				{   poly0,   poly0,   poly1}
		});
		
		ComparatorClass<Polynom<Rational>> monomOrder = Polynom.getTotalDegreeLexicoGraphicalAMC();
		
		CGSpacePartitioner<Polynom<Rational>> spacePartitioner 
			= new CGSpacePartitioner<Polynom<Rational>>(poly0, cg, monomOrder, 0);
		
		Module<Polynom<Rational>> expectedSpace
			= new Module<Polynom<Rational>>(poly0, new Polynom[][]{
					{ poly1, _poly1, _polyP,  poly1, _poly1,  poly0},
					{ poly0,  poly0, _poly1,  poly0,  poly0, _poly1}
			});
		
		assert spacePartitioner.spacePartitionSpace.isEqualTo(expectedSpace, monomOrder);
	}

	/**
	 * for ( x = 0 ; x <= m; x++) {
	 *   q := Z[x];
	 * 	 for ( y = 0 ; y <= n; y++) {
	 *     A[x][y] := 0;
	 *     for ( z = 0 ; z <= t ; z++ ) { 
	 *       A[x][y] := A[x][y] + B[q*z + y];
	 *     }
	 *   }
	 * }
	 */
	public void testPartition1D_Param6() {
		for (int r = 0; r < M; r++) {
			for (int c = 0; c < P; c++) {
				for (int s = 0; s < N; s++) {
					Q[r][c] := L[r][s] * R[s][c];
				}
			}
		}
		
		for (int f = 0; f < floor(M/Y) + 1; f++) {
			for (int g = 0; g < floor(N/X) + 1; f++) {
				for (int r = Y*f; r < Y*(f + 1); r++) {
					for (int c = X*g; c < X*(g + 1); c++) {
						for (int s = 0; s < N; s++) {
							Q[r][c] := Q[r][c] + L[r][s] * R[s][c];
						}
					}
				}
			}
		}
	}
	
	/**
	 * for ( x = 0 ; x <= n; x++) {
	 * 	 for ( y = 0 ; y <= n; y++) {
	 * 	   for ( z = 0 ; z <= n; z++) {
	 *       A[x][y][z] := A[x][y][z] + C[m*x][y][z] + C[q*x][y][z];
	 *       C[x][y][z] := C[x][y][z] + A[x][y][z];
	 *     }
	 *   }
	 * }
	 */
	public void testPartition1D_Param4() {
		Polynom<Rational> interfaceDummy = new Polynom<Rational>(Rational.ZERO, Rational.ONE);
		poly0 = interfaceDummy.getAddition().getIdentityElement();
		poly1 = interfaceDummy.getMultiplication().getIdentityElement();
		
		Polynom<Rational> polyM = polR(monR(1, 1, 0, 0));
		Polynom<Rational> polyN = polR(monR(1, 0, 1, 0));
		Polynom<Rational> polyQ = polR(monR(1, 0, 0, 1));
		
		Polynom<Rational> _poly1 = poly1.opposite();
		Polynom<Rational> _polyM = polyM.opposite();
		Polynom<Rational> _polyN = polyN.opposite();
		Polynom<Rational> _polyQ = polyQ.opposite();
		
		CGraph cg = new CGraph();
		
		CGComputationVertex cgCV1 = cg.addComputationVertex("1");
		CGComputationVertex cgCV3 = cg.addComputationVertex("3");
		
		CGDataVertex cgDVC = cg.addDataVertex("C");
		
		CGEdge e1C = cg.addEdge(cgCV1, cgDVC);
		CGEdge e1C2 = cg.addEdge(cgCV1, cgDVC);
		CGEdge e3C = cg.addEdge(cgCV3, cgDVC);
		
		ParametrizedIntegerPolyhederDomain<Polynom<Rational>> compDomain = convertRatPolynomialTableToRationalPolynomialPIPD(new Polynom[][]{ // (0..n)x(0..n)
				{ poly1,  poly0,  poly0,  poly0},
				{_poly1,  poly0,  poly0,  polyN},
				{ poly0,  poly1,  poly0,  poly0},
				{ poly0, _poly1,  poly0,  polyN},
				{ poly0,  poly0,  poly1,  poly0},
				{ poly0,  poly0, _poly1,  polyN}
		}, new Polynom[][]{
		});

		ParametrizedIntegerPolyhederDomain<Polynom<Rational>> dataDomain = convertRatPolynomialTableToRationalPolynomialPIPD(new Polynom[][]{ // (0..n)x(0..n)
				{ poly1,  poly0,  poly0,  poly0},
				{_poly1,  poly0,  poly0,  polyN},
				{ poly0,  poly1,  poly0,  poly0},
				{ poly0, _poly1,  poly0,  polyN},
				{ poly0,  poly0,  poly1,  poly0},
				{ poly0,  poly0, _poly1,  polyN}
		}, new Polynom[][]{
		});

		cgCV1.domain = compDomain;
		cgCV3.domain = compDomain;
		cgDVC.domain = dataDomain;

		e1C.dataDependence = convertRatPolynomialTableToRationalPolynomialAffineTrafo(new Polynom[][]{
				{ polyM,  poly0,  poly0,  poly0},
				{ poly0,  poly1,  poly0,  poly0},
				{ poly0,  poly0,  poly1,  poly0},
				{ poly0,  poly0,  poly0,  poly1}
		});
		e1C2.dataDependence = convertRatPolynomialTableToRationalPolynomialAffineTrafo(new Polynom[][]{
				{ polyQ,  poly0,  poly0,  poly0},
				{ poly0,  poly1,  poly0,  poly0},
				{ poly0,  poly0,  poly1,  poly0},
				{ poly0,  poly0,  poly0,  poly1}
		});
		e3C.dataDependence = convertRatPolynomialTableToRationalPolynomialAffineTrafo(new Polynom[][]{
				{ poly1,  poly0,  poly0,  poly0},
				{ poly0,  poly1,  poly0,  poly0},
				{ poly0,  poly0,  poly1,  poly0},
				{ poly0,  poly0,  poly0,  poly1}
		});
		
		ComparatorClass<Polynom<Rational>> monomOrder = Polynom.getTotalDegreeLexicoGraphicalAMC();
		
		CGSpacePartitioner<Polynom<Rational>> spacePartitioner 
			= new CGSpacePartitioner<Polynom<Rational>>(poly0, cg, monomOrder, 0);
		
		Module<Polynom<Rational>> expectedSpace
			= new Module<Polynom<Rational>>(poly0, new Polynom[][]{
					{_polyQ,  polyM,  poly0, _polyQ,  polyM,  poly0},
					{ poly0,  poly0,  poly1,  poly0,  poly0,  poly1}
			});

		assert spacePartitioner.spacePartitionSpace.isEqualTo(expectedSpace, monomOrder);
	}
}
