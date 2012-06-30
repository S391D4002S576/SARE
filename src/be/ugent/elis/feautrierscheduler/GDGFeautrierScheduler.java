package be.ugent.elis.feautrierscheduler;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.File;
import java.util.Vector;

import org.jscience.mathematics.numbers.Rational;
import org.jscience.mathematics.structures.Field_;
import org.jscience.mathematics.vectors.DenseMatrix;
import org.jscience.mathematics.vectors.Matrix;

import be.ugent.elis.AffineTransformation;
import be.ugent.elis.debugging.Reportable;
import be.ugent.elis.debugging.ReportableHtml;
import be.ugent.elis.debugging.ReporterBasic;
import be.ugent.elis.debugging.ReporterBreak;
import be.ugent.elis.debugging.ReporterParagraph;
import be.ugent.elis.debugging.ReporterTable;
import be.ugent.elis.dfg.GDGEdge;
import be.ugent.elis.dfg.GDGVertex;
import be.ugent.elis.dfg.GDGraph;
import be.ugent.elis.piplibnative.PipProblem;
import be.ugent.elis.piplibnative.PipQuast;
import be.ugent.elis.sare.PolyhedralPartition;
import be.ugent.elis.sare.PolyhedralPartitionAffineTransformation;
import be.ugent.elis.sare.domains.ParametrizedIntegerPolyhederDomain;

// XXX: The (parameterIndependentMode && !parameterContextSensitive)-method is now basically dead, is it not?

public class GDGFeautrierScheduler<F extends Field_<F>> {
	final static int verbosity = 4;
	
	F zero = null;
	F one = null;
	
	Vector<GDGFVertex> vertices = new Vector<GDGFVertex>();
	Vector<GDGFEdge> edges = new Vector<GDGFEdge>();
	
	GDGraph gdGraph = null;
	
	PipProblem pipProblem = null;

	PolyhedralPartitionSolution scheduleSolution = null;
	
	boolean parameterDependentMode = false;
	boolean parameterContextSensitive = false;
	int globalParameterCount = -1;
	int contextFarkamCount = 1; // XXX: We should set this to (constraints(parameter-context) + 1)
	
	Matrix<F> parameterContextDCM = null; // XXX: We had better have this one ready..
	
	PrintStream debugOutputStream = null;
	ReporterBasic reporterBasic = null;
	
	private void dbg(int requiredVerbosity, String s, Object ... args) {
		if (verbosity >= requiredVerbosity) {
			s = String.format(s, args).replace("\n", "<br>") + "<br>";
			reporterBasic.addParagraph(s, args);
			debugOutputStream.println(s);
			debugOutputStream.flush();
		}
	}
	
	private void dbg(int requiredVerbosity, Reportable ... reportables) {
		if (verbosity >= requiredVerbosity) {
			for (int q = 0; q < reportables.length; q++) {
				reporterBasic.add(reportables[q]);
				if (reportables[q] instanceof ReportableHtml) {
					debugOutputStream.println(((ReportableHtml) reportables[q]).toHtml());
					debugOutputStream.flush();
				}
			}
		}
	}
	
	public int getSkewSymmetricEquationCount() {
		return (globalParameterCount * (globalParameterCount + 1)) / 2;
	}
	
	class GDGFEdge {
		GDGEdge gdgEdge;
		GDGFEdge parentGDGFEdge;
		GDGFVertex fromVertex, toVertex;
		
		Matrix<F> consumedDCM, producedDCM;
		ParametrizedIntegerPolyhederDomain<F> consumedDomain, producedDomain;

		// Unknown offsets
		int dependenceFarkamOffset = -1;
		int edgeDelay = -1;
		int baseConversionDependenceFarkamUnkOffset = -1;
		int parameterFarkamUnkOffset = -1;
		
		// Eq offsets
		int edgeEqOffset = -1;
		int skewSymmetricEqOffset = -1;

		// Ineq offsets
		int parameterFarkamIneqOffset = -1;
		int baseConversionDependenceFarkamIneqOffset = -1;
		int dependenceFarkamIneqOffset = -1;
		int edgeDelayIneqOffset = -1;
		
		PolyhedralPartitionAffineTransformation<Rational> dependenceSolution;
		
		int index = -1;
		
		/* !parameterDependentMode */
		
		/* parameterDependentMode */
		public int getSkewSymmetricEquationOffset(int r, int c) {
			assert parameterDependentMode;
			
			if (r >= c) {
				// "Number of equations upto including ((r - 1), (r - 1))" = (r(r + 1))/2
				return skewSymmetricEqOffset + ((r*(r + 1))/2 + c);
			} else {
		        return getSkewSymmetricEquationOffset(c, r);
			}				 
		}
		
		public int getDelayEquationIndex(int unknown, int parameter) {
			assert parameterDependentMode;

			return edgeEqOffset + unknown*(globalParameterCount + 1) + parameter;
		}
		
		public int getDelayEquationIndex(int unknown) {
			assert !parameterDependentMode;

			return edgeEqOffset + unknown;
		}
		
		/* parameterDependentMode && !parameterContextSensitive */
		public int getBaseConversionDependenceFarkamIx(int dependenceFarkam, int parameter) {
			assert parameterDependentMode && !parameterContextSensitive;
			
			return dependenceFarkam*(globalParameterCount + 1) + parameter;
		}
		
		public int getBaseConversionDependenceFarkamUnkIx(int dependenceFarkam, int parameter) {
			assert parameterDependentMode && !parameterContextSensitive;
			
			return baseConversionDependenceFarkamUnkOffset 
			       + getBaseConversionDependenceFarkamIx(dependenceFarkam, parameter);
		}
		
		public int getBaseConversionDependenceFarkamCount() {
			assert parameterDependentMode && !parameterContextSensitive;
			
			return getDependenceFarkamCount() * (globalParameterCount + 1);
		}
		
		public int getBaseConversionDependenceFarkamIneqOffset(int dependenceFarkam, int parameter) {
			assert parameterDependentMode && !parameterContextSensitive;
			
			return baseConversionDependenceFarkamIneqOffset
		           + getBaseConversionDependenceFarkamIx(dependenceFarkam, parameter);
		}
		/**/
		
		/* parameterDependentMode && parameterContextSensitive */
		public int getParameterFarkamIx(int dependenceFarkam, int contextFarkam) {
			assert parameterDependentMode && parameterContextSensitive;
			
			return dependenceFarkam*contextFarkamCount + contextFarkam;
		}
		
		public int getParameterFarkamIneqOffset(int dependenceFarkam, int contextFarkam) {
			assert parameterDependentMode && parameterContextSensitive;
			
			return parameterFarkamIneqOffset + getParameterFarkamIx(dependenceFarkam, contextFarkam);
		}
		
		public int getParameterFarkamUnkIx(int dependenceFarkam, int contextFarkam) {
			assert parameterDependentMode && parameterContextSensitive;
			
			return parameterFarkamUnkOffset + getParameterFarkamIx(dependenceFarkam, contextFarkam);
		}
		
