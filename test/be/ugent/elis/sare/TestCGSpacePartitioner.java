package be.ugent.elis.sare;

import org.jscience.mathematics.numbers.Rational;
import org.jscience.mathematics.vectors.DenseMatrix;
import org.jscience.mathematics.vectors.Matrix;

import be.elis.ugent.math.Module;
import be.elis.ugent.math.polynomials.Polynom;
import be.ugent.elis.AffineTransformation;
import be.ugent.elis.align.TestCaseExtended;
import be.ugent.elis.cg.CGComputationVertex;
import be.ugent.elis.cg.CGDataVertex;
import be.ugent.elis.cg.CGEdge;
import be.ugent.elis.cg.CGSpacePartitioner;
import be.ugent.elis.cg.CGraph;
import be.ugent.elis.sare.domains.ParametrizedIntegerPolyhederDomain;

public class TestCGSpacePartitioner extends TestFuncs_Polyhedral {
	/**
	 * for ( i = 0 ; i <= n ; i++) {
	 * 	 for ( j = 0 ; j <= m ; j++) {
	 *     A[i][j] := A[i][j] + B[i - 1][j];
	 *     B[i][j] := A[i][j - 1] + B[i][j];
	 *   }
	 * }
	 * 
	 * Dependencies:
	 * - SpaceVector intersection
	 */
	public void testPartition2() {/*
		CGraph cg = new CGraph();
		
		CGComputationVertex cgCV1 = cg.addComputationVertex("1");
		CGComputationVertex cgCV2 = cg.addComputationVertex("2");
		
		CGDataVertex cgDVA = cg.addDataVertex("1");
		CGDataVertex cgDVB = cg.addDataVertex("2");
		
		CGEdge e1A = cg.addEdge(cgCV1, cgDVA);
		CGEdge e1B = cg.addEdge(cgCV1, cgDVB);
		CGEdge e2A = cg.addEdge(cgCV2, cgDVA);
		CGEdge e2B = cg.addEdge(cgCV2, cgDVB);
		
		ParametrizedIntegerPolyhederDomain<Rational> compDomain = convertIntTableToPIPDRational(new int[][]{ // (0..n, 0..m)
				{ 1,  0,  0,  0,  0},
				{-1,  0,  1,  0,  0},
				{ 0,  1,  0,  0,  0},
				{ 0, -1,  0,  1,  0}
		}, new int[][]{
				{ 1,  0,  0},
				{ 0,  1,  0}
		});

		cgCV1.domain = compDomain;
		cgCV2.domain = compDomain;
		cgDVA.domain = compDomain; // exact domain not important here, just dimension
		cgDVB.domain = compDomain;
		
		e1A.dataDependence = convertIntTableToAffineTrafoRational(new int[][]{
				{	1,   0,   0,  0,  0},
				{	0,   1,   0,  0,  0},
				{   0,   0,   0,  0,  1}
		});
		e1B.dataDependence = convertIntTableToAffineTrafoRational(new int[][]{
				{	1,   0,   0,  0, -1},
				{	0,   1,   0,  0,  0},
				{   0,   0,   0,  0,  1}
		});
		e2A.dataDependence = convertIntTableToAffineTrafoRational(new int[][]{
				{	1,   0,   0,  0,  0},
				{	0,   1,   0,  0, -1},
				{   0,   0,   0,  0,  1}
		});
		e2B.dataDependence = convertIntTableToAffineTrafoRational(new int[][]{
				{	1,   0,   0,  0,  0},
				{	0,   1,   0,  0,  0},
				{   0,   0,   0,  0,  1}
		});
		
		CGSpacePartitioner<Rational> spacePartitioner 
			= new CGSpacePartitioner<Rational>(cg, Rational.ZERO, Rational.ONE);*/
	}
	
