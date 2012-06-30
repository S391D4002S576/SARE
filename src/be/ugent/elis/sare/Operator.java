package be.ugent.elis.sare;

public abstract class Operator {
	public abstract int getNumberOfParameters();
	
	public abstract Class getDataTypeClass(int parameterIndex);
	
	public abstract String getName();
}