		public int getParameterFarkamCount() {
			assert parameterDependentMode && parameterContextSensitive;
			
			return contextFarkamCount * getDependenceFarkamCount();
		}
		/**/
		
		
		public void addDelayEquations() {
			if (!parameterDependentMode) {
				// Dependence Schedule Equations: sfm_c° SC_c - sfm_p° SC_p H - z~_cp° - dfm_cp° DC_cp = 0
				for (int q = 0; q < producedDCM.getNumberOfColumns(); q++) {
					// (sfm_c° SC_c - sfm_p° SC_p H - z~_cp° - dfm_cp° DC_cp)°_q = 0
					int[] currentEqUnknowns = pipProblem.domainEqs_Unknowns[getDelayEquationIndex(q)];
					
					for (int r = 0; r < fromVertex.getScheduleFarkamCount(); r++) { // sfm_c° (SC_c)_*q
						currentEqUnknowns[fromVertex.scheduleFarkamOffset + r] = toInt(fromVertex.dcm.get(r, q));
					}
		    			
					for (int r = 0; r < toVertex.getScheduleFarkamCount(); r++) { // - sfm_p° (SC_p H_cp)_*q
						currentEqUnknowns[toVertex.scheduleFarkamOffset + r] += -toInt(consumedDCM.get(r, q)); 
						// + Since produced and consumed vertices may be the same
					}
		    			
					currentEqUnknowns[edgeDelay] = (q == producedDCM.getNumberOfColumns() - 1) ? -1 : 0; // - (z~_cp)_q°
		   			
					for (int r = 0; r < getDependenceFarkamCount(); r++) { // - dfm_cp° (DC_cp)_q
						currentEqUnknowns[dependenceFarkamOffset + r] = -toInt(producedDCM.get(r, q));
					}
				}
			} else {
				if (!parameterContextSensitive) {
					// Dependence Schedule System: B(t, c)° SC_c - B(t, p)° SC_p H - Z~_cp - B(u, cp)° DC_cp = 0
					for (int u = 0; u < getUnknownCount(); u++) {
						// (B(t, c)° SC_c - B(t, p)° SC_p H - Z~_cp - B(u, cp)° DC_cp)_*u = 0
						for (int g = 0; g <= globalParameterCount; g++) {
							/**
							 * (B(t, c)° SC_c - B(t, p)° SC_p H - Z~_cp - B(u, cp)° DC_cp)_gu = 0
							 * 
							 * ForAll (B, C) : R^(nxm) x R^(nxp) . ForAll (i, j) : (1..m)x(1..p) .
							 *   (B° C)_(i, j) = Sum(q : 1..n . (B°)_(i, q) C_(q, j))
							 *                 = Sum(q : 1..n . B_(q, i) C_(q, j))
							 */ 
							int[] currentEqUnknowns = pipProblem.domainEqs_Unknowns[getDelayEquationIndex(u, g)];
							
							for (int r = 0; r < fromVertex.getScheduleFarkamCount(); r++) { // (B(t, c)° SC_c)_gu
								currentEqUnknowns[fromVertex.getBaseConversionScheduleFarkamOffset(r, g)]
								    = toInt(fromVertex.dcm.get(r, u));
							}
				    			
							for (int r = 0; r < toVertex.getScheduleFarkamCount(); r++) { // - (B(t, p)° SC_p H)_gu 
								currentEqUnknowns[toVertex.getBaseConversionScheduleFarkamOffset(r, g)] 
								    += -toInt(consumedDCM.get(r, u)); 
								// + Since produced and consumed vertices may be the same
							}
				    			
							currentEqUnknowns[edgeDelay] = (u == producedDCM.getNumberOfColumns() - 1) ? -1 : 0; // - B(eps, cp)° z~_cp
				   			
							for (int r = 0; r < getDependenceFarkamCount(); r++) { // - (B(u, cp)° DC_cp)_gu
								currentEqUnknowns[getBaseConversionDependenceFarkamUnkIx(r, g)] 
								    += -toInt(producedDCM.get(r, u));
							}
						}
					}
				} else {
					// Dependence Schedule System: F~° G(t, c)° SC_c - F~° G(t, p)° SC_p H - Z~_cp - F~° G(u, cp)° DC_cp = 0
					for (int u = 0; u < getUnknownCount(); u++) {
						// (F~° G(t, c)° SC_c - F~° G(t, p)° SC_p H - Z~_cp - F~° G(u, cp)° DC_cp)_*u = 0
						for (int g = 0; g <= globalParameterCount; g++) {
							/**
							 * (F~° G(t, c)° SC_c - F~° G(t, p)° SC_p H - Z~_cp - F~° G(u, cp)° DC_cp)_gu = 0
							 * 
							 * ForAll (A, B, C) : R^(lxk) x R^(mxl) x R^(mxn) . ForAll (g, u) : (1..k)x(1..n) .
							 *   (A° B° C)_(g, u) = Sum(q : 1..m . (A° B°)_(g, q) C_(q, u))
							 *      = Sum(q : 1..m . (B A)_(q, g) C_(q, u))
							 *      = Sum(q : 1..m . Sum(h : 1..l . B_(q, h) A_(h, g) C_(q, u)))
							 */ 
							int[] currentEqUnknowns = pipProblem.domainEqs_Unknowns[getDelayEquationIndex(u, g)];
							
							for (int q = 0; q < fromVertex.getScheduleFarkamCount(); q++) { // (F~° G(t, c)° SC_c)_gu
								for (int h = 0; h < contextFarkamCount; h++) {
									currentEqUnknowns[fromVertex.getParameterFarkamOffset(q, h)]
									    = toInt(fromVertex.dcm.get(q, u)) * toInt(parameterContextDCM.get(h, g));
								}
							}
				    			
							for (int q = 0; q < toVertex.getScheduleFarkamCount(); q++) { // -(F~° G(t, p)° SC_p H)_gu
								for (int h = 0; h < contextFarkamCount; h++) {
									currentEqUnknowns[toVertex.getParameterFarkamOffset(q, h)]
									    += -toInt(toVertex.dcm.get(q, u)) * toInt(parameterContextDCM.get(h, g));
								}
							}
				    			
							currentEqUnknowns[edgeDelay] = (u == producedDCM.getNumberOfColumns() - 1) ? -1 : 0; // - B(eps, cp)° z~_cp
				   			
							for (int q = 0; q < getDependenceFarkamCount(); q++) { // -(F~° G(t, p)° SC_p H)_gu
								for (int h = 0; h < contextFarkamCount; h++) {
									currentEqUnknowns[getParameterFarkamUnkIx(q, h)]
									    += -toInt(producedDCM.get(q, u)) * toInt(parameterContextDCM.get(h, g));
								}
							}
						}
					}
				}
			}
		}
		
		public void addDelayInequalities() {
			// EdgeDelay <= 0 
			pipProblem.domainIneqs_Unknowns[edgeDelayIneqOffset + 1][edgeDelay] = 1;
			
			// EdgeDelay <= 1 
			pipProblem.domainIneqs_Unknowns[edgeDelayIneqOffset][edgeDelay] = -1;
			pipProblem.domainIneqs_AdditiveConstant[edgeDelayIneqOffset] = 1;
		}
		
		public void addDependenceFarkamInequalities() {
			if (!parameterDependentMode) {
				for (int r = 0; r < getDependenceFarkamCount(); r++) {
	    			pipProblem.domainIneqs_Unknowns[dependenceFarkamIneqOffset + r][dependenceFarkamOffset + r] = 1;
				}
			} else {
				if (!parameterContextSensitive) {
					for (int r = 0; r < getDependenceFarkamCount(); r++) {
						for (int g = 0; g <= globalParameterCount; g++) {
							int[] currentIneq_Unknowns = pipProblem.domainIneqs_Unknowns[getBaseConversionDependenceFarkamIneqOffset(r, g)]; 
							currentIneq_Unknowns[getBaseConversionDependenceFarkamUnkIx(r, g)] = 1;
						}
					}
				} else {
					for (int r = 0; r < getDependenceFarkamCount(); r++) {
						for (int k = 0; k <= contextFarkamCount; k++) {
							int[] currentIneq_Unknowns = pipProblem.domainIneqs_Unknowns[getParameterFarkamIneqOffset(r, k)]; 
							currentIneq_Unknowns[getParameterFarkamUnkIx(r, k)] = 1;
						}
					}
				}
			}
		}
		
