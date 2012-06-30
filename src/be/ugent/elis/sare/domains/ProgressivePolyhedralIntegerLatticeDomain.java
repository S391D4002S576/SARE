package be.ugent.elis.sare.domains;

import be.ugent.elis.AffineTransformation_Obsolete;

public class ProgressivePolyhedralIntegerLatticeDomain extends Domain implements PolyhedralIntegralLattice {
	int loopDimension = -1;
	int parameterDimension = -1;
	AffineTransformation_Obsolete[] lowerBoundTransformation = null;
	AffineTransformation_Obsolete[] upperBoundTransformation = null;
	
	public void setLoopDimension(int newLoopDimension) {
		if (loopDimension != newLoopDimension) {
			loopDimension = newLoopDimension;
			
			lowerBoundTransformation = new AffineTransformation_Obsolete[loopDimension];
			upperBoundTransformation = new AffineTransformation_Obsolete[loopDimension];
		}
	}
	
	public void setParameterDimension(int newParameterDimension) {
		if (parameterDimension != newParameterDimension) {
			parameterDimension = newParameterDimension;
		}
	}
	
	public boolean isInternalDataConsistent() {
		return false;
		
		// D: U = uBPT
		// D: L = lBPT
		//
		// D: ForAll q : 0..(d - 1) . U_{q} = U_{q..q, 0..(q - 1) union {- d -}}
		//
		// D: R_{<>}(<>) := q : 0..(d - 1) . 
		//                  (q = 0) ? x : EmptySet . L_{0}..U_{0}
		//                          : x : P_{q - 1} . (L_{q}.x)..(U_{q}.x)
		// D: P_{<>} := q : 0..(d - 1) . 
		//              (q = 0) ? L_{0} .. U_{0}
		//                      : Union x : P_{q - 1} . {- x -} >< L_{q}.x .. U_{q}.x
		//           == q : 0..(d - 1) . 
		//              (q = 0) ? L_{0} .. U_{0}
		//                      : Union x : P_{q - 1} . {- x -} >< R_{q}(x)
		// =>
		// ForAll q : 0..(d - 1) . ForAll x : P_{q} . Exists y : P_{q - 1} . Exists z : R_{q}(y) . x = y concat z 
		// -->
		// For a valid iteration domain:  ForAll q : 0..(d - 1) . ForAll x : P_{q - 1} . (L_{q} - U_{q}).x <= 0
		//
		// lu_q.y <= 0     lu_(q + 1).x <= 0
		// lu_(q + 1).x = lu_(q + 1)_{0..(q - 1)}.y + lu_(q + 1)_{q}.z <= 0
		
		//for (int q = 0; q < dimension; q++) {
			// lowerbound < upperbound, and bounds should only depend on previous parameters?
		//}
		
		// Bounds should only depend on previous parameters
		//return true; // not yet completed
	}

	@Override public int getDimension() {
		return loopDimension;
	}

	@Override public int getConstraintCount() {
		return (lowerBoundTransformation.length + upperBoundTransformation.length);
	}
}
