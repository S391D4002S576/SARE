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

public class TestFastSpacePartitioner extends TestFuncs_Polyhedral {
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
	 * for ( i = 0 ; i <= n ; i++) {
	 * 	 for ( j = 0 ; j <= m ; j++) {
	 *     A[i][j] := A[i][j] + B[i][j];
	 *     B[i][j] := A[i][j] + B[i][j];
	 *   }
	 * }
	 */
	public void testPartition() {
		doSetup(2, 2, new int[]{2, 2}, new int[]{2, 2}, 2);

		g.addEdge(cV[0], dV[0], TestFuncs_Polyhedral.convertIntTableToAffineTrafoLargeInteger(new int[][]{
				{	1,   0,   0,  0,  0},
				{	0,   1,   0,  0,  0},
				{   0,   0,   0,  0,  1}
		}));
		g.addEdge(cV[0], dV[1], TestFuncs_Polyhedral.convertIntTableToAffineTrafoLargeInteger(new int[][]{
				{	1,   0,   0,  0,  0},
				{	0,   1,   0,  0,  0},
				{   0,   0,   0,  0,  1}
		}));
		g.addEdge(cV[1], dV[0], TestFuncs_Polyhedral.convertIntTableToAffineTrafoLargeInteger(new int[][]{
				{	1,   0,   0,  0,  0},
				{	0,   1,   0,  0,  0},
				{   0,   0,   0,  0,  1}
		}));
		g.addEdge(cV[1], dV[1], TestFuncs_Polyhedral.convertIntTableToAffineTrafoLargeInteger(new int[][]{
				{	1,   0,   0,  0,  0},
				{	0,   1,   0,  0,  0},
				{   0,   0,   0,  0,  1}
		}));

		expEdges[0][1] = TestFuncs_Polyhedral.getAffineModuleRelationFromGeneratingMatrix(new int[][]{
				{  1,  0,  1,  0,  0,  0,  0 },				
				{  0,  1,  0,  1,  0,  0,  0 }				
		}, 2, 2, 3);
		expEdges[1][0] = TestFuncs_Polyhedral.getAffineModuleRelationFromGeneratingMatrix(new int[][]{
				{  1,  0,  1,  0,  0,  0,  0 },				
				{  0,  1,  0,  1,  0,  0,  0 }				
		}, 2, 2, 3);
		expEdges[0][0] = TestFuncs_Polyhedral.getAffineModuleRelationFromGeneratingMatrix(new int[][]{
				{  1,  0,  1,  0,  0,  0,  0 },				
				{  0,  1,  0,  1,  0,  0,  0 }				
		}, 2, 2, 3);
		expEdges[1][1] = TestFuncs_Polyhedral.getAffineModuleRelationFromGeneratingMatrix(new int[][]{
				{  1,  0,  1,  0,  0,  0,  0 },				
				{  0,  1,  0,  1,  0,  0,  0 }				
		}, 2, 2, 3);
		
		assertActualEdgesAreAsExpected();		
	}
	
