package be.ugent.elis.sare;

public class ConditionalSolutionPartitionTree<S> {
	PartitionCondition condition = null;
	
	public ConditionalSolutionPartitionTree<S> positiveSubPart = null;
	public ConditionalSolutionPartitionTree<S> zeroSubPart = null;
	public ConditionalSolutionPartitionTree<S> negativeSubPart = null;
	
	public S leafContents = null;
	
	ConditionalSolutionPartitionTree<S> parentPart = null;
	
	public ConditionalSolutionPartitionTree(PartitionCondition iCondition, 
               ConditionalSolutionPartitionTree<S> iPositiveS, 
               ConditionalSolutionPartitionTree<S> iZeroS, 
               ConditionalSolutionPartitionTree<S> iNegativeS) {
		condition = iCondition;
		
		positiveSubPart = iPositiveS;
		zeroSubPart = iZeroS;
		negativeSubPart = iNegativeS;
		
		positiveSubPart.parentPart = this;
		zeroSubPart.parentPart = this;
		negativeSubPart.parentPart = this;
	}
	
	public ConditionalSolutionPartitionTree(S iLeafContents) {
		leafContents = iLeafContents;
	}

	public PartitionCondition[] getContextConditions() {
	    int contextConstraintCount = 0;
	    ConditionalSolutionPartitionTree<S> p = null;
	    for (p = this; p.parentPart != null; p = p.parentPart) contextConstraintCount++;
	    
	    PartitionCondition[] result = new PartitionCondition[contextConstraintCount];
	    p = this;
	    for (int r = 0; r < contextConstraintCount; r++) {
    		result[r] = (p.parentPart.negativeSubPart != this) ? p.condition : p.condition.oppose();
	    	p = p.parentPart;
	    }
	    
	    return result;
	}
	
	public boolean isInternalDataConsistent() {
		boolean subPartitionsValid = (positiveSubPart != null) && (zeroSubPart != null) && (negativeSubPart != null);
		boolean subPartitionsNull = (positiveSubPart == null) && (zeroSubPart == null) && (negativeSubPart == null);
		
		boolean leafValid = (leafContents != null);
		boolean leafNull = (leafContents == null);
		
		return ((leafValid && subPartitionsNull) || (leafNull && subPartitionsValid));
	}
}