	/**
	 * for ( i = 0 ; i <= n ; i++) {
	 * 	 for ( j = 0 ; j <= m ; j++) {
	 *     A[i][j] := A[i][j] + B[i - 7][j];
	 *     B[i][j] := A[i][j - 7] + B[i][j];
	 *   }
	 * }
	 * 
	 * Dependencies:
	 * - SpaceVector intersection
	 */
	public void testPartition2b() {
		CGraph cg = new CGraph();
		
		CGComputationVertex cgCV1 = cg.addComputationVertex("1");
		CGComputationVertex cgCV2 = cg.addComputationVertex("2");
		
		CGDataVertex cgDVA = cg.addDataVertex("1");
		CGDataVertex cgDVB = cg.addDataVertex("2");
		
		CGEdge e1A = cg.addEdge(cgCV1, cgDVA);
		CGEdge e1B = cg.addEdge(cgCV1, cgDVB);
		CGEdge e2A = cg.addEdge(cgCV2, cgDVA);
		CGEdge e2B = cg.addEdge(cgCV2, cgDVB);
		
		ParametrizedIntegerPolyhederDomain<Rational> compDomain = convertIntTableToPIPDRational(new int[][]{ // (0..n, 0..m)
				{ 1,  0,  0,  0,  0},
				{-1,  0,  1,  0,  0},
				{ 0,  1,  0,  0,  0},
				{ 0, -1,  0,  1,  0}
		}, new int[][]{
				{ 1,  0,  0},
				{ 0,  1,  0}
		});

		cgCV1.domain = compDomain;
		cgCV2.domain = compDomain;
		cgDVA.domain = compDomain; // exact domain not important here, just dimension
		cgDVB.domain = compDomain;
		
		e1A.dataDependence = convertIntTableToAffineTrafoRational(new int[][]{
				{	1,   0,   0,  0,  0},
				{	0,   1,   0,  0,  0},
				{   0,   0,   0,  0,  1}
		});
		e1B.dataDependence = convertIntTableToAffineTrafoRational(new int[][]{
				{	1,   0,   0,  0, -7},
				{	0,   1,   0,  0,  0},
				{   0,   0,   0,  0,  1}
		});
		e2A.dataDependence = convertIntTableToAffineTrafoRational(new int[][]{
				{	1,   0,   0,  0,  0},
				{	0,   1,   0,  0, -7},
				{   0,   0,   0,  0,  1}
		});
		e2B.dataDependence = convertIntTableToAffineTrafoRational(new int[][]{
				{	1,   0,   0,  0,  0},
				{	0,   1,   0,  0,  0},
				{   0,   0,   0,  0,  1}
		});

		CGSpacePartitioner<Rational> spacePartitioner 
		= new CGSpacePartitioner<Rational>(Rational.ZERO, cg, null, 0);
	
		Module<Rational> expectedSpace = null;  
			/*= convertIntTableToModuleRational(new int[][]{
				 {  1, -1,  0,  0,  0,  1, -1,  0,  0,  7 },
				 {  0,  0,  1,  0,  0,  0,  0,  1,  0,  0 },
				 {  0,  0,  0,  1,  0,  0,  0,  0,  1,  0 },
				 {  0,  0,  0,  0,  1,  0,  0,  0,  0,  1 }
			});*/ 
	
		assert spacePartitioner.spacePartitionSpace.isEqualTo(expectedSpace, null);		
	}
	
	/**
	 * Test Pugh: Transitive Closure of Infinite Graphs and its Applications
	 * for ( i = 0 ; i <= n ; i++) {
	 * 	 for ( j = 0 ; j <= m ; j++) {
	 *     A[i][j] := A[i + 1][j + z];
	 *   }
	 * }
	 * 
	 * Dependencies:
	 * - SpaceVector intersection
	 */
	public void testPartitionPugh() {
		CGraph cg = new CGraph();
		
		CGComputationVertex cgCV1 = cg.addComputationVertex("1");
		
		CGDataVertex cgDVA = cg.addDataVertex("1");
		
		CGEdge e1A = cg.addEdge(cgCV1, cgDVA);
		CGEdge e1B = cg.addEdge(cgCV1, cgDVA);
		
		ParametrizedIntegerPolyhederDomain<Rational> compDomain = convertIntTableToPIPDRational(new int[][]{ // (0..n, 0..m)
				{ 1,  0,  0,  0,  0,  0},
				{-1,  0,  1,  0,  0,  0},
				{ 0,  1,  0,  0,  0,  0},
				{ 0, -1,  0,  1,  0,  0}
		}, new int[][]{
				{ 1,  0,  0,  0},
				{ 0,  1,  0,  0},
				{ 0,  0,  1,  0}
		});

		cgCV1.domain = compDomain;
		cgDVA.domain = compDomain; // exact domain not important here, just dimension
		
		e1A.dataDependence = convertIntTableToAffineTrafoRational(new int[][]{
				{	1,   0,   0,  0,  0,  0},
				{	0,   1,   0,  0,  0,  0},
				{   0,   0,   0,  0,  0,  1}
		});
		e1B.dataDependence = convertIntTableToAffineTrafoRational(new int[][]{
				{	1,   0,   0,  0,  0,  1},
				{	0,   1,   0,  0,  1,  0},
				{   0,   0,   0,  0,  0,  1}
		});

		CGSpacePartitioner<Rational> spacePartitioner 
		= new CGSpacePartitioner<Rational>(Rational.ZERO, cg, null, 0);
	
		Module<Rational> expectedSpace  = null;
			/*= convertIntTableToModuleRational(new int[][]{
				 {  1,  1,  0,  0,  0,  1,  1,  0,  0,  7 },
				 {  0,  0,  1,  0,  0,  0,  0,  1,  0,  0 },
				 {  0,  0,  0,  1,  0,  0,  0,  0,  1,  0 },
				 {  0,  0,  0,  0,  1,  0,  0,  0,  0,  1 }
			});*/ 
	
		assert spacePartitioner.spacePartitionSpace.isEqualTo(expectedSpace, null);		
	}
	
