package be.ugent.elis.sare;

import org.jscience.mathematics.numbers.Rational;
import org.jscience.mathematics.vectors.DenseMatrix;
import org.jscience.mathematics.vectors.Matrix;

import be.ugent.elis.AffineTransformation;
import be.ugent.elis.piplibnative.PipQuast;
import be.ugent.elis.piplibnative.PipVector;

public class ParameterQuastAffineTransformation {
	int parameterCount = -1;
	int unknownCount = -1;
	Rational[] condition = null;
	ParameterQuastAffineTransformation thenPqat = null;
	ParameterQuastAffineTransformation elsePqat = null;
	
	public Matrix<Rational> transformation = null;
	
	public ParameterQuastAffineTransformation(PipQuast quast, int iParameterCount, int iUnknownCount) {
		parameterCount = iParameterCount;
		unknownCount = iUnknownCount;
		if (quast.conditionVector != null) { // Split in subsections
			assert (quast.conditionVector.getLength() == parameterCount + 1);
			
			condition = new Rational[quast.conditionVector.getLength()];
			for (int q = 0; q < condition.length; q++) {
				condition[q] = Rational.valueOf(quast.conditionVector.numerators[q], quast.conditionVector.denominators[q]);
			}
			
			thenPqat = new ParameterQuastAffineTransformation(quast.thenQuast, parameterCount, unknownCount);
			elsePqat = new ParameterQuastAffineTransformation(quast.elseQuast, parameterCount, unknownCount);
		} else {
			assert (quast.solution.length == unknownCount);
			
			Rational[][] elements = new Rational[unknownCount][parameterCount + 1];
			for (int u = 0; u < unknownCount; u++) {
				PipVector solVector = quast.solution[u];
				assert (solVector.getLength() == parameterCount + 1);
				
				for (int p = 0; p <= parameterCount; p++) {
					elements[u][p] = Rational.valueOf(solVector.numerators[p], solVector.denominators[p]);
				}
			}
			transformation = DenseMatrix.valueOf(elements);
		}
	}
	
	public String convertToString(String[] parameterNames, String[] unknownNames) {
		AffineTransformation<Rational> atr = new AffineTransformation<Rational>(transformation, Rational.ZERO, Rational.ONE);
		
		StringBuilder sb = new StringBuilder();
		
		boolean firstTerm = true;
		for (int d = 0; d <= atr.getDestinationDimension(); d++) {
			boolean offset = (d == atr.getDestinationDimension());
			String currentDestinationTransform = atr.getTransformedIndexesAsTextForDestinationDimension(d, parameterNames);
			if (!currentDestinationTransform.equals("0") || (firstTerm && offset)) {
				if (!firstTerm) sb.append(" + ");
				
				if (!currentDestinationTransform.equals("1") || offset) {
					sb.append((offset && firstTerm) ? currentDestinationTransform : ("(" + currentDestinationTransform + ")"));
				}
				if (!offset) sb.append(unknownNames[d]);
				firstTerm = false;
			}
		}
		
		return sb.toString();
	}
	
	public void leftMultiplyTransformationsBy(Matrix<Rational> leftFactor) {
		if (transformation != null) {
			transformation = ((DenseMatrix) leftFactor).times(transformation, Rational.ZERO);
		}
		
		if (thenPqat != null) thenPqat.leftMultiplyTransformationsBy(leftFactor);
		if (elsePqat != null) elsePqat.leftMultiplyTransformationsBy(leftFactor);
	}
}
