package be.ugent.elis.cg;


import org.jscience.mathematics.functions.ComparatorClass;

import be.elis.ugent.math.Module;
import be.elis.ugent.math.VectorOfModules;
import be.elis.ugent.math.structures.RingComparable;
import be.ugent.elis.AffineTransformation;

public class FastSpacePartitioner<R extends RingComparable<R>> {
	R genericInstance, zero, one;	
	
	RDMGraph<R> cGraph;
	
	public int verbosity = 0;
	
	public Module<R> spacePartitionSpace;
	
	public FastSpacePartitioner(R iGenericInstance, RDMGraph<R> iCGraph, int iVerbosity) {
		genericInstance = iGenericInstance;
		zero = genericInstance.getZero();
		one = genericInstance.getOne();		
		
		cGraph = iCGraph;
		
		verbosity = iVerbosity;
	}
}