		public void addConstraints() {
			addDelayEquations();
			addDelayInequalities();
			addDependenceFarkamInequalities();
			if (parameterDependentMode) addSkewSymmetricEquations();
		}
		
		public void addSkewSymmetricEquations() {
			/**
			 * ForAll A : R^(n x n) . [ ForAll y : R^n . y° A y = 0
			 *                        [ ==
			 *                        [ ^ ForAll i : 1..n . A_{i,i} = 0
			 *                        [ ^ ForAll (i, j) : { (i, j) : (1..n)^2 . (i != j) } . A_{i, j} + A_{j, i} = 0
			 *                        [ ==
			 *                        [ ^ ForAll (i, j) : (1..n)^2 . A_{i, j} + A_{j, i} = 0
			 */
			if (!parameterContextSensitive) {
				/**
				 * The skew-symmetric matrix is    (° means transpose)   (# means parameter-part of the matrix)
				 * (BC(sfm_c, c)° SC_c#) - (BC(sfm_p, p)° (SC_p H_cp)#) - (B(dfm_cp, cp)° DC_cp#) - (Z_cp~°)
				 * 
				 * ForAll (B, C#) : (R^(p x n))^2 .
				 *  [ ForAll y : R^n . y° (B° C#) y = 0
				 *  [ == ForAll (r, c) : (1..m)x(1..n) . Sum q : 1..p . (B_{q, r} C#_{q, c} + B_{q, c} C#_{q, r}) = 0
				 *  
				 * Since our skew-matrix is a matrix-sum, we'll apply the previous formula to each term and add the left-hand-sides.
				 * To simplify the implementation, we'll seperate the two terms in the formula by taking their contribution
				 * into account in seperate loop-instances ((r, c) and (c, r)).
				 */
				for (int r = 0; r <= globalParameterCount; r++) { 
					for (int c = 0; c <= globalParameterCount; c++) {
						// Add (Sum q : 1..p . (B_{q, r} C_{q, c}) to equation for every term
						int[] currentEq_Unknowns = pipProblem.domainEqs_Unknowns[getSkewSymmetricEquationOffset(r, c)];

						for (int q = 0; q < fromVertex.getScheduleFarkamCount(); q++) { // (BC(sfm_c, c)° SC_c)
							currentEq_Unknowns[fromVertex.getBaseConversionScheduleFarkamOffset(q, r)]
							                  += toInt(fromVertex.dcm.get(q, fromVertex.getUnknownCount() + c));
						}
						
						for (int q = 0; q < toVertex.getScheduleFarkamCount(); q++) { // - (BC(sfm_p, p)° SC_p H_cp)
							currentEq_Unknowns[toVertex.getBaseConversionScheduleFarkamOffset(q, r)]
							                  += -toInt(consumedDCM.get(q, toVertex.getUnknownCount() + c)); // FIXME: Doesn't the dependence map alter the perceived number of dimensions in the consumed computation domain? 
						}
						
						// - (Z_cp~°)
						currentEq_Unknowns[edgeDelay] += ((r == globalParameterCount) && (c == globalParameterCount)) ? -1 : 0;
						
						for (int q = 0; q < getDependenceFarkamCount(); q++) { // - (B(dfm_cp, cp)° DC_cp)
							currentEq_Unknowns[getBaseConversionDependenceFarkamUnkIx(q, r)]
							                  += -toInt(producedDCM.get(q, getUnknownCount() + c));
						}
					}
				}
			} else {
				/**
				 * The skew-symmetric matrix is    (° means transpose) (# means parameter-part of the matrix)
				 * (F~° G(t, c)° SC_c - F~° G(t, p)° SC_p H_cp - Z_cp~° - F~° G(u, cp)° DC_cp)
				 * 
				 * ForAll (A, B, C) : R^(lxk) x R^(mxl) x R^(mxn) . ForAll (g, u) : (1..k)x(1..n) .
			     *   (A° B° C)_(g, u) = Sum(q : 1..m . Sum(h : 1..l . B_(q, h) A_(h, g) C_(q, u)))
				 * ForAll (A, B, C#) : R^(lxn) x R^(mxl) x R^(mxn) .
				 *  [ ForAll y : R^n . y° (A° B° C#) y = 0
				 *  [ == ForAll (r, c) : (1..n)^2 .
				 *  [      Sum(q : 1..m . Sum(h : 1..l . (B_(q, h) A_(h, r) C#_(q, c) + B_(q, h) A_(h, c) C#_(q, r)))) 
				 *  [ == ForAll (r, c) : (1..n)^2 .
				 *  [      Sum(q : 1..m . Sum(h : 1..l . B_(q, h) (A_(h, r) C#_(q, c) + A_(h, c) C#_(q, r)))) 
				 *  
				 * Since our skew-matrix is a matrix-sum, we'll apply the previous formula to each term and add the left-hand-sides.
				 * To simplify the implementation, we'll seperate the two terms in the formula by taking their contribution
				 * into account in seperate loop-instances ((r, c) and (c, r)).
				 */
				for (int r = 0; r <= globalParameterCount; r++) {  
					for (int c = 0; c <= globalParameterCount; c++) {
						// Add (Sum q : 1..p . (B_{q, r} C_{q, c}) to equation for every term
						int[] currentEq_Unknowns = pipProblem.domainEqs_Unknowns[getSkewSymmetricEquationOffset(r, c)];

						for (int q = 0; q < fromVertex.getScheduleFarkamCount(); q++) { // (F~° G(t, c)° SC_c)_{r, c}
							for (int h = 0; h < contextFarkamCount; h++) {
								currentEq_Unknowns[fromVertex.getParameterFarkamOffset(q, h)]
									+= toInt(fromVertex.dcm.get(q, fromVertex.getUnknownCount() + c))
									   * toInt(parameterContextDCM.get(h, r));
							}
						}
						
						for (int q = 0; q < toVertex.getScheduleFarkamCount(); q++) { // (- F~° G(t, p)° SC_p H_cp)_{r, c}
							for (int h = 0; h < contextFarkamCount; h++) {
								currentEq_Unknowns[toVertex.getParameterFarkamOffset(q, h)]
									+= - toInt(consumedDCM.get(q, toVertex.getUnknownCount() + c)) // FIXME: Doesn't the dependence map alter the perceived* number of dimensions in the consumed computation domain? (*: depending on which reality the observations are made) 
									     * toInt(parameterContextDCM.get(h, r));
							}
						}
						
						// - (Z_cp~°)
						currentEq_Unknowns[edgeDelay] += ((r == globalParameterCount) && (c == globalParameterCount)) ? -1 : 0;
						
						for (int q = 0; q < getDependenceFarkamCount(); q++) { // (- F~° G(t, p)° SC_p H_cp)_{r, c}
							for (int h = 0; h < contextFarkamCount; h++) {
								currentEq_Unknowns[getParameterFarkamUnkIx(q, h)]
									+= - toInt(producedDCM.get(q, getUnknownCount() + c)) // FIXME: Doesn't the dependence map alter the perceived* number of dimensions in the consumed computation domain? (*: depending on which reality the observations are made) 
									     * toInt(parameterContextDCM.get(h, r));
							}
						}
					}
				}
			}
		}
			
