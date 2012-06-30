package be.ugent.elis.sare;

import org.jscience.mathematics.numbers.Rational;
import org.jscience.mathematics.vectors.DenseMatrix;
import org.jscience.mathematics.vectors.Matrix;

import be.ugent.elis.AffineTransformation;
import be.ugent.elis.align.TestCaseExtended;
import be.ugent.elis.dfg.GDGEdge;
import be.ugent.elis.dfg.GDGVertex;
import be.ugent.elis.dfg.GDGraph;
import be.ugent.elis.feautrierscheduler.GDGFeautrierScheduler;
import be.ugent.elis.sare.domains.ParametrizedIntegerPolyhederDomain;

public class TestFeautrier extends TestCaseExtended  {
	public Matrix<Rational> convertIntTableToMatrixRational(int[][] i) {
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
	
	public ParametrizedIntegerPolyhederDomain<Rational> convertIntTableToPIPDRational(int[][] domain, int[][] context) {
		return new ParametrizedIntegerPolyhederDomain<Rational>( 
				convertIntTableToMatrixRational(domain), convertIntTableToMatrixRational(context), 
                Rational.ZERO, Rational.ONE);
	}
	
	public AffineTransformation<Rational> convertIntTableToAffineTrafoRational(int[][] i) {
		return new AffineTransformation<Rational>(convertIntTableToMatrixRational(i), Rational.ZERO, Rational.ONE);
	}
	
	/**
	 * Feautrier's multi-dim example (for example of multi-dim paper):
	 *  for i := 0 to n do begin
	 *    for j := 0 to i do begin
	 *      s := s + a(i, j);
	 *    end;
	 *  end;
	 *  
	 *  GDGraph: 1 vertex (0..n, 0..i), 2 edges
	 *  * Edge 1: Dep Trafo: (i, j - 1)
	 *            Dep Domain: (0..n, 1..i)
	 *  * Edge 2: Dep Trafo: (i - 1, i - 1)
	 *            Dep Domain: (1..n, 0)
	 */
/*	public void test() {
		GDGraph gdGraph = new GDGraph();
		GDGVertex gdVertex = gdGraph.addVertex();
		GDGEdge gdEdge1 = gdGraph.addEdge(gdVertex, gdVertex);
		GDGEdge gdEdge2 = gdGraph.addEdge(gdVertex, gdVertex);

		Matrix<Rational> trafoMatrix1 = DenseMatrix.valueOf(new Rational[][]{ // (i, j - 1)
				{Rational.valueOf(1, 1), Rational.valueOf(0, 1), Rational.valueOf(0, 1), Rational.valueOf(0, 1)},
				{Rational.valueOf(0, 1), Rational.valueOf(1, 1), Rational.valueOf(0, 1), Rational.valueOf(-1, 1)},
				{Rational.valueOf(0, 1), Rational.valueOf(0, 1), Rational.valueOf(1, 1), Rational.valueOf(0, 1)},
				{Rational.valueOf(0, 1), Rational.valueOf(0, 1), Rational.valueOf(0, 1), Rational.valueOf(1, 1)}
              });
		gdEdge1.dataDependence = new AffineTransformation<Rational>(trafoMatrix1, Rational.ZERO, Rational.ONE);
		
		Matrix<Rational> trafoMatrix2 = DenseMatrix.valueOf(new Rational[][]{ // (i - 1, i - 1)
				{Rational.valueOf(1, 1), Rational.valueOf(0, 1), Rational.valueOf(0, 1), Rational.valueOf(-1, 1)},
				{Rational.valueOf(1, 1), Rational.valueOf(0, 1), Rational.valueOf(0, 1), Rational.valueOf(-1, 1)},
				{Rational.valueOf(0, 1), Rational.valueOf(0, 1), Rational.valueOf(1, 1), Rational.valueOf(0, 1)},
				{Rational.valueOf(0, 1), Rational.valueOf(0, 1), Rational.valueOf(0, 1), Rational.valueOf(1, 1)}
              });
		gdEdge2.dataDependence = new AffineTransformation<Rational>(trafoMatrix2, Rational.ZERO, Rational.ONE);
		
		ParametrizedIntegerPolyhederDomain<Rational> pipd1
			= new ParametrizedIntegerPolyhederDomain<Rational>(2, 1, 4, 0, Rational.ZERO, Rational.ONE);
		Matrix<Rational> producedDomainMatrix1 = DenseMatrix.valueOf(new Rational[][]{ // (0..n, 1..i)
				{Rational.valueOf( 1, 1), Rational.valueOf( 0, 1), Rational.valueOf(0, 1), Rational.valueOf(0, 1)},
				{Rational.valueOf(-1, 1), Rational.valueOf( 0, 1), Rational.valueOf(1, 1), Rational.valueOf(0, 1)},
				{Rational.valueOf( 0, 1), Rational.valueOf( 1, 1), Rational.valueOf(0, 1), Rational.valueOf(1, 1)},
				{Rational.valueOf( 1, 1), Rational.valueOf(-1, 1), Rational.valueOf(0, 1), Rational.valueOf(0, 1)}
              });
		pipd1.domainConstraintMatrix = DenseMatrix.valueOf(producedDomainMatrix1);
		gdEdge1.producedDomain = pipd1;
		
		ParametrizedIntegerPolyhederDomain<Rational> pipd2
			= new ParametrizedIntegerPolyhederDomain<Rational>(2, 1, 4, 0, Rational.ZERO, Rational.ONE);
		Matrix<Rational> producedDomainMatrix2 = DenseMatrix.valueOf(new Rational[][]{ // (1..n, 0)
				{Rational.valueOf( 1, 1), Rational.valueOf( 0, 1), Rational.valueOf(0, 1), Rational.valueOf(1, 1)},
				{Rational.valueOf(-1, 1), Rational.valueOf( 0, 1), Rational.valueOf(1, 1), Rational.valueOf(0, 1)},
				{Rational.valueOf( 0, 1), Rational.valueOf( 1, 1), Rational.valueOf(0, 1), Rational.valueOf(0, 1)},
				{Rational.valueOf( 0, 1), Rational.valueOf(-1, 1), Rational.valueOf(0, 1), Rational.valueOf(0, 1)}
              });
		pipd2.domainConstraintMatrix = DenseMatrix.valueOf(producedDomainMatrix2);
		gdEdge2.producedDomain = pipd2;
		
		ParametrizedIntegerPolyhederDomain<Rational> pipdc
			= new ParametrizedIntegerPolyhederDomain<Rational>(2, 1, 4, 0, Rational.ZERO, Rational.ONE);
		Matrix<Rational> compDomainMatrix = DenseMatrix.valueOf(new Rational[][]{ // (0..n, 0..i)
				{Rational.valueOf( 1, 1), Rational.valueOf( 0, 1), Rational.valueOf(0, 1), Rational.valueOf(0, 1)},
				{Rational.valueOf(-1, 1), Rational.valueOf( 0, 1), Rational.valueOf(1, 1), Rational.valueOf(0, 1)},
				{Rational.valueOf( 0, 1), Rational.valueOf( 1, 1), Rational.valueOf(0, 1), Rational.valueOf(0, 1)},
				{Rational.valueOf( 1, 1), Rational.valueOf(-1, 1), Rational.valueOf(0, 1), Rational.valueOf(0, 1)}
              });
		pipdc.domainConstraintMatrix = DenseMatrix.valueOf(compDomainMatrix);
		gdVertex.computationDomain = pipdc;
		
		GDGFeautrierScheduler<Rational> scheduler 
		= new GDGFeautrierScheduler<Rational>(gdGraph, Rational.ZERO, Rational.ONE);
	
		gdGraph.<Rational>codeGeneration(Rational.ZERO, Rational.ONE);
	}*/
	
	/**
	 *  1 vertex with 1 simple parameter-dependent affine transformation.
	 *  
	 *  for i := 0 to n do begin
	 *    a(i) := a(i - m);    
	 *  end;
	 *  
	 *  GDGraph:
	 *  * V1 (0..n)
	 *  * E1 (Trafo: (i - m), Domain: (m..n))
	 *//*
	public void test2() {
		GDGraph gdGraph = new GDGraph();
		GDGVertex gdVertex = gdGraph.addVertex();
		GDGEdge gdEdge1 = gdGraph.addEdge(gdVertex, gdVertex);

		gdEdge1.dataDependence = convertIntTableToAffineTrafoRational(new int[][]{ // (i - m) 
				{1, -1, 0, 0},
				{0,  1, 0, 0},
				{0,  0, 1, 0},
				{0,  0, 0, 1}
        });
		
		gdEdge1.producedDomain = convertIntTableToPIPDRational(1, 2, new int[][]{ // (m..n)
				{ 1,-1, 0, 0},
				{-1, 0, 1, 0}
		});

		gdVertex.computationDomain = convertIntTableToPIPDRational(1, 2, new int[][]{ // (0..n)
				{ 1, 0, 0, 0},
				{-1, 0, 1, 0}
		});
		
		GDGFeautrierScheduler<Rational> scheduler 
			= new GDGFeautrierScheduler<Rational>(gdGraph, Rational.ZERO, Rational.ONE);
	
		gdGraph.<Rational>codeGeneration(Rational.ZERO, Rational.ONE);
	}*/
	
	/**
	 *  1 vertex with 1 simple parameter-dependent affine transformation.
	 *  Same as test2 but no upper bound.
	 *  
	 *  for i := 0 to +infinity do begin
	 *    a(i) := a(i - m);    
	 *  end;
	 *  
	 *  GDGraph:
	 *  * V1 (0..+inf)
	 *  * E1 (Trafo: (i - m), Domain: (m..+inf))
	 *//*
	public void test2_c() { 
		GDGraph gdGraph = new GDGraph();
		GDGVertex gdVertex = gdGraph.addVertex();
		GDGEdge gdEdge1 = gdGraph.addEdge(gdVertex, gdVertex);

		gdEdge1.dataDependence = convertIntTableToAffineTrafoRational(new int[][]{ // (i - m) 
				{1, -1, 0},
				{0,  1, 0},
				{0,  0, 1}
        });
		
		gdEdge1.producedDomain = convertIntTableToPIPDRational(1, 1, new int[][]{ // (m..+inf)
				{ 1,-1, 0}
		});

		gdVertex.computationDomain = convertIntTableToPIPDRational(1, 1, new int[][]{ // (0..+inf)
				{ 1, 0, 0}
		});
		
		gdGraph.calculateFeautrierSchedule();
	
		gdGraph.<Rational>codeGeneration(Rational.ZERO, Rational.ONE);
	}*/
	
	/** --> Works with default FFA
	 *  1 vertex with 1 simple parameter-dependent affine transformation.
	 *  Modified version of test2_c: Additional constraint constrains m.
	 *  
	 *  for i := 0 to +infinity do begin
	 *    a(i) := a(i - m);    
	 *  end;
	 *  
	 *  GDGraph:
	 *  * V1 (0..+inf, m >= 1)
	 *  * E1 (Trafo: (i - m), Domain: (m..+inf, m >= 1))
	 *//*
	public void test2_d() { 
		GDGraph gdGraph = new GDGraph();
		GDGVertex gdVertex = gdGraph.addVertex();
		GDGEdge gdEdge1 = gdGraph.addEdge(gdVertex, gdVertex);

		gdEdge1.dataDependence = convertIntTableToAffineTrafoRational(new int[][]{ // (i - m) 
				{ 1, -1,  0},
				{ 0,  1,  0},
				{ 0,  0,  1}
        });
		
		gdEdge1.producedDomain = convertIntTableToPIPDRational(1, 1, new int[][]{ // (m..+inf)
				{ 1, -1,  0},
				{ 0,  1, -1}
		});

		gdVertex.computationDomain = convertIntTableToPIPDRational(1, 1, new int[][]{ // (0..+inf)
				{ 1,  0,  0},
				{ 0,  1, -1}
		});
		
		gdGraph.calculateFeautrierSchedule();
	
		gdGraph.<Rational>codeGeneration(Rational.ZERO, Rational.ONE);
	}*/
	
	/** 
	 *  
	 *  
	 *  for i := 0 to n do begin
	 *    for j := 0 to n do begin
	 *      a(i, j) := a(i, j - 1) + a(i - 1, j + m);
	 *    end;      
	 *  end;
	 *  
	 *  GDGraph:
	 *  * V1 (1..n, 1..(n - m))
	 *  * E1 (Trafo: (i, j - 1), Domain: (0..n, 1..n))
	 *  * E2 (Trafo: (i - 1, j + m), Domain: (1..n, 0..(n - m)))
	 *//*
	public void test4() { 
		GDGraph gdGraph = new GDGraph();
		GDGVertex gdVertex = gdGraph.addVertex();
		GDGEdge gdEdge1 = gdGraph.addEdge(gdVertex, gdVertex);
		GDGEdge gdEdge2 = gdGraph.addEdge(gdVertex, gdVertex);

		gdEdge1.dataDependence = convertIntTableToAffineTrafoRational(new int[][]{ // (i, j - 1) 
				{ 1,  0,  0,  0,  0},
				{ 0,  1,  0,  0, -1},
				{ 0,  0,  1,  0,  0},
				{ 0,  0,  0,  1,  0},
				{ 0,  0,  0,  0,  1}
        });
		gdEdge1.producedDomain = convertIntTableToPIPDRational(2, 2, new int[][]{ // (0..n, 1..n)
				{ 1,  0,  0,  0,  0},
				{-1,  0,  0,  1,  0},
				{ 0,  1,  0,  0, -1},
				{ 0, -1,  0,  1,  0},
				{ 0,  0,  1,  0,  0},
				{ 0,  0,  0,  1,  0}
		});

		gdEdge2.dataDependence = convertIntTableToAffineTrafoRational(new int[][]{ // (i - 1, j + m) 
				{ 1,  0,  0,  0, -1},
				{ 0,  1,  1,  0,  0},
				{ 0,  0,  1,  0,  0},
				{ 0,  0,  0,  1,  0},
				{ 0,  0,  0,  0,  1}
        });
		gdEdge2.producedDomain = convertIntTableToPIPDRational(2, 2, new int[][]{ // (1..n, 0..(n - m))
				{ 1,  0,  0,  0, -1},
				{-1,  0,  0,  1,  0},
				{ 0,  1,  0,  0,  0},
				{ 0, -1, -1,  1,  0},
				{ 0,  0,  1,  0,  0},
				{ 0,  0,  0,  1,  0}
		});

		gdVertex.computationDomain = convertIntTableToPIPDRational(2, 2, new int[][]{ // (1..n, 1..(n - m))
				{ 1,  0,  0,  0, -1},
				{-1,  0,  0,  1,  0},
				{ 0,  1,  0,  0, -1},
				{ 0, -1, -1,  1,  0},
				{ 0,  0,  1,  0,  0},
				{ 0,  0,  0,  1,  0}
		});
		
		gdGraph.calculateFeautrierSchedule(true);
	
		gdGraph.<Rational>codeGeneration(Rational.ZERO, Rational.ONE);
	}*/

	/** 
	 *  Similar to test 4.  But: Simplified: No upper bounds for computation domain.
	 *  
	 *  for i := 0 to +inf do begin
	 *    for j := 0 to +inf do begin
	 *      a(i, j) := a(i, j - 1) + a(i - 1, j + m);
	 *    end;      
	 *  end;
	 *  
	 *  GDGraph:
	 *  * V1 (1..+inf, 1..+inf)
	 *  * E1 (Trafo: (i, j - 1), Domain: (0..+inf, 1..+inf))
	 *  * E2 (Trafo: (i - 1, j + m), Domain: (1..+inf, 0..+inf))
	 */
	public void test4_b() {/*
		GDGraph gdGraph = new GDGraph();
		GDGVertex gdVertex = gdGraph.addVertex();
		GDGEdge gdEdge1 = gdGraph.addEdge(gdVertex, gdVertex);
		GDGEdge gdEdge2 = gdGraph.addEdge(gdVertex, gdVertex);

		gdEdge1.dataDependence = convertIntTableToAffineTrafoRational(new int[][]{ // (i, j - 1) 
				{ 1,  0,  0,  0},
				{ 0,  1,  0, -1},
				{ 0,  0,  1,  0},
				{ 0,  0,  0,  1}
        });
		gdEdge1.producedDomain = convertIntTableToPIPDRational(2, 1, new int[][]{ // (0..+inf, 1..+inf)
				{ 1,  0,  0,  0},
				{ 0,  1,  0, -1},
				{ 0,  0,  1,  0}
		});

		gdEdge2.dataDependence = convertIntTableToAffineTrafoRational(new int[][]{ // (i - 1, j + m) 
				{ 1,  0,  0, -1},
				{ 0,  1,  1,  0},
				{ 0,  0,  1,  0},
				{ 0,  0,  0,  1}
        });
		gdEdge2.producedDomain = convertIntTableToPIPDRational(2, 1, new int[][]{ // (1..+inf, 0..+inf)
				{ 1,  0,  0, -1},
				{ 0,  1,  0,  0},
				{ 0,  0,  1,  0}
		});

		gdVertex.computationDomain = convertIntTableToPIPDRational(2, 1, new int[][]{ // (1..+inf, 1..+inf)
				{ 1,  0,  0, -1},
				{ 0,  1,  0, -1},
				{ 0,  0,  1,  0}
		});
		
		gdGraph.calculateFeautrierSchedule(false);
	
		gdGraph.<Rational>codeGeneration(Rational.ZERO, Rational.ONE);
	*/}

	/** 
	 *  Similar to test 4_b.  But: Parametric scheduler used.
	 *  
	 *  for i := 0 to +inf do begin
	 *    for j := 0 to +inf do begin
	 *      a(i, j) := a(i, j - 1) + a(i - 1, j + m);
	 *    end;      
	 *  end;
	 *  
	 *  GDGraph:
	 *  * V1 (1..+inf, 1..+inf)
	 *  * E1 (Trafo: (i, j - 1), Domain: (0..+inf, 1..+inf))
	 *  * E2 (Trafo: (i - 1, j + m), Domain: (1..+inf, 0..+inf))
	 */
	public void test4_b_parametric() { // Schedule found: (m + 1)i + j + (-m - 2)
		/* RelTrafo1: (i, j) - (i, j - 1) = (0, 1)
		 * RelTrafo2: (i, j) - (i - 1, j + m) = (1, -m)
		 * (m + 1) - m + (-m - 2) = -m - 1
		 *//*
		GDGraph gdGraph = new GDGraph();
		GDGVertex gdVertex = gdGraph.addVertex();
		GDGEdge gdEdge1 = gdGraph.addEdge(gdVertex, gdVertex);
		GDGEdge gdEdge2 = gdGraph.addEdge(gdVertex, gdVertex);

		gdEdge1.dataDependence = convertIntTableToAffineTrafoRational(new int[][]{ // (i, j - 1) 
				{  1,  0,  0,  0},
				{  0,  1,  0, -1},
				{  0,  0,  0,  1}
        });
		gdEdge1.producedDomain = convertIntTableToPIPDRational(new int[][]{ // (0..+inf, 1..+inf)
				{  1,  0,  0,   0},
				{  0,  1,  0,  -1}
		}, new int[][]{
				{  1,  0}
		});

		gdEdge2.dataDependence = convertIntTableToAffineTrafoRational(new int[][]{ // (i - 1, j + m) 
				{ 1,  0,  0, -1},
				{ 0,  1,  1,  0},
				{ 0,  0,  0,  1}
        });		
		gdEdge2.producedDomain = convertIntTableToPIPDRational(new int[][]{ // (1..+inf, 0..+inf)
				{  1,  0,  0, -1},
				{  0,  1,  0,  0}
		}, new int[][]{
				{  1,  0}
		});

		gdVertex.computationDomain = convertIntTableToPIPDRational(new int[][]{ // (1..+inf, 1..+inf)
				{ 1,  0,  0, -1},
				{ 0,  1,  0, -1}
		}, new int[][]{
				{  1,  0}
		});
		
		gdGraph.calculateFeautrierSchedule(true, true);
	
		gdGraph.<Rational>codeGeneration(Rational.ZERO, Rational.ONE);
	*/}

	/** 
	 *  Similar to test 4_b.  But: Parameter-free (m = 7).
	 *  
	 *  for i := 0 to +inf do begin
	 *    for j := 0 to +inf do begin
	 *      a(i, j) := a(i, j - 1) + a(i - 1, j + 7);
	 *    end;      
	 *  end;
	 *  
	 *  GDGraph:
	 *  * V1 (1..+inf, 1..+inf)
	 *  * E1 (Trafo: (i, j - 1), Domain: (0..+inf, 1..+inf))
	 *  * E2 (Trafo: (i - 1, j + 7), Domain: (1..+inf, 0..+inf))
	 */
	public void test4_c() {/*
		GDGraph gdGraph = new GDGraph();
		GDGVertex gdVertex = gdGraph.addVertex();
		GDGEdge gdEdge1 = gdGraph.addEdge(gdVertex, gdVertex);
		GDGEdge gdEdge2 = gdGraph.addEdge(gdVertex, gdVertex);

		gdEdge1.dataDependence = convertIntTableToAffineTrafoRational(new int[][]{ // (i, j - 1) 
				{ 1,  0,  0},
				{ 0,  1, -1},
				{ 0,  0,  1}
        });
		gdEdge1.producedDomain = convertIntTableToPIPDRational(2, 0, new int[][]{ // (0..+inf, 1..+inf)
				{ 1,  0,  0},
				{ 0,  1, -1}
		});

		gdEdge2.dataDependence = convertIntTableToAffineTrafoRational(new int[][]{ // (i - 1, j + 7) 
				{ 1,  0, -1},
				{ 0,  1,  7},
				{ 0,  0,  1}
        });
		gdEdge2.producedDomain = convertIntTableToPIPDRational(2, 0, new int[][]{ // (1..+inf, 0..+inf)
				{ 1,  0, -1},
				{ 0,  1,  0}
		});

		gdVertex.computationDomain = convertIntTableToPIPDRational(2, 0, new int[][]{ // (1..+inf, 1..+inf)
				{ 1,  0, -1},
				{ 0,  1, -1}
		});
		
		gdGraph.calculateFeautrierSchedule(false);
	
		gdGraph.<Rational>codeGeneration(Rational.ZERO, Rational.ONE);
	*/}

	/**
	 *  1 vertex with 1 simple parameter-dependent affine transformation.
	 *  Same as test2, parameter > 0
	 *  
	 *  for i := 0 to n do begin
	 *    a(i) := a(i - m);    
	 *  end;
	 *  
	 *  GDGraph:
	 *  * V1 (0..n)
	 *  * E1 (Trafo: (i - m), Domain: (m..n))
	 *//*
	public void test2_() {
		GDGraph gdGraph = new GDGraph();
		GDGVertex gdVertex = gdGraph.addVertex();
		GDGEdge gdEdge1 = gdGraph.addEdge(gdVertex, gdVertex);

		gdEdge1.dataDependence = convertIntTableToAffineTrafoRational(new int[][]{ // (i - m) 
				{1, -1, 0, 0},
				{0,  1, 0, 0},
				{0,  0, 1, 0},
				{0,  0, 0, 1}
        });
		
		gdEdge1.producedDomain = convertIntTableToPIPDRational(1, 2, new int[][]{ // (m..n)
				{ 1,-1, 0,  0},
				{-1, 0, 1,  0},
				{ 0, 0, 1, -1}
		});

		gdVertex.computationDomain = convertIntTableToPIPDRational(1, 2, new int[][]{ // (0..n)
				{ 1, 0, 0,  0},
				{-1, 0, 1,  0},
				{ 0, 0, 1, -1}
		});
		
		GDGFeautrierScheduler<Rational> scheduler 
			= new GDGFeautrierScheduler<Rational>(gdGraph, Rational.ZERO, Rational.ONE);
	
		gdGraph.<Rational>codeGeneration(Rational.ZERO, Rational.ONE);
	}*/
	
	/**
	 *  Bifurcation system
	 *  
	 *  for i := 0 to m do begin
	 *    for j := 0 to i do begin
	 *      for k := 1 to j - 1 do begin
	 *        a(i, j) := max(a(i, j), a(i, k) + a(i - k, j - k));
	 *      end;
	 *    end;      
	 *  end;
	 *  
	 *  GDGraph:
	 *  * V1 (2..m, 2..i, 1..j - 1)
	 *  { y | (y : D) ^ (F(y) : D) }
	 *  * E1: a(i, j) 
	 *    - Trafo: (i, j, k - 1)
	 *    - { ((i, j, k) : D(V1)) ^ ((i, j, k - 1) : D(V1)) }
	 *      = { ((i : 0..m) ^ (j : 0..i) ^ (k : 1..j - 1))  ^  ((i : 0..m) ^ (j : 0..i) ^ (k - 1 : 1..j - 1)) }
	 *      = { (i : 0..m) ^ (j : 0..i) ^  ((k : 1..j - 1))  ^  ((k - 1 : 1..j - 1)) }
	 *      = { (i : 0..m) ^ (j : 0..i) ^  (k : 2..j - 1) }
	 *      = { (i : 3..m) ^ (j : 3..i) ^  (k : 2..j - 1) }
	 *      --> Domain: (3..m, 3..i, 2..j - 1))
	 *  * E2: a(i, k) 
	 *    - Trafo: (i, k, k - 1)
	 *    - { ((i, j, k) : D(V1)) ^ ((i, k, k - 1) : D(V1)) }
	 *      = { ((i : 0..m) ^ (j : 0..i) ^ (k : 1..j - 1))  ^  ((i : 0..m) ^ (k : 0..i) ^ (k - 1 : 1..j - 1)) }
	 *      = { (i : 0..m) ^ ((j : 0..i) ^ (k : 1..j - 1))  ^  ((k : 0..i) ^ (k - 1 : 1..j - 1)) }
	 *      = { (i : 0..m) ^ ((j : 0..i) ^ (k : 2..j - 1))  ^  (k : 0..i) }
	 *      = { (i : 0..m) ^ ((j : 0..i) ^ (k : 2..j - 1))  ^  (k : 2..i) }
	 *      = { (i : 0..m) ^ (j : 0..i) ^ (k : 2..min(i, j - 1) }
	 *      = { (i : 0..m) ^ (j : 0..i) ^ (k : 2..j - 1 }
	 *      = { (i : 0..m) ^ (j : 3..i) ^ (k : 2..j - 1 }
	 *      = { (i : 3..m) ^ (j : 3..i) ^ (k : 2..j - 1 }
	 *      --> Domain: (3..m, 3..i, 2..j - 1))
	 *  * E3: a(i - k, j - k)
	 *    - Trafo: (i - k, j - k, j - k - 1)
	 *    - { ((i, j, k) : D(V1)) ^ ((i, k, k - 1) : D(V1)) }
	 *      = { ((i : 0..m) ^ (j : 0..i) ^ (k : 1..j - 1))  ^  ((i - k : 0..m) ^ (j - k : 0..i) ^ (j - k - 1 : 1..j - 1)) }
	 *      
	 *      = { ((i : 0..m) ^ (j : 0..i) ^ (k : 1..j - 1))  ^  ((k : i - m..i) ^ (j - k : 0..i) ^ (j - k - 1 : 1..j - 1)) }
	 *      = { (i : 0..m) ^ (j : 0..i) ^ (k : max(1, i - m)..min(i, j - 1))  ^  ((j - k : 0..i) ^ (j - k - 1 : 1..j - 1)) }
	 *      = { (i : 0..m) ^ (j : 0..i) ^ (k : max(1, i - m)..min(i, j - 1))  ^  ((k : j - i..j) ^ (j - k - 1 : 1..j - 1)) }
	 *      = { (i : 0..m) ^ (j : 0..i) ^ (k : max(1, i - m)..min(i, j - 1))  ^  ((k : j - i..j) ^ (k - j + 1 : 1 - j..-1)) }
	 *      = { (i : 0..m) ^ (j : 0..i) ^ (k : max(1, i - m)..min(i, j - 1))  ^  ((k : j - i..j) ^ (k : 0..j - 2)) }
	 *      = { (i : 0..m) ^ (j : 0..i) ^ (k : max(1, i - m, j - i, 0)..min(i, j - 1, j, j - 2)) }
	 *      = { (i : 0..m) ^ (j : 0..i) ^ (k : max(1, i - m, j - i)..min(i, j - 2)) }
	 *      = { (i : 0..m) ^ (j : 0..i) ^ (k : max(1, i - m)..j - 2) }
	 *      = { (i : 0..m) ^ (j : 0..i) ^ (k : 1..j - 2) }
	 *      = { (i : 3..m) ^ (j : 3..i) ^ (k : 1..j - 2) }
	 *      --> Domain: (3..m, 3..i, 1..j - 2))
	 *  * Recap
	 *    * V1 (0..m, 0..i, 1..j - 1)
	 *    * E1: a(i, j) 
	 *      - Trafo: (i, j, k - 1)
	 *      - Domain: (3..m, 3..i, 2..j - 1))
	 *    * E2: a(i, k) 
	 *      - Trafo: (i, k, k - 1)
	 *      - Domain: (3..m, 3..i, 2..j - 1))
	 *    * E3: a(i - k, j - k)
	 *      - Trafo: (i - k, j - k, j - k - 1)
	 *      - Domain: (3..m, 3..i, 1..j - 2))
	 */
	public void test3() {
		GDGraph gdGraph = new GDGraph();
		GDGVertex gdVertex = gdGraph.addVertex();
		GDGEdge gdEdge1 = gdGraph.addEdge(gdVertex, gdVertex);
		GDGEdge gdEdge2 = gdGraph.addEdge(gdVertex, gdVertex);
		GDGEdge gdEdge3 = gdGraph.addEdge(gdVertex, gdVertex);

		// Dependencies - Trafos
		gdEdge1.dataDependence = convertIntTableToAffineTrafoRational(new int[][]{ // (i, j, k - 1) 
				{ 1,  0,  0,  0,  0},
				{ 0,  1,  0,  0,  0},
				{ 0,  0,  1,  0, -1},
				{ 0,  0,  0,  0,  1}
        });
		gdEdge2.dataDependence = convertIntTableToAffineTrafoRational(new int[][]{ // (i, k, k - 1)
				{ 1,  0,  0,  0,  0},
	            { 0,  0,  1,  0,  0},
	            { 0,  0,  1,  0, -1},
				{ 0,  0,  0,  0,  1}
		});
		gdEdge3.dataDependence = convertIntTableToAffineTrafoRational(new int[][]{ // (i - k, j - k, j - k - 1)
				{ 1,  0, -1,  0,  0},
				{ 0,  1, -1,  0,  0},
				{ 0,  1, -1,  0, -1},
				{ 0,  0,  0,  0,  1}
		});
		
		// Dependencies - Produced domains
		gdEdge1.producedDomain = convertIntTableToPIPDRational(new int[][]{ // (3..m, 3..i, 2..j - 1)
				{ 1,  0,  0,  0, -3},
				{-1,  0,  0,  1,  0},
				{ 0,  1,  0,  0, -3},
				{ 1, -1,  0,  0,  0},
				{ 0,  0,  1,  0, -2},
				{ 0,  1, -1,  0, -1}
		}, new int[][]{
				{ 1,  0}
		});
		gdEdge2.producedDomain = convertIntTableToPIPDRational(new int[][]{ // (3..m, 3..i, 2..j - 1)
				{ 1,  0,  0,  0, -3},
				{-1,  0,  0,  1,  0},
				{ 0,  1,  0,  0, -3},
				{ 1, -1,  0,  0,  0},
				{ 0,  0,  1,  0, -2},
				{ 0,  1, -1,  0, -1}
		}, new int[][]{
				{ 1,  0}	
		});
		gdEdge3.producedDomain = convertIntTableToPIPDRational(new int[][]{ // (3..m, 3..i, 1..j - 2)
				{ 1,  0,  0,  0, -3},
				{-1,  0,  0,  1,  0},
				{ 0,  1,  0,  0, -3},
				{ 1, -1,  0,  0,  0},
				{ 0,  0,  1,  0, -1},
				{ 0,  1, -1,  0, -2}
		}, new int[][]{
				{ 1,  0}
		});
		
		// Computation domain
		gdVertex.computationDomain = convertIntTableToPIPDRational(new int[][]{ // (0..m, 0..i, 1..j - 1)
				{ 1,  0,  0,  0,  0},
				{-1,  0,  0,  1,  0},
				{ 0,  1,  0,  0,  0},
				{ 1, -1,  0,  0,  0},
				{ 0,  0,  1,  0,  1},
				{ 0,  1, -1,  0, -1}
		}, new int[][]{
				{ 1,  0}	
		});
		
		gdGraph.calculateFeautrierSchedule(false, false);
	
		gdGraph.<Rational>codeGeneration(Rational.ZERO, Rational.ONE);
	}
}
