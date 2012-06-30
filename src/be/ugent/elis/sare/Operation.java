package be.ugent.elis.sare;

public class Operation {
    public VariableDependence[] consumedVariableDependencies = null;
    public Operator operator = null;
    
    public void setOperator(Operator newOperator) {
    	if (newOperator != operator) {
    		newOperator = operator;
    		
    		consumedVariableDependencies 
    			= (operator == null) ? null : new VariableDependence[operator.getNumberOfParameters()];
    	}
    }
    
    public boolean isInternalDataConsistent() {
    	if ((operator == null) ^ (consumedVariableDependencies == null)) return false;
    	
    	if (operator != null) {
    		if (operator.getNumberOfParameters() != consumedVariableDependencies.length) return false;
    	}
    	
    	return true;
    }
}