		public int getDependenceFarkamCount() {
			return producedDCM.getNumberOfRows();
		}
		
		public int getUnknownCount() {
			return  producedDomain.getDimension();
		}
		
		public int getParameterCount() {
			return  producedDomain.getParameterCount();
		}
		
		public int getParameterConstraintCount() {
			return  producedDomain.getParameterConstraintCount();
		}
		
		public GDGFEdge(GDGEdge iGDGEdge, GDGFVertex iFromVertex, GDGFVertex iToVertex, int iIndex) {
			gdgEdge = iGDGEdge;
			fromVertex = iFromVertex;
			toVertex = iToVertex;
			index = iIndex;
			
			producedDomain = (ParametrizedIntegerPolyhederDomain<F>) gdgEdge.producedDomain;
			producedDCM = parameterDependentMode ? producedDomain.getHomogenizedDomainFarkamMatrix()
							                     : producedDomain.getHomogenizedCombinedDomainFarkamMatrix();
			
    		AffineTransformation<F> depAffTrafo = (AffineTransformation<F>) gdgEdge.dataDependence;
    		consumedDomain = toVertex.computationDomain.getTransformedPolyheder(depAffTrafo);
    		consumedDCM = parameterDependentMode ? consumedDomain.getHomogenizedDomainFarkamMatrix()
                                                 : consumedDomain.getHomogenizedCombinedDomainFarkamMatrix();
		}
		
		public GDGFEdge(GDGFeautrierScheduler parentScheduler, PartitionSolution parentSolution, GDGFEdge iParentGDGFEdge, GDGFVertex iFromVertex, GDGFVertex iToVertex, int iIndex) {
			parentGDGFEdge = iParentGDGFEdge;
			gdgEdge = parentGDGFEdge.gdgEdge;
			fromVertex = iFromVertex;
			toVertex = iToVertex;
			index = iIndex;
			
			Matrix<F> parentDependenceDCM = parentGDGFEdge.producedDCM;
			// Reduce dependence domain using schedule of preceeding dimension
			boolean[] usedConstraints = new boolean[parentDependenceDCM.getNumberOfRows()];
			int constraintsToReduce = 0;
			AffineTransformation<Rational> depFarkamTrafo = parentSolution.dependenceFarkams[parentGDGFEdge.index];
			for (int q = 0; q < usedConstraints.length; q++) {
				usedConstraints[q] = (!depFarkamTrafo.subTransformation(q, 1).isZero()); // XXX: Does isZero suffice? (parameters!)
				if (usedConstraints[q]) constraintsToReduce++;
			}
			
			F[][] elms = (F[][]) new Field_[usedConstraints.length + constraintsToReduce][parentDependenceDCM.getNumberOfColumns()];
			int sourceRow = 0;
			for (int r = 0; r < elms.length; r++) {
				for (int c = 0; c < elms[r].length; c++) {
					elms[r][c] = parentDependenceDCM.get(sourceRow, c);
				}
				
				if (usedConstraints[sourceRow]) {
					usedConstraints[sourceRow] = false;
				} else sourceRow++;
			}
			
			producedDCM = DenseMatrix.valueOf(elms);
			
    		consumedDCM = parentGDGFEdge.consumedDCM;
		}

		public Matrix<F> getParameterConstraintMatrix() {
			return producedDomain.getParameterConstraintMatrix();
		}

		public void allocateUnknownsAndConstraints(Allocator ineqA, Allocator eqA, Allocator unknownA) {
			edgeDelay = unknownA.allocate(1);
    		edgeDelayIneqOffset = ineqA.allocate(2);
    		if (!parameterDependentMode) {
        		dependenceFarkamOffset = unknownA.allocate(getDependenceFarkamCount());
       			edgeEqOffset = eqA.allocate(producedDCM.getNumberOfColumns());
        		dependenceFarkamIneqOffset = ineqA.allocate(getDependenceFarkamCount());
    		} else {
       			edgeEqOffset = eqA.allocate(getUnknownCount() * (globalParameterCount + 1));
    			skewSymmetricEqOffset = eqA.allocate(((globalParameterCount + 1) * (globalParameterCount + 2))/2);
    			
    			if (!parameterContextSensitive) {
    	    		baseConversionDependenceFarkamUnkOffset = unknownA.allocate(getBaseConversionDependenceFarkamCount());
            		baseConversionDependenceFarkamIneqOffset = ineqA.allocate(getBaseConversionDependenceFarkamCount());
    			} else {
    				parameterFarkamUnkOffset = unknownA.allocate(getParameterFarkamCount());
            		parameterFarkamIneqOffset = ineqA.allocate(getParameterFarkamCount());
    			}
    		}
    	}
	}
	
	class GDGFVertex {
		GDGVertex gdgVertex = null;
		GDGFVertex parentGDGFVertex = null;
		
		ParametrizedIntegerPolyhederDomain<F> computationDomain = null;
		Matrix<F> dcm = null;
		
		int index = -1;
		
		// Unknown offsets
		int scheduleFarkamOffset = -1;
		int baseConversionScheduleFarkamUnkOffset = -1;
		int parameterFarkamUnkOffset = -1;
		
		// Ineq offset
		int scheduleFarkamIneqOffset = -1; 
		int parameterFarkamIneqOffset = -1;
		int baseConversionScheduleFarkamIneqOffset = -1;
		
		PolyhedralPartitionAffineTransformation<Rational> scheduleSolution = null;
		
		/* parameterDependentMode && !parameterContextSensitive */
		public int getBaseConversionScheduleFarkamIx(int farkamMultiplier, int parameter) {
			assert parameterDependentMode && !parameterContextSensitive;
			
			return farkamMultiplier*(globalParameterCount + 1) + parameter;
		}
		
		public int getBaseConversionScheduleFarkamOffset(int farkamMultiplier, int parameter) {
			assert parameterDependentMode && !parameterContextSensitive;
			
			return baseConversionScheduleFarkamUnkOffset 
				   + getBaseConversionScheduleFarkamIx(farkamMultiplier, parameter);
		}
		
		public int getBaseConversionScheduleFarkamCount() {
			assert parameterDependentMode && !parameterContextSensitive;
			
			return getScheduleFarkamCount() * (globalParameterCount + 1);
		}
		
		public int getBaseConversionScheduleFarkamIneqOffset(int farkamMultiplier, int parameter) {
			assert parameterDependentMode && !parameterContextSensitive;
			
			return baseConversionScheduleFarkamIneqOffset 
			   + getBaseConversionScheduleFarkamIx(farkamMultiplier, parameter);
		}		
		/**/
		
		/* parameterDependentMode && parameterContextSensitive */
		public int getParameterFarkamIx(int scheduleFarkam, int contextFarkam) {
			assert parameterDependentMode && parameterContextSensitive;
			
			return scheduleFarkam*contextFarkamCount + contextFarkam;
		}
		
		public int getParameterFarkamIneqOffset(int scheduleFarkam, int contextFarkam) {
			assert parameterDependentMode && parameterContextSensitive;
			
			return parameterFarkamIneqOffset + getParameterFarkamIx(scheduleFarkam, contextFarkam);
		}
		
