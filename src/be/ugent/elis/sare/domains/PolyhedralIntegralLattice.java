package be.ugent.elis.sare.domains;

import org.jscience.mathematics.structures.Field_;
import org.jscience.mathematics.vectors.Matrix;

public interface PolyhedralIntegralLattice<F extends Field_<F>> {
	public int getConstraintCount();
	
	public int getParameterCount();
	public int getParameterConstraintCount();
	
	public int getDimension();
	
	public Matrix<F> getDomainConstraintMatrix();
}