	/**
	 * for ( i = 0 ; i <= n ; i++) {
	 * 	 for ( j = 0 ; j <= m ; j++) {
	 *     A[i][j] := A[i][j] + B[i][j];
	 *     B[i][j] := A[i][j] + B[i][j];
	 *   }
	 * }
	 */
	public void testPartition() {/*
		CGraph cg = new CGraph();
		
		CGComputationVertex cgCV1 = cg.addComputationVertex("1");
		CGComputationVertex cgCV2 = cg.addComputationVertex("2");
		
		CGDataVertex cgDVA = cg.addDataVertex("1");
		CGDataVertex cgDVB = cg.addDataVertex("2");
		
		CGEdge e1A = cg.addEdge(cgCV1, cgDVA);
		CGEdge e1B = cg.addEdge(cgCV1, cgDVB);
		CGEdge e2A = cg.addEdge(cgCV2, cgDVA);
		CGEdge e2B = cg.addEdge(cgCV2, cgDVB);
		
		ParametrizedIntegerPolyhederDomain<Rational> compDomain = convertIntTableToPIPDRational(new int[][]{ // (0..n, 0..m)
				{ 1,  0,  0,  0,  0},
				{-1,  0,  1,  0,  0},
				{ 0,  1,  0,  0,  0},
				{ 0, -1,  0,  1,  0}
		}, new int[][]{
				{ 1,  0,  0},
				{ 0,  1,  0}
		});

		cgCV1.domain = compDomain;
		cgCV2.domain = compDomain;
		cgDVA.domain = compDomain; // exact domain not important here, just dimension
		cgDVB.domain = compDomain;
		
		e1A.dataDependence = convertIntTableToAffineTrafoRational(new int[][]{
				{	1,   0,   0,  0,  0},
				{	0,   1,   0,  0,  0},
				{   0,   0,   0,  0,  1}
		});
		e1B.dataDependence = convertIntTableToAffineTrafoRational(new int[][]{
				{	1,   0,   0,  0,  0},
				{	0,   1,   0,  0,  0},
				{   0,   0,   0,  0,  1}
		});
		e2A.dataDependence = convertIntTableToAffineTrafoRational(new int[][]{
				{	1,   0,   0,  0,  0},
				{	0,   1,   0,  0,  0},
				{   0,   0,   0,  0,  1}
		});
		e2B.dataDependence = convertIntTableToAffineTrafoRational(new int[][]{
				{	1,   0,   0,  0,  0},
				{	0,   1,   0,  0,  0},
				{   0,   0,   0,  0,  1}
		});
		
		CGSpacePartitioner<Rational> spacePartitioner 
			= new CGSpacePartitioner<Rational>(cg, Rational.ZERO, Rational.ONE);
	*/}
	