		public int getParameterFarkamOffset(int scheduleFarkam, int contextFarkam) {
			assert parameterDependentMode && parameterContextSensitive;
			
			return parameterFarkamUnkOffset + getParameterFarkamIx(scheduleFarkam, contextFarkam);
		}
		
		public int getParameterFarkamCount() {
			assert parameterDependentMode && parameterContextSensitive;
			
			return getScheduleFarkamCount()*contextFarkamCount;
		}
		/**/
			
		public int getUnknownCount() {
			return computationDomain.getDimension();
		}
		
		public void addScheduleFarkamInequalities() {
			if (!parameterDependentMode) {
	    		for (int f = 0; f < getScheduleFarkamCount(); f++) {
	        		pipProblem.domainIneqs_Unknowns[scheduleFarkamIneqOffset + f][scheduleFarkamOffset + f] = 1;
	    		}
			} else {
				if (!parameterContextSensitive) {
		    		for (int f = 0; f < getScheduleFarkamCount(); f++) {
		    			for (int g = 0; g <= globalParameterCount; g++) {
			        		int[] currentIneq_Unknowns = pipProblem.domainIneqs_Unknowns[getBaseConversionScheduleFarkamIneqOffset(f, g)];
			        		currentIneq_Unknowns[getBaseConversionScheduleFarkamOffset(f, g)] = 1;
		    			}
		    		}
				} else {
		    		for (int f = 0; f < getScheduleFarkamCount(); f++) {
		    			for (int g = 0; g < contextFarkamCount; g++) {
			        		int[] currentIneq_Unknowns = pipProblem.domainIneqs_Unknowns[getParameterFarkamIneqOffset(f, g)];
			        		currentIneq_Unknowns[getParameterFarkamOffset(f, g)] = 1;
		    			}
		    		}
				}
			}
		}
		
		public int getScheduleFarkamCount() {
			if (!parameterDependentMode) {
				return computationDomain.getConstraintCount() + globalParameterCount + 1;
			} else {
				return computationDomain.getConstraintCount() + 1;
			}
		}
		
		public GDGFVertex(GDGVertex iGDGVertex, int iIndex) {
			gdgVertex = iGDGVertex;
			index = iIndex;
			
			computationDomain = (ParametrizedIntegerPolyhederDomain<F>) gdgVertex.computationDomain;
			dcm = parameterDependentMode ? computationDomain.getHomogenizedDomainFarkamMatrix()
										 : computationDomain.getHomogenizedCombinedDomainFarkamMatrix();
		}

		public GDGFVertex(GDGFVertex iParentGDGFVertex, int iIndex) {
			parentGDGFVertex = iParentGDGFVertex;
			gdgVertex = parentGDGFVertex.gdgVertex;
			index = iIndex;
			
			computationDomain = parentGDGFVertex.computationDomain;
			dcm = parentGDGFVertex.dcm;
		}

		public Matrix<F> getParameterConstraintMatrix() {
			return computationDomain.getParameterConstraintMatrix();
		}

		public void allocateUnknownsAndConstraints(Allocator ineqA, Allocator eqA, Allocator unknownA) {
    		if (!parameterDependentMode) {
            	scheduleFarkamOffset = unknownA.allocate(getScheduleFarkamCount());
           		scheduleFarkamIneqOffset = ineqA.allocate(getScheduleFarkamCount());
    		} else {
    			if (!parameterContextSensitive) {
               		baseConversionScheduleFarkamUnkOffset = unknownA.allocate((globalParameterCount + 1) * getScheduleFarkamCount());
               		baseConversionScheduleFarkamIneqOffset = ineqA.allocate((globalParameterCount + 1) * getScheduleFarkamCount());
    			} else {
               		parameterFarkamUnkOffset = unknownA.allocate(contextFarkamCount * getScheduleFarkamCount());
               		parameterFarkamIneqOffset = ineqA.allocate(contextFarkamCount * getScheduleFarkamCount());
    			}
       		}
		}
	}
	
	class PartitionSolution {
		AffineTransformation<Rational>[] schedules = (AffineTransformation<Rational>[]) new AffineTransformation[vertices.size()];
		AffineTransformation<Rational>[] dependenceFarkams = (AffineTransformation<Rational>[]) new AffineTransformation[edges.size()];
		//AffineTransformation<Rational>[] dependenceFarkams = (AffineTransformation<Rational>[]) new AffineTransformation[edges.size()];
		AffineTransformation<Rational>[] edgeDelays = (AffineTransformation<Rational>[]) new AffineTransformation[edges.size()];
		AffineTransformation<Rational> edgesRemaining = null;
	}
	
	class PolyhedralPartitionSolution extends PolyhedralPartition<PartitionSolution> {
		PartitionSolution nodeSolution = null;
		PolyhedralPartitionSolution subSchedule = null;
		
		public PolyhedralPartitionSolution(Rational[] iCondition, PolyhedralPartition<PartitionSolution> iPositiveS, PolyhedralPartition<PartitionSolution> iZeroS, PolyhedralPartition<PartitionSolution> iNegativeS) {
			super(iCondition, iPositiveS, iZeroS, iNegativeS);
		}

		public PolyhedralPartitionSolution(PartitionSolution iLeafContents) {
			super(iLeafContents);
		}
		
