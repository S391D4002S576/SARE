package be.ugent.elis.sare;

import org.jscience.mathematics.numbers.LargeInteger;
import org.jscience.mathematics.numbers.Rational;
import org.jscience.mathematics.vectors.DenseMatrix;
import org.jscience.mathematics.vectors.Matrix;

import be.elis.ugent.graphLibrary.Vertex;
import be.elis.ugent.math.Module;
import be.elis.ugent.math.polynomials.Polynom;
import be.ugent.elis.AffineModuleRelation;
import be.ugent.elis.AffineTransformation;
import be.ugent.elis.align.TestCaseExtended;
import be.ugent.elis.cg.CGComputationVertex;
import be.ugent.elis.cg.CGDataVertex;
import be.ugent.elis.cg.CGEdge;
import be.ugent.elis.cg.CGSpacePartitioner;
import be.ugent.elis.cg.CGraph;
import be.ugent.elis.cg.PolyhedralDomainVertexData;
import be.ugent.elis.cg.RDMGraph;
import be.ugent.elis.cg.RSDGraph;
import be.ugent.elis.sare.domains.ParametrizedIntegerPolyhederDomain;

public class TestExperimental extends TestFuncs_Polyhedral {
	static LargeInteger _li = LargeInteger.ZERO;
	
	RDMGraph<LargeInteger> g;
	RSDGraph<LargeInteger> rsdg;
	
	Vertex<PolyhedralDomainVertexData<LargeInteger>>[] cV;
	Vertex<PolyhedralDomainVertexData<LargeInteger>>[] dV;
	
	AffineModuleRelation<LargeInteger>[][] expEdges;
	
	ParametrizedIntegerPolyhederDomain<LargeInteger>[] doms
		= (ParametrizedIntegerPolyhederDomain<LargeInteger>[]) new ParametrizedIntegerPolyhederDomain<?>[4];
	
	private void doSetup(int compVertices, int dataVertices,
			  			 int[] compDomDims, int[] dataDomDims,
			  			 int paramCount) {
		g = new RDMGraph<LargeInteger>(_li);
		cV = (Vertex<PolyhedralDomainVertexData<LargeInteger>>[]) new Vertex<?>[compVertices];
		dV = (Vertex<PolyhedralDomainVertexData<LargeInteger>>[]) new Vertex<?>[dataVertices];

		for (int q = 1; q <= 3; q++) {
			doms[q]	= TestFuncs_Polyhedral.convertIntTableToPIPDLargeInteger(new int[][]{ // (0..n)
					new int[q + paramCount + 1]
			}, new int[][]{
					new int[paramCount + 1]
			});
		}
		
		for (int c = 0; c < cV.length; c++) {
			cV[c] = g.addComputationVertex(new PolyhedralDomainVertexData<LargeInteger>(String.format("c%d", c), 
						doms[compDomDims[c]]
					));
		}

		for (int d = 0; d < dV.length; d++) {
			dV[d] = g.addDataVertex(new PolyhedralDomainVertexData<LargeInteger>(String.format("d%d", d), 
						doms[dataDomDims[d]]
					));
		}
		
		expEdges = (AffineModuleRelation<LargeInteger>[][]) new AffineModuleRelation<?>[cV.length][cV.length];
	}
	
	private void assertActualEdgesAreAsExpected() {
		rsdg = g.fastSpacePartitioning();
		
		Vertex<PolyhedralDomainVertexData<LargeInteger>>[] w 
			= (Vertex<PolyhedralDomainVertexData<LargeInteger>>[]) new Vertex<?>[cV.length];
		for (int c = 0; c < cV.length; c++) w[c] = rsdg.getVertex(c);
		
		for (int a = 0; a < w.length; a++) {
			for (int b = 0; b < w.length; b++) {
				assert (rsdg.getEdges(w[a], w[b]).size() == (expEdges[a][b] == null ? 0 : 1));
				AffineModuleRelation<LargeInteger> actual = rsdg.getEdges(w[a], w[b]).get(0).data;
				AffineModuleRelation<LargeInteger> expected = expEdges[a][b]; 
				assert actual.equals(expected) : String.format("Failure for edge (%d, %d)", a, b);
			}
		}
	}
	