	/**
	 * for ( k = 0 ; k <= n; k++) {
	 *     A[n - k] := A[n - k] + C[n - k];
	 *     B[k] := B[k] + A[k];
	 *     C[k] := C[k] + B[k];
	 * }
	 */
	public void testPartition1D_SimpleParametric() {/*
		CGraph cg = new CGraph();
		
		CGComputationVertex cgCV1 = cg.addComputationVertex("1");
		CGComputationVertex cgCV2 = cg.addComputationVertex("2");
		CGComputationVertex cgCV3 = cg.addComputationVertex("3");
		
		CGDataVertex cgDVA = cg.addDataVertex("A");
		CGDataVertex cgDVB = cg.addDataVertex("B");
		CGDataVertex cgDVC = cg.addDataVertex("C");
		
		CGEdge e1A = cg.addEdge(cgCV1, cgDVA);
		CGEdge e1C = cg.addEdge(cgCV1, cgDVC);
		CGEdge e2A = cg.addEdge(cgCV2, cgDVA);
		CGEdge e2B = cg.addEdge(cgCV2, cgDVB);
		CGEdge e3C = cg.addEdge(cgCV3, cgDVC);
		CGEdge e3B = cg.addEdge(cgCV3, cgDVB);
		
		ParametrizedIntegerPolyhederDomain<Rational> compDomain = convertIntTableToPIPDRational(new int[][]{ // (0..n)
				{ 1,  0,  0},
				{-1,  1,  0}
		}, new int[][]{
				{ 1,  0}
		});

		cgCV1.domain = compDomain;
		cgCV2.domain = compDomain;
		cgCV3.domain = compDomain;
		cgDVA.domain = compDomain; // exact domain not important here, just dimension
		cgDVB.domain = compDomain;
		cgDVC.domain = compDomain;

		e1A.dataDependence = convertIntTableToAffineTrafoRational(new int[][]{
				{  -1,  1,  0},
				{   0,  0,  1}
		});
		e1C.dataDependence = convertIntTableToAffineTrafoRational(new int[][]{
				{  -1,  1,  0},
				{   0,  0,  1}
		});
		e2A.dataDependence = convertIntTableToAffineTrafoRational(new int[][]{
				{	1,  0,  0},
				{   0,  0,  1}
		});
		e2B.dataDependence = convertIntTableToAffineTrafoRational(new int[][]{
				{	1,  0,  0},
				{   0,  0,  1}
		});
		e3C.dataDependence = convertIntTableToAffineTrafoRational(new int[][]{
				{   1,  0,  0},
				{   0,  0,  1}
		});
		e3B.dataDependence = convertIntTableToAffineTrafoRational(new int[][]{
				{   1,  0,  0},
				{   0,  0,  1}
		});
		
		CGSpacePartitioner<Rational> spacePartitioner 
			= new CGSpacePartitioner<Rational>(cg, Rational.ZERO, Rational.ONE);
	*/}

	/**
	 * for ( k = 0 ; k <= n; k++) {
	 *     A[k] := A[k] + C[k];
	 *     B[k] := B[k] + A[k];
	 *     C[k] := C[k] + B[k];
	 * }
	 */
	public void testPartition1D() {/*
		CGraph cg = new CGraph();
		
		CGComputationVertex cgCV1 = cg.addComputationVertex("1");
		CGComputationVertex cgCV2 = cg.addComputationVertex("2");
		CGComputationVertex cgCV3 = cg.addComputationVertex("3");
		
		CGDataVertex cgDVA = cg.addDataVertex("A");
		CGDataVertex cgDVB = cg.addDataVertex("B");
		CGDataVertex cgDVC = cg.addDataVertex("C");
		
		CGEdge e1A = cg.addEdge(cgCV1, cgDVA);
		CGEdge e1C = cg.addEdge(cgCV1, cgDVC);
		CGEdge e2A = cg.addEdge(cgCV2, cgDVA);
		CGEdge e2B = cg.addEdge(cgCV2, cgDVB);
		CGEdge e3C = cg.addEdge(cgCV3, cgDVC);
		CGEdge e3B = cg.addEdge(cgCV3, cgDVB);
		
		ParametrizedIntegerPolyhederDomain<Rational> compDomain = convertIntTableToPIPDRational(new int[][]{ // (0..n)
				{ 1,  0,  0},
				{-1,  1,  0}
		}, new int[][]{
				{ 1,  0}
		});

		cgCV1.domain = compDomain;
		cgCV2.domain = compDomain;
		cgCV3.domain = compDomain;
		cgDVA.domain = compDomain; // exact domain not important here, just dimension
		cgDVB.domain = compDomain;
		cgDVC.domain = compDomain;

		e1A.dataDependence = convertIntTableToAffineTrafoRational(new int[][]{
				{	1,  0,  0},
				{   0,  0,  1}
		});
		e1C.dataDependence = convertIntTableToAffineTrafoRational(new int[][]{
				{	1,  0,  0},
				{   0,  0,  1}
		});
		e2A.dataDependence = convertIntTableToAffineTrafoRational(new int[][]{
				{	1,  0,  0},
				{   0,  0,  1}
		});
		e2B.dataDependence = convertIntTableToAffineTrafoRational(new int[][]{
				{	1,  0,  0},
				{   0,  0,  1}
		});
		e3C.dataDependence = convertIntTableToAffineTrafoRational(new int[][]{
				{	1,  0,  0},
				{   0,  0,  1}
		});
		e3B.dataDependence = convertIntTableToAffineTrafoRational(new int[][]{
				{	1,  0,  0},
				{   0,  0,  1}
		});
		
		CGSpacePartitioner<Rational> spacePartitioner 
			= new CGSpacePartitioner<Rational>(cg, Rational.ZERO, Rational.ONE);
	*/}

