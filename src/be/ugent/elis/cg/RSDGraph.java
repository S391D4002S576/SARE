package be.ugent.elis.cg;


import be.elis.ugent.math.structures.RingComparable;
import be.elis.ugent.graphLibrary.Graph;
import be.ugent.elis.AffineModuleRelation;

public class RSDGraph<R extends RingComparable<R>> extends RADGraph<R> {
	public RSDGraph(R i_R) { super(i_R); }
	
	@Override protected RSDGraph<R> newInstance() {
		return new RSDGraph<R>(_R);
	}
}
