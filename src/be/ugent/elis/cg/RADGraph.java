package be.ugent.elis.cg;


import be.elis.ugent.math.structures.RingComparable;
import be.elis.ugent.graphLibrary.Graph;
import be.ugent.elis.AffineModuleRelation;

public class RADGraph<R extends RingComparable<R>> extends Graph<PolyhedralDomainVertexData<R>, AffineModuleRelation<R>> {
	R _R;
	
	public RADGraph(R i_R) {
		_R = i_R;
	}
	
	@Override protected Graph<PolyhedralDomainVertexData<R>, AffineModuleRelation<R>> newInstance() {
		return new RADGraph<R>(_R);
	}
	
	public RADGraph<R> calcTransitiveClosure() {
		return (RADGraph<R>) floydWarshall(new AffineModuleRelation.AfterOperation<R>(_R),
				 new AffineModuleRelation.ConjunctionOperation<R>(_R));
	}
}