	/**
	 * for ( k = 0 ; k <= n; k++) {
	 * for ( l = 0 ; l <= n; k++) {
	 *     A[k][l] := A[k][l] + C[k + m][l + 1];
	 *     C[k][l] := C[k][l] + A[k][l];
	 * }
	 * }
	 */
	public void testPartition1D_Param2() {/*
		CGraph cg = new CGraph();
		
		CGComputationVertex cgCV1 = cg.addComputationVertex("1");
		CGComputationVertex cgCV3 = cg.addComputationVertex("3");
		
		CGDataVertex cgDVA = cg.addDataVertex("A");
		CGDataVertex cgDVC = cg.addDataVertex("C");
		
		CGEdge e1A = cg.addEdge(cgCV1, cgDVA);
		CGEdge e1C = cg.addEdge(cgCV1, cgDVC);
		CGEdge e3C = cg.addEdge(cgCV3, cgDVC);
		CGEdge e3B = cg.addEdge(cgCV3, cgDVA);
		
		ParametrizedIntegerPolyhederDomain<Rational> compDomain = convertIntTableToPIPDRational(new int[][]{ // (0..n)x(0..n)
				{ 1,  0,  0,  0,  0},
				{-1,  0,  1,  0,  0},
				{ 0,  1,  0,  0,  0},
				{ 0, -1,  1,  0,  0}
		}, new int[][]{
				{ 1,  0,  0},
				{ 0,  1,  0}
		});

		cgCV1.domain = compDomain;
		cgCV3.domain = compDomain;
		cgDVA.domain = compDomain; // exact domain not important here, just dimension
		cgDVC.domain = compDomain;

		e1A.dataDependence = convertIntTableToAffineTrafoRational(new int[][]{
				{	1,  0,  0,  0,  0},
				{	0,  1,  0,  0,  0},
				{   0,  0,  0,  0,  1}
		});
		e1C.dataDependence = convertIntTableToAffineTrafoRational(new int[][]{
				{	1,  0,  0,  1,  0},
				{	0,  1,  0,  0,  1},
				{   0,  0,  0,  0,  1}
		});
		e3C.dataDependence = convertIntTableToAffineTrafoRational(new int[][]{
				{	1,  0,  0,  0,  0},
				{	0,  1,  0,  0,  0},
				{   0,  0,  0,  0,  1}
		});
		e3B.dataDependence = convertIntTableToAffineTrafoRational(new int[][]{
				{	1,  0,  0,  0,  0},
				{	0,  1,  0,  0,  0},
				{   0,  0,  0,  0,  1}
		});
		
		CGSpacePartitioner<Rational> spacePartitioner 
			= new CGSpacePartitioner<Rational>(cg, Rational.ZERO, Rational.ONE);
	*/}

