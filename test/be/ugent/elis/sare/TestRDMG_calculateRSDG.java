package be.ugent.elis.sare;

import org.jscience.mathematics.numbers.LargeInteger;

import be.elis.ugent.graphLibrary.Vertex;
import be.ugent.elis.AffineModuleRelation;
import be.ugent.elis.cg.PolyhedralDomainVertexData;
import be.ugent.elis.cg.RDMGraph;
import be.ugent.elis.cg.RSDGraph;
import be.ugent.elis.graphLibrary.GraphTesting;
import be.ugent.elis.sare.domains.ParametrizedIntegerPolyhederDomain;

public class TestRDMG_calculateRSDG extends GraphTesting {
	LargeInteger _li = LargeInteger.valueOf(1);
	
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
	public void testCalculatedRSDG() {
	}
}