	/**
	 * Trying to get a space partition which isn't parallel to a fully parallel outer loop
	 * for ( i = 0 ; i <= n ; i++) {
	 * 	 for ( j = 0 ; j <= n ; j++) {
	 * 		A[i][j][1] = A[i][j][1] + ...
	 *   }
	 * }
	 * for ( i = 0 ; i <= n ; i++) {
	 * 	 for ( j = 0 ; j <= n ; j++) {
	 * 		A[j][1][i] = A[j][1][i] + ...
	 *   }
	 * }
	 * 
	 * Dependencies:
	 * - SpaceVector intersection
	 */
	public void testPartitionLimLam_1b() {
		doSetup(2, 1, new int[]{2, 2}, new int[]{3}, 1);

		g.addEdge(cV[0], dV[0], TestFuncs_Polyhedral.convertIntTableToAffineTrafoLargeInteger(new int[][]{
				{	1,   0,   0,  0},
				{	0,   1,   0,  0},
				{	0,   0,   0,  1},
				{   0,   0,   0,  1}
		}));
		g.addEdge(cV[1], dV[0], TestFuncs_Polyhedral.convertIntTableToAffineTrafoLargeInteger(new int[][]{
				{	0,   1,   0,  0},
				{	0,   0,   0,  1},
				{	1,   0,   0,  0},
				{   0,   0,   0,  1}
		}));

		expEdges[0][0] = TestFuncs_Polyhedral.getAffineModuleRelationFromGeneratingMatrix(new int[][]{
				{   1,   0,   1,  0,  0,  0  },
				{   0,   1,   0,  1,  0,  0  }
		}, 2, 2, 2);
		expEdges[1][1] = TestFuncs_Polyhedral.getAffineModuleRelationFromGeneratingMatrix(new int[][]{
				{   1,   0,   1,  0,  0,  0  },
				{   0,   1,   0,  1,  0,  0  }
		}, 2, 2, 2);
	
		expEdges[0][1] = TestFuncs_Polyhedral.getAffineModuleRelationFromGeneratingMatrix(new int[][]{
				{   1,   0,   1,  0,  0,  0  },
				{   0,   1,   0,  0,  0, -1  },
				{   0,   0,   0,  1,  0,  1  }
		}, 2, 2, 2);
		expEdges[1][0] = TestFuncs_Polyhedral.getAffineModuleRelationFromGeneratingMatrix(new int[][]{
				{   1,   0,   1,  0,  0,  0  },
				{   0,   1,   0,  0,  0, -1  },
				{   0,   0,   0,  1,  0,  1  }
		}, 2, 2, 2);
		
		assertActualEdgesAreAsExpected();		
	}
	
	/**
	 * Test from Lim & Lam (Communication-Free Parallelisation via Affine Transformations), but modified 
	 * - To show that some sets of dependences lead to processor spaces which are not full-dimensional
	 * for ( i = 0 ; i <= n ; i++) {
	 * 	 for ( j = 0 ; j <= n ; j++) {
	 * 		A[i][j][1] = A[i][j][1] + ...
	 *   }
	 * }
	 * for ( i = 0 ; i <= n ; i++) {
	 * 	 for ( j = 0 ; j <= n ; j++) {
	 * 		A[j][1][i] = A[j][1][i] + ...
	 *   }
	 * }
	 * 
	 * Dependencies:
	 * - SpaceVector intersection
	 */
	public void testPartitionLimLam_1c() {
		doSetup(2, 1, new int[]{2, 2}, new int[]{3}, 1);

		g.addEdge(cV[0], dV[0], TestFuncs_Polyhedral.convertIntTableToAffineTrafoLargeInteger(new int[][]{
				{	1,   0,   0,  0},
				{	0,   1,   0,  0},
				{	0,   0,   0,  1},
				{   0,   0,   0,  1}
		}));
		g.addEdge(cV[1], dV[0], TestFuncs_Polyhedral.convertIntTableToAffineTrafoLargeInteger(new int[][]{
				{	0,   1,   0,  0},
				{	0,   0,   0,  1},
				{	1,   0,   0,  0},
				{   0,   0,   0,  1}
		}));

		expEdges[0][0] = TestFuncs_Polyhedral.getAffineModuleRelationFromGeneratingMatrix(new int[][]{
				{   1,   0,   1,  0,  0,  0  },
				{   0,   1,   0,  1,  0,  0  }
		}, 2, 2, 2);
		expEdges[1][1] = TestFuncs_Polyhedral.getAffineModuleRelationFromGeneratingMatrix(new int[][]{
				{   1,   0,   1,  0,  0,  0  },
				{   0,   1,   0,  1,  0,  0  }
		}, 2, 2, 2);
	
		expEdges[0][1] = TestFuncs_Polyhedral.getAffineModuleRelationFromGeneratingMatrix(new int[][]{
				{   1,   0,   1,  0,  0,  0  },
				{   0,   1,   0,  0,  0, -1  },
				{   0,   0,   0,  1,  0,  1  }
		}, 2, 2, 2);
		expEdges[1][0] = TestFuncs_Polyhedral.getAffineModuleRelationFromGeneratingMatrix(new int[][]{
				{   1,   0,   1,  0,  0,  0  },
				{   0,   1,   0,  0,  0, -1  },
				{   0,   0,   0,  1,  0,  1  }
		}, 2, 2, 2);
		
		assertActualEdgesAreAsExpected();		
	}
	
