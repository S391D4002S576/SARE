package be.ugent.elis.sare.domains;

import org.jscience.mathematics.numbers.Rational;

public class ParameterPartition<S> {
	int parameterCount = -1;
	Rational[] condition = null;
	
	ParameterPartition<S> positiveS = null;
	ParameterPartition<S> zeroS = null;
	ParameterPartition<S> negativeS = null;
	
	S leafContents = null;
	
	public ParameterPartition(int iParameterCount, Rational[] iCondition,
			                  ParameterPartition<S> iPositiveS, ParameterPartition<S> iZeroS, 
			                  ParameterPartition<S> iNegativeS) {
		parameterCount = iParameterCount;
		condition = iCondition;
		
		positiveS = iPositiveS;
		zeroS = iZeroS;
		negativeS = iNegativeS;
	}
	
	public ParameterPartition(S iLeafContents) {
		leafContents = iLeafContents;
	}
	
	public boolean isInternalDataConsistent() {
		boolean subPartitionsValid = (positiveS != null) && (zeroS != null) && (negativeS != null)
		                             && ((condition != null) && (parameterCount == condition.length));
		boolean subPartitionsNull = (positiveS == null) && (zeroS == null) && (negativeS == null) 
		                            && ((condition == null) && (parameterCount == -1));
		
		boolean leafValid = (leafContents != null);
		boolean leafNull = (leafContents == null);
		
		return ((leafValid && subPartitionsNull) || (leafNull && subPartitionsValid));
	}
}