	    private PartitionSolution processPartitionSolution(AffineTransformation<Rational> currentPartitionSolution, Rational[][] currentPartitionContext) {
	    	PartitionSolution partitionSolution = new PartitionSolution();
	    	
	    	for (int v = 0; v < vertices.size(); v++) { GDGFVertex vertex = vertices.get(v);
				dbg(0, new ReporterParagraph("Vertex %s schedule:", vertex.gdgVertex.getName()), new ReporterBreak());
	       		Matrix<F> pilcdDCM_T = vertex.dcm.transpose();
	        		
	    		if (!parameterDependentMode) {
		    		AffineTransformation<Rational> schedFarkamSolution 
		    			= currentPartitionSolution.subTransformation(vertex.scheduleFarkamOffset, vertex.getScheduleFarkamCount());
		   			dbg(4, "Schedule Farkams: %s", schedFarkamSolution.convertToString(new String[]{"s0", "s1", "s2", "s3", "s4", "s5", "s6", "s7"}, new String[]{"m", "n", "p", "q"}));
		   			
			    	partitionSolution.schedules[v] = new AffineTransformation<Rational>( 
				   			((DenseMatrix) pilcdDCM_T).times(schedFarkamSolution.transformationMatrix, zero), Rational.ZERO, Rational.ONE);
	    		} else {
	    			Rational[][] elms = new Rational[vertex.getScheduleFarkamCount()][contextFarkamCount];
	    			AffineTransformation<Rational> parameterConstraintsToScheduleFarkamsTrafo
	    				= currentPartitionSolution.subTransformation(vertex.parameterFarkamUnkOffset, vertex.getParameterFarkamCount());
	    			
	    			assert (parameterConstraintsToScheduleFarkamsTrafo.transformationMatrix.getNumberOfColumns() == 1);
	    			for (int f = 0; f < vertex.getScheduleFarkamCount(); f++) {
	    				for (int c = 0; c < contextFarkamCount; c++) {
	    					elms[f][c] = parameterConstraintsToScheduleFarkamsTrafo.transformationMatrix.get(vertex.getParameterFarkamIx(f, c), 0);
	    				}
	    			}
	    			
	    			DenseMatrix<Rational> parameterContextToScheduleFarkamsTrafoMatrix = (DenseMatrix<Rational>) DenseMatrix.valueOf(elms);
	    			
		   			dbg(4, new ReporterParagraph("Parameter Context to Schedule Farkam Trafo:"), parameterContextToScheduleFarkamsTrafoMatrix.asReportable());
	    			
	    			Matrix<Rational> parametersToScheduleFarkamTrafo
	    			    = parameterContextToScheduleFarkamsTrafoMatrix.times((Matrix<Rational>) parameterContextDCM, Rational.ZERO);
	    			
		   			dbg(4, new ReporterParagraph("Parameter to Schedule Farkam Trafo:"), parametersToScheduleFarkamTrafo.asReportable());
	    			
			    	partitionSolution.schedules[v] = new AffineTransformation<Rational>( 
				   			((DenseMatrix) pilcdDCM_T).times(parametersToScheduleFarkamTrafo, zero), Rational.ZERO, Rational.ONE);
	    		}
		   			
		       	dbg(0, "Schedule: %s", partitionSolution.schedules[v].convertToString(new String[]{"i", "j", "k"}, new String[]{"m", "n", "p", "q"}));
	   		}
	    	
	    	for (int e = 0; e < edges.size(); e++) { GDGFEdge edge = edges.get(e);
				dbg(0, "Edge %s solution: %s", edge.gdgEdge.getIndex()); dbg(0, new ReporterBreak());
	    		Matrix<F> pildDCM_T = edge.producedDCM.transpose();

	    		if (!parameterDependentMode) {
	        		partitionSolution.dependenceFarkams[e] 
	        		    = currentPartitionSolution.subTransformation(edge.dependenceFarkamOffset, edge.getDependenceFarkamCount());
	       			dbg(0, "Edge Farkams: %s", partitionSolution.dependenceFarkams[e].convertToString(new String[]{"d0", "d1", "d2", "d3", "d4", "d5", "d6", "d7"}, new String[]{"m", "n", "p", "q"}));
	    		} else {
	    			Rational[][] elms = new Rational[edge.getDependenceFarkamCount()][contextFarkamCount];
	    			AffineTransformation<Rational> parameterConstraintsToDependenceFarkamsTrafo
	    				= currentPartitionSolution.subTransformation(edge.parameterFarkamUnkOffset, edge.getParameterFarkamCount());
	    			
	    			assert (parameterConstraintsToDependenceFarkamsTrafo.transformationMatrix.getNumberOfColumns() == 1);
	    			for (int f = 0; f < edge.getDependenceFarkamCount(); f++) {
	    				for (int c = 0; c < contextFarkamCount; c++) {
	    					elms[f][c] = parameterConstraintsToDependenceFarkamsTrafo.transformationMatrix.get(edge.getParameterFarkamIx(f, c), 0);
	    				}
	    			}
	    			
	    			DenseMatrix<Rational> parameterContextToDependenceFarkamsTrafoMatrix = (DenseMatrix<Rational>) DenseMatrix.valueOf(elms);
	    			
		   			dbg(4, new ReporterParagraph("Parameter Context to Dependence Farkam Trafo:"), parameterContextToDependenceFarkamsTrafoMatrix.asReportable());
	    			
	    			Matrix<Rational> parametersToDependenceFarkamTrafo
	    			    = parameterContextToDependenceFarkamsTrafoMatrix.times((Matrix<Rational>) parameterContextDCM, Rational.ZERO);
	    			
		   			dbg(4, new ReporterParagraph("Parameter to Dependence Farkam Trafo:"), parametersToDependenceFarkamTrafo.asReportable());
	    			
			    	partitionSolution.dependenceFarkams[e] = new AffineTransformation<Rational>(parametersToDependenceFarkamTrafo,
			    			         Rational.ZERO, Rational.ONE);
	    		}
	        	partitionSolution.edgeDelays[e]
	       		    = currentPartitionSolution.subTransformation(edge.edgeDelay, 1);
	       	
	    		AffineTransformation<Rational> depTimeOffset = new AffineTransformation<Rational>( 
	   				((DenseMatrix) pildDCM_T).times(partitionSolution.dependenceFarkams[e].transformationMatrix, zero), Rational.ZERO, Rational.ONE);
	   			
	       		dbg(4, "Time Offset: %s", depTimeOffset.convertToString(new String[]{"i", "j", "k"}, new String[]{"m", "n", "p", "q"}));
	   			dbg(0, "Edge Delay: %s", partitionSolution.edgeDelays[e].convertToString(new String[]{"w"}, new String[]{"m", "n", "p", "q"}));
	   		}
	    	
	    	partitionSolution.edgesRemaining = currentPartitionSolution.subTransformation(0, 1);
	    	dbg(0, "Edges remaining: %s", partitionSolution.edgesRemaining.convertToString(new String[]{"z"}, new String[]{"m", "n", "p", "q"}));
	    	
	    	return partitionSolution;
	    }
	}
	
    public PolyhedralPartitionSolution fromPipSolution(PolyhedralPartitionAffineTransformation<Rational> apSolution) {
		PolyhedralPartitionSolution result = new PolyhedralPartitionSolution(null);
		
		if (apSolution != null) {
			result.nodeSolution = result.processPartitionSolution(apSolution.leafContents, apSolution.getContextInequalities());

			// Process succeeding schedule dimensions
			if (!result.nodeSolution.edgesRemaining.isZero()) {
				GDGFeautrierScheduler<F> subScheduler = new GDGFeautrierScheduler<F>(gdGraph, parameterDependentMode, parameterContextSensitive, this, result.nodeSolution, zero, one);
				result.subSchedule = subScheduler.scheduleSolution;
			}
			
			// Process SubPartitions
			if (apSolution.getNegativeSubPart() != null) { // SubPartitions
				dbg(0, "<table width=100%%><tr><td colspan=2>Negative sub-partition:</td></tr><tr><td width=25>&nbsp;</td><td>");
				result.negativeSubPart = (PolyhedralPartition<PartitionSolution>) fromPipSolution(apSolution.getNegativeSubPart());
				dbg(0, "</td></tr><tr><td colspan=2>Positive sub-partition:</td></tr><tr><td width=25>&nbsp;</td><td>");
				result.positiveSubPart = (PolyhedralPartition<PartitionSolution>) fromPipSolution(apSolution.getPositiveSubPart());
				dbg(0, "</td></tr></table>");
				result.zeroSubPart = result.positiveSubPart;
			}
		}
		
		return result;
	}
	
    private int toInt(F f) {
    	Rational li = (Rational) f;
    	
    	return (int) li.longValue();
    }

    public void buildEdgeAndVertexVectors() {
    	int[] vertexMap = new int[gdGraph.getVertexCount()];
    	for (int v = 0; v < gdGraph.getVertexCount(); v++) {
    		GDGVertex vertex = gdGraph.getVertex(v);
    		
    		if (!vertex.isReady) {
    			vertexMap[v] = vertices.size(); 
    			vertices.add(new GDGFVertex(vertex, vertexMap[v]));
    		}
    	}
    	
    	for (int e = 0; e < gdGraph.getEdgeCount(); e++) {
    		GDGEdge edge = gdGraph.getEdge(e);
    		
    		if (!edge.isTriviallySatisfied()) {
    			edges.add(new GDGFEdge(edge, 
    					               vertices.get(vertexMap[edge.getFromVertex().getIndex()]),
    					               vertices.get(vertexMap[edge.getToVertex().getIndex()]),
    					               edges.size())
    					 );
    		}
    	}
    	
    }
    