	/**
	 * for ( k = 0 ; k <= n; k++) {
	 *     A[n - k] := A[n - k] + C[n - k];
	 *     B[k] := B[k] + A[k];
	 *     C[k] := C[k] + B[k];
	 * }
	 */
	public void testPartition1D_SimpleParametric() {
		doSetup(3, 3, new int[]{1, 1, 1}, new int[]{1, 1, 1}, 1);

		g.addEdge(cV[0], dV[0], TestFuncs_Polyhedral.convertIntTableToAffineTrafoLargeInteger(new int[][]{
				{  -1,  1,  0},
				{   0,  0,  1}
		}));
		g.addEdge(cV[0], dV[2], TestFuncs_Polyhedral.convertIntTableToAffineTrafoLargeInteger(new int[][]{
				{  -1,  1,  0},
				{   0,  0,  1}
		}));
		g.addEdge(cV[1], dV[1], TestFuncs_Polyhedral.convertIntTableToAffineTrafoLargeInteger(new int[][]{
				{	1,  0,  0},
				{   0,  0,  1}
		}));
		g.addEdge(cV[1], dV[0], TestFuncs_Polyhedral.convertIntTableToAffineTrafoLargeInteger(new int[][]{
				{	1,  0,  0},
				{   0,  0,  1}
		}));
		g.addEdge(cV[2], dV[2], TestFuncs_Polyhedral.convertIntTableToAffineTrafoLargeInteger(new int[][]{
				{   1,  0,  0},
				{   0,  0,  1}
		}));
		g.addEdge(cV[1], dV[0], TestFuncs_Polyhedral.convertIntTableToAffineTrafoLargeInteger(new int[][]{
				{	1,  0,  0},
				{   0,  0,  1}
		}));

		expEdges[0][1] = TestFuncs_Polyhedral.getAffineModuleRelationFromGeneratingMatrix(new int[][]{
		}, 2, 2, 3);
		expEdges[1][0] = TestFuncs_Polyhedral.getAffineModuleRelationFromGeneratingMatrix(new int[][]{
		}, 2, 2, 3);
		expEdges[0][0] = TestFuncs_Polyhedral.getAffineModuleRelationFromGeneratingMatrix(new int[][]{
		}, 2, 2, 3);
		expEdges[1][1] = TestFuncs_Polyhedral.getAffineModuleRelationFromGeneratingMatrix(new int[][]{
		}, 2, 2, 3);
		
		assertActualEdgesAreAsExpected();		
	}

	/**
	 * for ( k = 0 ; k <= n; k++) {
	 *     A[k] := A[k] + C[k];
	 *     B[k] := B[k] + A[k];
	 *     C[k] := C[k] + B[k];
	 * }
	 */
	public void testPartition1D() {
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
	}

	/**
	 * for ( k = 0 ; k <= n; k++) {
	 * for ( l = 0 ; l <= n; k++) {
	 *     A[k][l] := A[k][l] + C[k + m][l + 1];
	 *     C[k][l] := C[k][l] + A[k][l];
	 * }
	 * }
	 */
	public void testPartition1D_Param2() {
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
	}

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
	public void testPartition3D() {
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
			
	}
}
