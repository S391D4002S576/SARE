package be.ugent.elis.sare.operators;

import be.ugent.elis.sare.Operator;

public class SubstitutionOperator extends Operator {

	@Override
	public Class getDataTypeClass(int parameterIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getNumberOfParameters() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override public String getName() {
		return String.format("Substitution");
	}
}