	/**
	 * for ( i = 0 ; i <= n ; i++) {
	 * 	 for ( j = 0 ; j <= m ; j++) {
	 *     A[i][j] := A[i][j] + B[i - 1][j];
	 *     B[i][j] := A[i][j - 1] + B[i][j];
	 *   }
	 * }

	 * Transformation without time mapping gives:
	 * ( i = i0 {0..n} )
	 * Invars: 
	 * iA - jA + iB - jB + 1 = 0
	 * 
	 *  
	 * {
	 * 	 for ( j = 0 ; j <= m ; j++) {
	 *     A[i][j] := A[i][j] + B[i - 1][j];
	 *     B[i][j] := A[i][j - 1] + B[i][j];
	 *   }
	 * } ???????????
	 * 
	 * Dependencies:
	 * - SpaceVector intersection
	 */
	public void testPartition2() {
		doSetup(2, 2, new int[]{2, 2}, new int[]{2, 2}, 2); 
			
		g.addEdge(cV[0], dV[0], TestFuncs_Polyhedral.convertIntTableToAffineTrafoLargeInteger(new int[][]{
				{	1,   0,   0,  0,  0},
				{	0,   1,   0,  0,  0},
				{   0,   0,   0,  0,  1}
		}));
		g.addEdge(cV[0], dV[1], TestFuncs_Polyhedral.convertIntTableToAffineTrafoLargeInteger(new int[][]{
				{	1,   0,   0,  0, -1},
				{	0,   1,   0,  0,  0},
				{   0,   0,   0,  0,  1}
		}));
		g.addEdge(cV[1], dV[0], TestFuncs_Polyhedral.convertIntTableToAffineTrafoLargeInteger(new int[][]{
				{	1,   0,   0,  0,  0},
				{	0,   1,   0,  0, -1},
				{   0,   0,   0,  0,  1}
		}));
		g.addEdge(cV[1], dV[1], TestFuncs_Polyhedral.convertIntTableToAffineTrafoLargeInteger(new int[][]{
				{	1,   0,   0,  0,  0},
				{	0,   1,   0,  0,  0},
				{   0,   0,   0,  0,  1}
		}));
			
		expEdges[0][1] = TestFuncs_Polyhedral.getAffineModuleRelationFromGeneratingMatrix(new int[][]{
				{  1, -1,  1, -1,  0,  0, -1 }				
		}, 2, 2, 3);
		expEdges[1][0] = TestFuncs_Polyhedral.getAffineModuleRelationFromGeneratingMatrix(new int[][]{
				{  1, -1,  1, -1,  0,  0,  1 }				
		}, 2, 2, 3);
		expEdges[0][0] = TestFuncs_Polyhedral.getAffineModuleRelationFromGeneratingMatrix(new int[][]{
				{  1, -1,  1, -1,  0,  0,  0 }				
		}, 2, 2, 3);
		expEdges[1][1] = TestFuncs_Polyhedral.getAffineModuleRelationFromGeneratingMatrix(new int[][]{
				{  1, -1,  1, -1,  0,  0,  0 }				
		}, 2, 2, 3);
		
		assertActualEdgesAreAsExpected();
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
		doSetup(2, 2, new int[]{2, 2}, new int[]{2, 2}, 2);

		g.addEdge(cV[0], dV[0], TestFuncs_Polyhedral.convertIntTableToAffineTrafoLargeInteger(new int[][]{
				{	1,   0,   0,  0,  0},
				{	0,   1,   0,  0,  0},
				{   0,   0,   0,  0,  1}
		}));
		g.addEdge(cV[0], dV[1], TestFuncs_Polyhedral.convertIntTableToAffineTrafoLargeInteger(new int[][]{
				{	1,   0,   0,  0, -7},
				{	0,   1,   0,  0,  0},
				{   0,   0,   0,  0,  1}
		}));
		g.addEdge(cV[1], dV[0], TestFuncs_Polyhedral.convertIntTableToAffineTrafoLargeInteger(new int[][]{
				{	1,   0,   0,  0,  0},
				{	0,   1,   0,  0, -7},
				{   0,   0,   0,  0,  1}
		}));
		g.addEdge(cV[1], dV[1], TestFuncs_Polyhedral.convertIntTableToAffineTrafoLargeInteger(new int[][]{
				{	1,   0,   0,  0,  0},
				{	0,   1,   0,  0,  0},
				{   0,   0,   0,  0,  1}
		}));

		expEdges[0][1] = TestFuncs_Polyhedral.getAffineModuleRelationFromGeneratingMatrix(new int[][]{
				{  1, -1,  1, -1,  0,  0, -7 }				
		}, 2, 2, 3);
		expEdges[1][0] = TestFuncs_Polyhedral.getAffineModuleRelationFromGeneratingMatrix(new int[][]{
				{  1, -1,  1, -1,  0,  0,  7 }				
		}, 2, 2, 3);
		expEdges[0][0] = TestFuncs_Polyhedral.getAffineModuleRelationFromGeneratingMatrix(new int[][]{
				{  1, -1,  1, -1,  0,  0,  0 }				
		}, 2, 2, 3);
		expEdges[1][1] = TestFuncs_Polyhedral.getAffineModuleRelationFromGeneratingMatrix(new int[][]{
				{  1, -1,  1, -1,  0,  0,  0 }				
		}, 2, 2, 3);
		
		assertActualEdgesAreAsExpected();		
	}
	
