package be.ugent.elis.cg;

import be.elis.ugent.math.structures.RingComparable;
import be.ugent.elis.sare.domains.ParametrizedIntegerPolyhederDomain;

public class PolyhedralDomainVertexData<R extends RingComparable<R>> {
	String name;
	ParametrizedIntegerPolyhederDomain<R> domain;
	
	public PolyhedralDomainVertexData(String iName, ParametrizedIntegerPolyhederDomain<R> iDomain) {
		name = iName;
		domain = iDomain;
	}
}
