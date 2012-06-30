package be.ugent.elis.sare.operators;

import be.ugent.elis.sare.Operator;
import be.ugent.elis.sare.datatypes.IntegerDataType;

/**
 * @author Sven De Smet
 * 
 * Generic Viterbi Operator: Result = EmissionScore + Max i : Predecessors . PredecessorScore_i + TransmissionScore_i
 * Parameter 0: EmissionScore
 * Parameter 1 + 2*i: PredecessorScore_i 
 * Parameter 1 + 2*i + 1: TransmissionScore_i 
 */
public class ViterbiOperator extends Operator {
	int predecessorCount = -1;

	public ViterbiOperator(int iPredecessors) {
		assert (iPredecessors >= 0);
		
		predecessorCount = iPredecessors;
	}
	
	@Override public int getNumberOfParameters() {
		return (2*predecessorCount + 1);
	}

	@Override public Class getDataTypeClass(int parameterIndex) {
		return IntegerDataType.class;
	}

	@Override public String getName() {
		return String.format("Viterbi_{%d}", predecessorCount);
	}
}
