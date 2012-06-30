package be.ugent.elis.sare;

import org.jscience.mathematics.numbers.Rational;
import org.jscience.mathematics.structures.Field_;
import org.jscience.mathematics.vectors.DenseMatrix;
import org.jscience.mathematics.vectors.Matrix;

import be.ugent.elis.AffineTransformation;
import be.ugent.elis.piplibnative.PipNewParam;
import be.ugent.elis.piplibnative.PipQuast;
import be.ugent.elis.piplibnative.PipVector;

public class PolyhedralPartitionAffineTransformation<F extends Field_<F>> extends PolyhedralPartition<AffineTransformation<F>> {
	F zero = null;
	F one = null;
	
	public PolyhedralPartitionAffineTransformation(Rational[] iCondition, PolyhedralPartition<AffineTransformation<F>> iPositiveS, 
            PolyhedralPartition<AffineTransformation<F>> iZeroS, PolyhedralPartition<AffineTransformation<F>> iNegativeS, 
            F iZero, F iOne) {
		super(iCondition, iPositiveS, iZeroS, iNegativeS);

		zero = iZero;
		one = iOne;
	}

	public PolyhedralPartitionAffineTransformation(AffineTransformation<F> iLeafContents, F iZero, F iOne) {
		super(iLeafContents);

		zero = iZero;
		one = iOne;
	}
	
	public static <F extends Field_<F>> PolyhedralPartitionAffineTransformation<F> newAffinePartitionAffineTransformation(
			AffineTransformation<F> solution, Rational[] condition, PolyhedralPartitionAffineTransformation<F> positiveSubPart, 
			PolyhedralPartitionAffineTransformation<F> zeroSubPart, PolyhedralPartitionAffineTransformation<F> negativeSubPart, 
			F iZero, F iOne) {
		if (solution != null) {
			return new PolyhedralPartitionAffineTransformation<F>(condition, positiveSubPart, zeroSubPart, negativeSubPart, iZero, iOne);
		} else {
			return new PolyhedralPartitionAffineTransformation<F>(solution, iZero, iOne);
		}
	}

	public PolyhedralPartitionAffineTransformation<F> getPositiveSubPart() {
		return (PolyhedralPartitionAffineTransformation<F>) positiveSubPart;
	}
	
	public PolyhedralPartitionAffineTransformation<F> getNegativeSubPart() {
		return (PolyhedralPartitionAffineTransformation<F>) negativeSubPart;
	}
	
	public PolyhedralPartitionAffineTransformation<F> getZeroSubPart() {
		return (PolyhedralPartitionAffineTransformation<F>) zeroSubPart;
	}
	
	public static PolyhedralPartitionAffineTransformation<Rational> fromPipQuast(PipQuast quast) {
		if (quast.conditionVector != null) { // Split in subsections
			Rational[] condition = new Rational[quast.conditionVector.getLength()];
			for (int q = 0; q < condition.length; q++) {
				condition[q] = Rational.valueOf(quast.conditionVector.numerators[q], quast.conditionVector.denominators[q]);
			}
			
			PolyhedralPartitionAffineTransformation<Rational> thenAT 
				= PolyhedralPartitionAffineTransformation.fromPipQuast(quast.thenQuast);
			PolyhedralPartitionAffineTransformation<Rational> elseAT 
				= PolyhedralPartitionAffineTransformation.fromPipQuast(quast.elseQuast);
			
			return new PolyhedralPartitionAffineTransformation<Rational>(condition, thenAT, thenAT, elseAT, Rational.ZERO, Rational.ONE);
		} else {
			Rational[][] elements = new Rational[quast.solution.length][(quast.solution.length > 0) ? quast.solution[0].getLength() : 0];
			
			for (int u = 0; u < quast.solution.length; u++) {
				PipVector solVector = quast.solution[u];
				for (int p = 0; p < elements[u].length; p++) {
					elements[u][p] = Rational.valueOf(solVector.numerators[p], solVector.denominators[p]);
				}
			}
			/*int homogeneousRow = elements.length - 1;
			for (int p = 0; p < elements[homogeneousRow].length; p++) {
				elements[homogeneousRow][p] = (p == elements[homogeneousRow].length - 1) ? Rational.ONE : Rational.ZERO;
			}	*/		
			
			AffineTransformation<Rational> transformation
				= new AffineTransformation<Rational>(DenseMatrix.valueOf(elements), Rational.ZERO, Rational.ONE);
			
			return new PolyhedralPartitionAffineTransformation<Rational>(transformation, Rational.ZERO, Rational.ONE);
		}
	}
	
	public String convertToString(String[] parameterNames, String[] unknownNames) {
		StringBuilder sb = new StringBuilder();
		
		boolean firstTerm = true;
		for (int d = 0; d <= leafContents.getDestinationDimension(); d++) {
			boolean offset = (d == leafContents.getDestinationDimension());
			String currentDestinationTransform = leafContents.getTransformedIndexesAsTextForDestinationDimension(d, parameterNames);
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
	
	public void leftMultiplyTransformationsBy(Matrix<F> leftFactor) {
		if (leafContents != null) {
			leafContents = leafContents.leftMultiply(leftFactor);
		}
		
		if (getPositiveSubPart() != null) getPositiveSubPart().leftMultiplyTransformationsBy(leftFactor);
		if (getZeroSubPart() != null) getZeroSubPart().leftMultiplyTransformationsBy(leftFactor);
		if (getNegativeSubPart() != null) getNegativeSubPart().leftMultiplyTransformationsBy(leftFactor);
	}

	public PolyhedralPartitionAffineTransformation<F> subSolutionSpace(int[] rows) {
		F[][] nSolutionElms = null;
		if (leafContents != null) {
			nSolutionElms = (F[][]) new Field_[rows.length][leafContents.transformationMatrix.getNumberOfColumns()];
			for (int q = 0; q < rows.length; q++) {
				for (int c = 0; c < leafContents.transformationMatrix.getNumberOfColumns(); c++) {
					nSolutionElms[q][c] = leafContents.transformationMatrix.get(q, c);
				}
			}
		}
		AffineTransformation<F> nSolution = new AffineTransformation<F>(DenseMatrix.valueOf(nSolutionElms), zero, one);
		
		Rational[] nCondition = (condition != null) ? condition.clone() : null;
		
		PolyhedralPartitionAffineTransformation<F> nPositiveSubPart 
		= (getPositiveSubPart() != null) ? getPositiveSubPart().subSolutionSpace(rows) : null;
		PolyhedralPartitionAffineTransformation<F> nZeroSubPart 
		= (getZeroSubPart() != null) ? getZeroSubPart().subSolutionSpace(rows) : null;
		PolyhedralPartitionAffineTransformation<F> nNegativeSubPart 
		= (getNegativeSubPart() != null) ? getNegativeSubPart().subSolutionSpace(rows) : null;
		
		return newAffinePartitionAffineTransformation(nSolution, nCondition, nPositiveSubPart, nZeroSubPart, nNegativeSubPart, zero, one);
	}

	public PolyhedralPartitionAffineTransformation<F> subSolutionSpace(int rangeOffset, int rangeLength) {
    	int[] unknowns = new int[rangeLength];
    	for (int q = 0; q < rangeLength; q++) unknowns[q] = rangeOffset + q;
	    	
    	return subSolutionSpace(unknowns);
	}
}