	/**
	 * Test Pugh: Transitive Closure of Infinite Graphs and its Applications
	 * - To show that some transitive closures of relations are infeasible
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
		doSetup(1, 1, new int[]{2}, new int[]{2}, 3);

		g.addEdge(cV[0], dV[0], TestFuncs_Polyhedral.convertIntTableToAffineTrafoLargeInteger(new int[][]{
				{	1,   0,   0,  0,  0,  0},
				{	0,   1,   0,  0,  0,  0},
				{   0,   0,   0,  0,  0,  1}
		}));
		g.addEdge(cV[0], dV[0], TestFuncs_Polyhedral.convertIntTableToAffineTrafoLargeInteger(new int[][]{
				{	1,   0,   0,  0,  0,  1},
				{	0,   1,   0,  0,  1,  0},
				{   0,   0,   0,  0,  0,  1}
		}));

		expEdges[0][0] = TestFuncs_Polyhedral.getAffineModuleRelationFromGeneratingMatrix(new int[][]{
		}, 2, 2, 4);
	
		assertActualEdgesAreAsExpected();		
	}
	
	/**
	 * Test from Lim & Lam (Communication-Free Parallelisation via Affine Transformations), but simplified 
	 * - To show that some sets of dependences lead to processor spaces which are not full-dimensional
	 * for ( i = 0 ; i <= n ; i++) {
	 * 	 for ( j = 0 ; j <= n ; j++) {
	 * 		A[i][j][1] = A[i][j][1] + ...
	 *   }
	 * }
	 * for ( i = 0 ; i <= n ; i++) {
	 * 	 for ( j = 0 ; j <= n ; j++) {
	 * 		A[i][1][j] = A[i][1][j] + ...
	 *   }
	 * }
	 * 
	 * Dependencies:
	 * - SpaceVector intersection
	 */
	public void testPartitionLimLam_1() {
		doSetup(2, 1, new int[]{2, 2}, new int[]{3}, 1);

		g.addEdge(cV[0], dV[0], TestFuncs_Polyhedral.convertIntTableToAffineTrafoLargeInteger(new int[][]{
				{	1,   0,   0,  0},
				{	0,   1,   0,  0},
				{	0,   0,   0,  1},
				{   0,   0,   0,  1}
		}));
		g.addEdge(cV[1], dV[0], TestFuncs_Polyhedral.convertIntTableToAffineTrafoLargeInteger(new int[][]{
				{	1,   0,   0,  0},
				{	0,   0,   0,  1},
				{	0,   1,   0,  0},
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
	 * Test Lim & Lam (Communication-Free Parallelisation via Affine Transformations) 
	 * - To show that some sets of dependences lead to processor spaces which are not full-dimensional
	 * for ( i = 0 ; i <= n ; i++) {
	 * 	 for ( j = 0 ; j <= n ; j++) {
	 * 		A[i][j][1] = A[i][j][1] + ...
	 *   }
	 * }
	 * for ( i = 0 ; i <= n ; i++) {
	 * 	 for ( j = 0 ; j <= n ; j++) {
	 * 		A[i][1][j] = A[i][1][j] + ...
	 *   }
	 * }
	 * for ( i = 0 ; i <= n ; i++) {
	 * 	 for ( j = 0 ; j <= n ; j++) {
	 * 		A[1][i][j] = A[1][i][j] + ...
	 *   }
	 * }
	 * 
	 * Dependencies:
	 * - SpaceVector intersection
	 */
	public void testPartitionLimLam() {
		doSetup(3, 1, new int[]{2, 2, 2}, new int[]{3}, 1);

		g.addEdge(cV[0], dV[0], TestFuncs_Polyhedral.convertIntTableToAffineTrafoLargeInteger(new int[][]{
				{	1,   0,   0,  0},
				{	0,   1,   0,  0},
				{	0,   0,   0,  1},
				{   0,   0,   0,  1}
		}));
		g.addEdge(cV[1], dV[0], TestFuncs_Polyhedral.convertIntTableToAffineTrafoLargeInteger(new int[][]{
				{	1,   0,   0,  0},
				{	0,   0,   0,  1},
				{	0,   1,   0,  0},
				{   0,   0,   0,  1}
		}));
		g.addEdge(cV[2], dV[0], TestFuncs_Polyhedral.convertIntTableToAffineTrafoLargeInteger(new int[][]{
				{	0,   0,   0,  1},
				{	1,   0,   0,  0},
				{	0,   1,   0,  0},
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
		expEdges[2][2] = TestFuncs_Polyhedral.getAffineModuleRelationFromGeneratingMatrix(new int[][]{
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
		
		expEdges[0][2] = TestFuncs_Polyhedral.getAffineModuleRelationFromGeneratingMatrix(new int[][]{
				{   0,   1,   1,  0,  0,  0  },
				{   1,   0,   0,  0,  0, -1  },
				{   0,   0,   0,  1,  0,  1  }
		}, 2, 2, 2);
		expEdges[2][0] = TestFuncs_Polyhedral.getAffineModuleRelationFromGeneratingMatrix(new int[][]{
				{   1,   0,   0,  1,  0,  0  },
				{   0,   1,   0,  0,  0, -1  },
				{   0,   0,   1,  0,  0,  1  }
		}, 2, 2, 2);
		
		expEdges[1][2] = TestFuncs_Polyhedral.getAffineModuleRelationFromGeneratingMatrix(new int[][]{
				{   0,   1,   0,  1,  0,  0  },
				{   1,   0,   0,  0,  0, -1  },
				{   0,   0,   1,  0,  0,  1  }
		}, 2, 2, 2);
		expEdges[2][1] = TestFuncs_Polyhedral.getAffineModuleRelationFromGeneratingMatrix(new int[][]{
				{   0,   1,   0,  1,  0,  0  },
				{   1,   0,   0,  0,  0, -1  },
				{   0,   0,   1,  0,  0,  1  }
		}, 2, 2, 2);
		
		assertActualEdgesAreAsExpected();		
	}
	
	/** Matrix Multiplication
	 * for ( i = 0 ; i <= n ; i++ ) {
	 * 	 for ( j = 0 ; j <= n ; j++ ) {
	 *     C[i][j] := 0;
	 *     for ( k = 0 ; k <= n ; k++ ) {
	 *       C[i][j] := C[i][j] + A[i][k]*B[k][j];
	 *     }
	 *   }
	 * }
	 */
	public void testPartition_MatrixMultiplication() {
		doSetup(2, 1, new int[]{2, 3}, new int[]{2}, 1); 
			
		g.addEdge(cV[0], dV[0], TestFuncs_Polyhedral.convertIntTableToAffineTrafoLargeInteger(new int[][]{
				{	1,   0,   0,  0},
				{	0,   1,   0,  0},
				{   0,   0,   0,  1}
		}));
		g.addEdge(cV[1], dV[0], TestFuncs_Polyhedral.convertIntTableToAffineTrafoLargeInteger(new int[][]{
				{	1,   0,   0,  0,  0},
				{	0,   1,   0,  0,  0},
				{   0,   0,   0,  0,  1}
		}));
			
		expEdges[0][1] = TestFuncs_Polyhedral.getAffineModuleRelationFromGeneratingMatrix(new int[][]{
				{  1,  0,  1,  0,  0,  0,  0 },				
				{  0,  1,  0,  1,  0,  0,  0 }				
		}, 2, 3, 2);
		expEdges[1][0] = TestFuncs_Polyhedral.getAffineModuleRelationFromGeneratingMatrix(new int[][]{
				{  1,  0,  0,  1,  0,  0,  0 },				
				{  0,  1,  0,  0,  1,  0,  0 }				
		}, 3, 2, 2);
		expEdges[0][0] = TestFuncs_Polyhedral.getAffineModuleRelationFromGeneratingMatrix(new int[][]{
				{  1,  0,  1,  0,  0,  0 },				
				{  0,  1,  0,  1,  0,  0 }				
		}, 2, 2, 2);
		expEdges[1][1] = TestFuncs_Polyhedral.getAffineModuleRelationFromGeneratingMatrix(new int[][]{
				{  1,  0,  0,  1,  0,  0,  0,  0 },				
				{  0,  1,  0,  0,  1,  0,  0,  0 }				
		}, 3, 3, 2);
		
		assertActualEdgesAreAsExpected();
	}

	/** Two Matrix Multiplications (E*(A*B) || ) -- Example of Sadayappan (18 dec 2007) // Slightly modified: added initialization for D
	 * E*(A*B) ||| C = A*B; D = E*C
	 * for ( i = 0 ; i <= n ; i++ ) {
	 * 	 for ( j = 0 ; j <= n ; j++ ) {
	 *     C[i][j] := 0;
	 *     for ( k = 0 ; k <= n ; k++ ) {
	 *       C[i][j] := C[i][j] + A[i][k]*B[k][j];
	 *     }
	 *   }
	 * }
	 * 
	 * for ( i = 0 ; i <= n ; i++ ) {
	 * 	 for ( j = 0 ; j <= n ; j++ ) {
	 *     D[i][j] := 0;
	 *     for ( k = 0 ; k <= n ; k++ ) {
	 *       D[i][j] := D[i][j] + E[i][k]*C[k][j];
	 *     }
	 *   }
	 * }
	 */
	public void testPartition_TwoMatrixMultiplications_Sadayappan() {
		doSetup(4, 2, new int[]{2, 3, 2, 3}, new int[]{2, 2}, 1); 
			
		g.addEdge(cV[0], dV[0], TestFuncs_Polyhedral.convertIntTableToAffineTrafoLargeInteger(new int[][]{
				{	1,   0,   0,  0},
				{	0,   1,   0,  0},
				{   0,   0,   0,  1}
		}));
		g.addEdge(cV[1], dV[0], TestFuncs_Polyhedral.convertIntTableToAffineTrafoLargeInteger(new int[][]{
				{	1,   0,   0,  0,  0},
				{	0,   1,   0,  0,  0},
				{   0,   0,   0,  0,  1}
		}));
		g.addEdge(cV[3], dV[0], TestFuncs_Polyhedral.convertIntTableToAffineTrafoLargeInteger(new int[][]{
				{	0,   0,   1,  0,  0},
				{	0,   1,   0,  0,  0},
				{   0,   0,   0,  0,  1}
		}));
		g.addEdge(cV[2], dV[1], TestFuncs_Polyhedral.convertIntTableToAffineTrafoLargeInteger(new int[][]{
				{	1,   0,   0,  0},
				{	0,   1,   0,  0},
				{   0,   0,   0,  1}
		}));
		g.addEdge(cV[3], dV[1], TestFuncs_Polyhedral.convertIntTableToAffineTrafoLargeInteger(new int[][]{
				{	1,   0,   0,  0,  0},
				{	0,   1,   0,  0,  0},
				{   0,   0,   0,  0,  1}
		}));
			
		expEdges[0][1] = TestFuncs_Polyhedral.getAffineModuleRelationFromGeneratingMatrix(new int[][]{
		}, 2, 3, 2);
		expEdges[1][0] = TestFuncs_Polyhedral.getAffineModuleRelationFromGeneratingMatrix(new int[][]{
		}, 3, 2, 2);
		expEdges[0][0] = TestFuncs_Polyhedral.getAffineModuleRelationFromGeneratingMatrix(new int[][]{
		}, 2, 2, 2);
		expEdges[1][1] = TestFuncs_Polyhedral.getAffineModuleRelationFromGeneratingMatrix(new int[][]{
		}, 3, 3, 2);
		
		assertActualEdgesAreAsExpected();
	}
	
	/** More matrix multiplications ((F*G)*(A*B))
	 * for ( i = 0 ; i <= n ; i++ ) {
	 * 	 for ( j = 0 ; j <= n ; j++ ) {
	 *     C[i][j] := 0;
	 *     for ( k = 0 ; k <= n ; k++ ) {
	 *       C[i][j] := C[i][j] + A[i][k]*B[k][j];
	 *     }
	 *   }
	 * }
	 * 
	 * for ( i = 0 ; i <= n ; i++ ) {
	 * 	 for ( j = 0 ; j <= n ; j++ ) {
	 *     E[i][j] := 0;
	 *     for ( k = 0 ; k <= n ; k++ ) {
	 *       E[i][j] := E[i][j] + F[i][k]*G[k][j];
	 *     }
	 *   }
	 * }
	 * 
	 * for ( i = 0 ; i <= n ; i++ ) {
	 * 	 for ( j = 0 ; j <= n ; j++ ) {
	 *     D[i][j] := 0;
	 *     for ( k = 0 ; k <= n ; k++ ) {
	 *       D[i][j] := D[i][j] + E[i][k]*C[k][j];
	 *     }
	 *   }
	 * }
	 */
	public void testPartition_MatrixMultiplications_B() {
		doSetup(6, 3, new int[]{2, 3, 2, 3, 2, 3}, new int[]{2, 2, 2}, 1); 
			
		g.addEdge(cV[0], dV[0], TestFuncs_Polyhedral.convertIntTableToAffineTrafoLargeInteger(new int[][]{
				{	1,   0,   0,  0},
				{	0,   1,   0,  0},
				{   0,   0,   0,  1}
		}));
		g.addEdge(cV[1], dV[0], TestFuncs_Polyhedral.convertIntTableToAffineTrafoLargeInteger(new int[][]{
				{	1,   0,   0,  0,  0},
				{	0,   1,   0,  0,  0},
				{   0,   0,   0,  0,  1}
		}));
		g.addEdge(cV[1], dV[1], TestFuncs_Polyhedral.convertIntTableToAffineTrafoLargeInteger(new int[][]{
				{	1,   0,   0,  0},
				{	0,   1,   0,  0},
				{   0,   0,   0,  1}
		}));
		g.addEdge(cV[2], dV[1], TestFuncs_Polyhedral.convertIntTableToAffineTrafoLargeInteger(new int[][]{
				{	1,   0,   0,  0,  0},
				{	0,   1,   0,  0,  0},
				{   0,   0,   0,  0,  1}
		}));
		g.addEdge(cV[5], dV[0], TestFuncs_Polyhedral.convertIntTableToAffineTrafoLargeInteger(new int[][]{
				{	0,   0,   1,  0,  0},
				{	0,   1,   0,  0,  0},
				{   0,   0,   0,  0,  1}
		}));
		g.addEdge(cV[5], dV[1], TestFuncs_Polyhedral.convertIntTableToAffineTrafoLargeInteger(new int[][]{
				{	1,   0,   0,  0,  0},
				{	0,   0,   1,  0,  0},
				{   0,   0,   0,  0,  1}
		}));
		g.addEdge(cV[4], dV[2], TestFuncs_Polyhedral.convertIntTableToAffineTrafoLargeInteger(new int[][]{
				{	1,   0,   0,  0},
				{	0,   1,   0,  0},
				{   0,   0,   0,  1}
		}));
		g.addEdge(cV[5], dV[2], TestFuncs_Polyhedral.convertIntTableToAffineTrafoLargeInteger(new int[][]{
				{	1,   0,   0,  0,  0},
				{	0,   1,   0,  0,  0},
				{   0,   0,   0,  0,  1}
		}));
			
		expEdges[0][1] = TestFuncs_Polyhedral.getAffineModuleRelationFromGeneratingMatrix(new int[][]{
		}, 2, 3, 2);
		expEdges[1][0] = TestFuncs_Polyhedral.getAffineModuleRelationFromGeneratingMatrix(new int[][]{
		}, 3, 2, 2);
		expEdges[0][0] = TestFuncs_Polyhedral.getAffineModuleRelationFromGeneratingMatrix(new int[][]{
		}, 2, 2, 2);
		expEdges[1][1] = TestFuncs_Polyhedral.getAffineModuleRelationFromGeneratingMatrix(new int[][]{
		}, 3, 3, 2);
		
		assertActualEdgesAreAsExpected();
	}
}