	private void buildEdgeAndVertexVectorsFromParent(GDGFeautrierScheduler<F> parentScheduler, PartitionSolution parentSolution) {
    	int[] vertexMap = new int[parentScheduler.vertices.size()];
		for (int v = 0; v < parentScheduler.vertices.size(); v++) { GDGFVertex vertex = parentScheduler.vertices.get(v);
			vertexMap[v] = vertices.size(); 
			vertices.add(new GDGFVertex(vertex, vertexMap[v]));
		}
	
		for (int e = 0; e < parentScheduler.edges.size(); e++) { GDGFEdge edge = parentScheduler.edges.get(e);
			if (parentSolution.edgeDelays[e].isZero()) { // XXX: The isZero function may not be ideal in this context: what about (affine functions of) parameters which are potentially zero  
				edges.add(new GDGFEdge(parentScheduler, parentSolution,
						 edge, vertices.get(vertexMap[edge.fromVertex.index]),
	                     vertices.get(vertexMap[edge.toVertex.index]),
	                    		 edges.size())
						);
			}
		}
	}

	public GDGFeautrierScheduler(GDGraph iGDGraph, boolean iParameterDependendMode, boolean iParameterContextSensitive, GDGFeautrierScheduler<F> parentScheduler, PartitionSolution parentSolution, F iZero, F iOne) {
		gdGraph = iGDGraph;
		zero = iZero;
		one = iOne;
		
		parameterDependentMode = iParameterDependendMode;
		parameterContextSensitive = iParameterContextSensitive;
		
		if (parentScheduler == null) {
			buildEdgeAndVertexVectors();
			try {
				File file = new File("debug.html");
				file.delete();
				debugOutputStream = new PrintStream("debug.html");
				debugOutputStream.println("<html>");
				debugOutputStream.println("<meta http-equiv=\"Content-Style-Type\" content=\"text/css\">");
				debugOutputStream.println("<meta http-equiv=\"content-type\" content=\"text/html; charset=iso-8859-1\">");
				debugOutputStream.println("<head>");
				debugOutputStream.println("<style type=\"text/css\">");
				debugOutputStream.println("table { spacing: 0px; border-spacing: 0px; margin: 0px; padding: 0em; }");
				debugOutputStream.println("td.matrix { padding-left: 0.25em; padding-right: 0.25em; " +
						"border-right: 1px solid #333333; border-bottom: 1px solid #333333; border-left: 1px solid #AAAAAA; border-top: 1px solid #AAAAAA;" +
						"background-color: white; text-align: right }");
				debugOutputStream.println("</style>");
				debugOutputStream.println("</head>");
				debugOutputStream.println("<body>");
				reporterBasic = new ReporterBasic();
			} catch (FileNotFoundException e) {
				System.err.println("File not found exception while trying to open debug output file.");
				e.printStackTrace(System.err);
			}
		} else {
			buildEdgeAndVertexVectorsFromParent(parentScheduler, parentSolution);
			debugOutputStream = parentScheduler.debugOutputStream;
			reporterBasic = parentScheduler.reporterBasic;
		}
		
    	assert gdGraph.isExactAffineDFG();
    	assert zero.getClass() == Rational.class; // Currently only works for rationals (?)
    	
   		dbg(0, String.format("Starting scheduling for %d edges", edges.size()));
       	schedule();
   	}
	
	class Allocator {
		int total = -1;
		
		public Allocator(int iTotal) { total = iTotal; }
		
		public int allocate(int count) {
			int result = total;
			
			total += count;
			
			return result; 
		}
		
		public int getTotal() { return total; };
	}
	
    private void schedule() {
    	ParametrizedIntegerPolyhederDomain<F>[] subContexts 
    		= new ParametrizedIntegerPolyhederDomain[edges.size() + vertices.size()];
    	int subContextIx = 0; 
    	
    	// Create the parameter context first
    	for (int e = 0; e < edges.size(); e++) { GDGFEdge edge = edges.get(e);
			if (e == 0) globalParameterCount = edge.getParameterCount();
			assert (globalParameterCount == edge.getParameterCount());
			subContexts[subContextIx++] 
			    = new ParametrizedIntegerPolyhederDomain<F>(edge.getParameterConstraintMatrix(), null, zero, one);
    	}
    	
    	for (int v = 0; v < vertices.size(); v++) { GDGFVertex vertex = vertices.get(v);
    		assert (globalParameterCount == vertex.computationDomain.getParameterCount());
			subContexts[subContextIx++] 
			    = new ParametrizedIntegerPolyhederDomain<F>(vertex.getParameterConstraintMatrix(), null, zero, one);
    	}
    	
    	ParametrizedIntegerPolyhederDomain<F> parameterDomain = ParametrizedIntegerPolyhederDomain.<F>intersection(subContexts, zero, one);
    	parameterContextDCM = parameterDomain.getDomainConstraintMatrix().getHomogenizedMatrix(zero, one);
    	contextFarkamCount = parameterContextDCM.getNumberOfRows();
    	dbg(3, new ReporterParagraph("Context domain:"), parameterContextDCM.asReportable(), new ReporterBreak());
    	
    	/* TODO: XXX: ...we should convert the structure of polytopes-classes such that
    	 * we have a hierarchic class-structure with each class defining parameter-polytopes
    	 * for its children in the hierarchy, and domain polytopes as the leaves.
    	 */

    	// Count total unknowns and constraints 
    	Allocator ineqA = new Allocator(0);
    	Allocator eqA = new Allocator(1); // Epsilon-sum
    	Allocator unknownA = new Allocator(1); // Epsilon-sum
    	allocateEdgeUnknownsAndConstraints(ineqA, eqA, unknownA);
    	allocateVertexUnknownsAndConstraints(ineqA, eqA, unknownA);
    	
    	// XXX: Pip Problem should be simplified prior to submission to PIP (eliminate as much dependence farkams as possible)

    	pipProblem = new PipProblem(unknownA.getTotal(), 0, ineqA.getTotal(), eqA.getTotal(), 0, -1, PipProblem.USolutionType.Integer);
    	createNamesForPip(unknownA.getTotal(), eqA.getTotal());
    	addConstraints();

		dbg(2, new ReporterBreak(), pipProblem.asReportable());
		dbg(1, new ReporterBreak());
		
    	PipQuast solution = pipProblem.solve();
		dbg(1, solution.asReportable(pipProblem.unknownNames, pipProblem.parameterNames), new ReporterBreak());
		
		// Extract schedules
   		PolyhedralPartitionAffineTransformation<Rational> apSolution = PolyhedralPartitionAffineTransformation.fromPipQuast(solution);
   		
   		assert (apSolution.leafContents != null); // No parameters --> Pip solution should be one leaf.
   		
   		scheduleSolution = fromPipSolution(apSolution);
    }

	private void addConstraints() {
		addEdgeConstraints();
    	addVertexConstraints();
		
    	// Last equation: Sum equation: a = #edgedelays - sum(edgedelays) --> minimize
    	pipProblem.domainEqs_Unknowns[0][0] = 1;
    	for (int e = 0; e < edges.size(); e++) { GDGFEdge edge = edges.get(e);
    		pipProblem.domainEqs_Unknowns[0][edge.edgeDelay] = 1;
    	}
		pipProblem.domainEqs_AdditiveConstant[0] = -edges.size();
	}