	/**
	 * for ( k = 0 ; k <= p ; k++) {
	 * for ( i = 0 ; i <= n ; i++) {
	 * 	 for ( j = 0 ; j <= m ; j++) {
	 *     A[k][i][j] := A[k][i][j] + C[k][i - 1][j];
	 *     B[k][i][j] := B[k][i][j] + A[k][i][j - 1];
	 *     C[k][i][j] := C[k][i][j] + B[k - 1][i][j];
	 *   }
	 * }
	 * }
	 */
	public void testPartition3D() {/*
		CGraph cg = new CGraph();
		
		CGComputationVertex cgCV1 = cg.addComputationVertex("1");
		CGComputationVertex cgCV2 = cg.addComputationVertex("2");
		CGComputationVertex cgCV3 = cg.addComputationVertex("3");
		
		CGDataVertex cgDVA = cg.addDataVertex("A");
		CGDataVertex cgDVB = cg.addDataVertex("B");
		CGDataVertex cgDVC = cg.addDataVertex("C");
		
		CGEdge e1A = cg.addEdge(cgCV1, cgDVA);
		CGEdge e1C = cg.addEdge(cgCV1, cgDVC);
		CGEdge e2A = cg.addEdge(cgCV2, cgDVA);
		CGEdge e2B = cg.addEdge(cgCV2, cgDVB);
		CGEdge e3C = cg.addEdge(cgCV3, cgDVC);
		CGEdge e3B = cg.addEdge(cgCV3, cgDVB);
		
		ParametrizedIntegerPolyhederDomain<Rational> compDomain = convertIntTableToPIPDRational(new int[][]{ // (0..n, 0..m)
				{ 1,  0,  0,  0,  0,  0,  0},
				{-1,  0,  0,  1,  0,  0,  0},
				{ 0,  1,  0,  0,  0,  0,  0},
				{ 0, -1,  0,  0,  1,  0,  0},
				{ 0,  0,  1,  0,  0,  0,  0},
				{ 0,  0, -1,  0,  0,  1,  0}
		}, new int[][]{
				{ 1,  0,  0,  0},
				{ 0,  1,  0,  0},
				{ 0,  0,  1,  0}
		});

		cgCV1.domain = compDomain;
		cgCV2.domain = compDomain;
		cgCV3.domain = compDomain;
		cgDVA.domain = compDomain; // exact domain not important here, just dimension
		cgDVB.domain = compDomain;
		cgDVC.domain = compDomain;

		e1A.dataDependence = convertIntTableToAffineTrafoRational(new int[][]{
				{	1,   0,   0,   0,  0,  0,  0},
				{	0,   1,   0,   0,  0,  0,  0},
				{	0,   0,   1,   0,  0,  0,  0},
				{   0,   0,   0,   0,  0,  0,  1}
		});
		e1C.dataDependence = convertIntTableToAffineTrafoRational(new int[][]{
				{	1,   0,   0,   0,  0,  0,  0},
				{	0,   1,   0,   0,  0,  0,  0},
				{	0,   0,   1,   0,  0,  0,  0},
				{   0,   0,   0,   0,  0,  0,  1}
		});
		e2A.dataDependence = convertIntTableToAffineTrafoRational(new int[][]{
				{	1,   0,   0,   0,  0,  0,  0},
				{	0,   1,   0,   0,  0,  0,  0},
				{	0,   0,   1,   0,  0,  0,  0},
				{   0,   0,   0,   0,  0,  0,  1}
		});
		e2B.dataDependence = convertIntTableToAffineTrafoRational(new int[][]{
				{	1,   0,   0,   0,  0,  0,  0},
				{	0,   1,   0,   0,  0,  0,  0},
				{	0,   0,   1,   0,  0,  0,  0},
				{   0,   0,   0,   0,  0,  0,  1}
		});
		e3C.dataDependence = convertIntTableToAffineTrafoRational(new int[][]{
				{	1,   0,   0,   0,  0,  0,  0},
				{	0,   1,   0,   0,  0,  0,  0},
				{	0,   0,   1,   0,  0,  0,  0},
				{   0,   0,   0,   0,  0,  0,  1}
		});
		e3B.dataDependence = convertIntTableToAffineTrafoRational(new int[][]{
				{	1,   0,   0,   0,  0,  0,  0},
				{	0,   1,   0,   0,  0,  0,  0},
				{	0,   0,   1,   0,  0,  0,  0},
				{   0,   0,   0,   0,  0,  0,  1}
		});
		
		CGSpacePartitioner<Rational> spacePartitioner 
			= new CGSpacePartitioner<Rational>(cg, Rational.ZERO, Rational.ONE);
			*/
	}
}
