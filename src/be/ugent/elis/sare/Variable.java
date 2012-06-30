package be.ugent.elis.sare;

import be.ugent.elis.sare.domains.Domain;

public class Variable {
	public String name = null;
    public Domain computationDomain = null;
    public Operation producingOperation = null;
    int index = -1;
    /**
     * Determines whether this variable already contains data we don't need to calculate.
     * (which implies - it doesn't need a producing operation
     *                - we don't need to schedule it)
     */
    public boolean isReady = false;
    
    public Variable(int iIndex) {
    	index = iIndex;
    }
    
    public int getIndex() {
    	return index;
    }
}