	private void allocateVertexUnknownsAndConstraints(Allocator ineqA, Allocator eqA, Allocator unknownA) {
		for (int v = 0; v < vertices.size(); v++) { 
			vertices.get(v).allocateUnknownsAndConstraints(ineqA, eqA, unknownA);
    	}
	}

	private void allocateEdgeUnknownsAndConstraints(Allocator ineqA, Allocator eqA, Allocator unknownA) {
		for (int e = 0; e < edges.size(); e++) { 
			edges.get(e).allocateUnknownsAndConstraints(ineqA, eqA, unknownA);
    	}
	}

	private void createNamesForPip(int unknownCount, int equationCount) {
		// Create names for unknowns
    	pipProblem.unknownNames = new String[unknownCount];
    	pipProblem.equationNames = new String[equationCount];
    	pipProblem.unknownNames[0] = "a";
		pipProblem.equationNames[0] = "Remaining dependencies";
    	for (int e = 0; e < edges.size(); e++) { GDGFEdge edge = edges.get(e);
    		for (int f = 0; f < edge.getDependenceFarkamCount(); f++) {
    			if (!parameterDependentMode) {
        			pipProblem.unknownNames[edge.dependenceFarkamOffset + f] = String.format("d_{%d, %d}", edge.gdgEdge.getIndex(), f);
    			} else {
    				if (!parameterContextSensitive) {
    	    			for (int p = 0; p <= globalParameterCount; p++) {
    	    				pipProblem.unknownNames[edge.getBaseConversionDependenceFarkamUnkIx(f, p)]
    	    				    = String.format("B_{d_{%d, %d}, %d}", edge.gdgEdge.getIndex(), f, p);
    	    			}
    				} else {
    	    			for (int p = 0; p < contextFarkamCount; p++) {
    	    				pipProblem.unknownNames[edge.getParameterFarkamUnkIx(f, p)]
    	    				    = String.format("G_{d_{%d, %d}, %d}", edge.gdgEdge.getIndex(), f, p);
    	    			}
    				}
    			}
    		}
   			pipProblem.unknownNames[edge.edgeDelay] = String.format("e_{%d}", edge.gdgEdge.getIndex());
   			
   			// Create names for equations
   			// Delay equations
			for (int u = 0; u < edge.getUnknownCount(); u++) {
				if (!parameterDependentMode) {
					pipProblem.equationNames[edge.getDelayEquationIndex(u)] = String.format("Delay equation (unknown %2d)", u);
				} else {
					for (int g = 0; g <= globalParameterCount; g++) {
						pipProblem.equationNames[edge.getDelayEquationIndex(u, g)]
						   = String.format("Delay equation (unknown %2d, parameter %2d)", u, g);
					}
				}
			}
			
			// Skew-Symmetric equations
			if (parameterDependentMode) {
				for (int r = 0; r <= globalParameterCount; r++) {
					for (int c = 0; c <= globalParameterCount; c++) {
						pipProblem.equationNames[edge.getSkewSymmetricEquationOffset(r, c)]
												   = String.format("Skew Symmetric equation (row %2d, column %2d)", r, c);
					}
				}
			}
    	}
    	
    	for (int v = 0; v < vertices.size(); v++) { GDGFVertex vertex = vertices.get(v);
    		for (int f = 0; f < vertex.getScheduleFarkamCount(); f++) {
    			if (!parameterDependentMode) {
        			pipProblem.unknownNames[vertex.scheduleFarkamOffset + f] = String.format("s_{%s, %d}", vertex.gdgVertex.getName(), f);
    			} else {
    				if (!parameterContextSensitive) {
		    			for (int p = 0; p <= globalParameterCount; p++) {
		    				pipProblem.unknownNames[vertex.getBaseConversionScheduleFarkamOffset(f, p)]
		    				    = String.format("B_{s_{%s, %d}, %d}", vertex.gdgVertex.getName(), f, p);
		    			}
    				} else {
    	    			for (int p = 0; p < contextFarkamCount; p++) {
    	    				pipProblem.unknownNames[vertex.getParameterFarkamOffset(f, p)]
    	    				    = String.format("G_{s_{%s, %d}, %d}", vertex.gdgVertex.getName(), f, p);
    	    			}
    				}
    			}
			}
    	}
    	
    	pipProblem.parameterNames = null;
	}

	private void addEdgeConstraints() {
		for (int e = 0; e < edges.size(); e++) { GDGFEdge edge = edges.get(e); // Edge
    		edge.addConstraints();

    		dbg(3, new ReporterTable(new Reportable[][]{
    				{new ReporterParagraph("Produced %s domain:", edge.fromVertex.gdgVertex.getName()),
    					new ReporterParagraph("Produced (dependence) domain:"),
    					new ReporterParagraph("Transformation:"),
    					new ReporterParagraph("Consumed %s domain:", edge.toVertex.gdgVertex.getName()),
    					new ReporterParagraph("Effective consumed domain:")
    				},
    				{edge.fromVertex.dcm.asReportable(),
    					edge.producedDCM.asReportable(),	
    					((AffineTransformation<F>) edge.gdgEdge.dataDependence).transformationMatrix.asReportable(),
    					edge.toVertex.dcm.asReportable(),
    					edge.consumedDCM.asReportable()
    				}
    		}), new ReporterBreak());
    	}
	}

	/**
     * Adds the constraints (Union v : vertices . Union q : scheduleFarkams(v) . { q >= 0 }) 
     */
	private void addVertexConstraints() {
    	for (int v = 0; v < vertices.size(); v++) { GDGFVertex vertex = vertices.get(v); // Vertex
    		vertex.addScheduleFarkamInequalities();
    		
    		dbg(3, new ReporterParagraph(String.format("Vertex %s domain:", vertex.gdgVertex.getName())), 
     				new ReporterBreak(), 
     				new ReporterParagraph(vertex.computationDomain.getDomainConstraintsDescriptionAsText(new String[]{"i", "j", "m", "n"}, new String[]{})),
     				new ReporterBreak());
   			dbg(6, new ReporterBreak(), new ReporterParagraph(pipProblem.getDomainConstraintsDescriptionAsText()));
    	}
	}
}

/*public AffineTransformation<F> addParameterIdentityTransform(AffineTransformation<F> t, int parameterCount) {
Matrix<F> m = t.transformationMatrix;
System.out.println(((DenseMatrix) m).asString(10));
F[][] elms = (F[][]) new Field[m.getNumberOfRows() + parameterCount][m.getNumberOfColumns()];
for (int r = 0; r < m.getNumberOfRows() - 1; r++) {
	for (int c = 0; c < m.getNumberOfColumns(); c++) {
		elms[r][c] = m.get(r, c);
	}
}

int rowOffs = m.getNumberOfRows() - 1; 
for (int r = 0; r <= parameterCount; r++) { // Includes homogeneous coordinate
	for (int c = 0; c < m.getNumberOfColumns(); c++) {
		elms[rowOffs + r][c] = ((c - rowOffs) == r) ? one : zero;
	}
}

Matrix<F> resultM = DenseMatrix.valueOf(elms);
assert resultM.isInternalDataConsistent();
System.out.println(((DenseMatrix) resultM).asString(10));

return new AffineTransformation<F>(resultM, zero, one);
}*/