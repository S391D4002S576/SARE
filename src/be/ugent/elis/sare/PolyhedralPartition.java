package be.ugent.elis.sare;

import org.jscience.mathematics.numbers.Rational;

public class PolyhedralPartition<S> {
	int parameterCount = -1;
	Rational[] condition = null;
	
	public PolyhedralPartition<S> positiveSubPart = null;
	public PolyhedralPartition<S> zeroSubPart = null;
	public PolyhedralPartition<S> negativeSubPart = null;
	
	public S leafContents = null;
	
	PolyhedralPartition<S> parentPart = null;
	
	public PolyhedralPartition(Rational[] iCondition, 
			               PolyhedralPartition<S> iPositiveS, PolyhedralPartition<S> iZeroS, PolyhedralPartition<S> iNegativeS) {
		parameterCount = iCondition.length - 1;
		condition = iCondition;
		
		positiveSubPart = iPositiveS;
		zeroSubPart = iZeroS;
		negativeSubPart = iNegativeS;
		
		positiveSubPart.parentPart = this;
		zeroSubPart.parentPart = this;
		negativeSubPart.parentPart = this;
	}
	
	public PolyhedralPartition(S iLeafContents) {
		leafContents = iLeafContents;
	}

	public Rational[][] getContextInequalities() {
	    int contextConstraintCount = 0;
	    PolyhedralPartition<S> p = null;
	    for (p = this; p.parentPart != null; p = p.parentPart) contextConstraintCount++;
	    
	    Rational[][] result = new Rational[contextConstraintCount][(contextConstraintCount != 0) ? condition.length : 0];
	    p = this;
	    for (int r = 0; r < contextConstraintCount; r++) {
	    	for (int c = 0; c < p.condition.length; c++) {
	    		result[r][c] = (p.parentPart.negativeSubPart != this) ? p.condition[c] : p.condition[c].opposite();
	    	}
	    	p = p.parentPart;
	    }
	    
	    return result;
	}
	
	public boolean isInternalDataConsistent() {
		boolean subPartitionsValid = (positiveSubPart != null) && (zeroSubPart != null) && (negativeSubPart != null)
		                             && ((condition != null) && (parameterCount == condition.length));
		boolean subPartitionsNull = (positiveSubPart == null) && (zeroSubPart == null) && (negativeSubPart == null) 
		                            && ((condition == null) && (parameterCount == -1));
		
		boolean leafValid = (leafContents != null);
		boolean leafNull = (leafContents == null);
		
		return ((leafValid && subPartitionsNull) || (leafNull && subPartitionsValid));
	}
}
