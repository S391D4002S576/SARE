package be.ugent.elis;

/**
 * @deprecated
 */
public class Rational_Obsolete {
	int numerator = 0;
	int denominator = 0;
	
	public Rational_Obsolete(int iNumerator, int iDenominator) {
		numerator = iNumerator;
		denominator = iDenominator;
	}
	
	public Rational_Obsolete(int integer) {
		numerator = integer;
		denominator = 1;
	}
	
	public void abs() {
		numerator = Math.abs(numerator);
		denominator = Math.abs(denominator);
	}
	
	public static Rational_Obsolete multiply(Rational_Obsolete l, Rational_Obsolete r) {
		return new Rational_Obsolete(l.numerator * r.numerator, l.denominator * r.denominator);
	}
	
	public static Rational_Obsolete multiply(int l, Rational_Obsolete r) {
		return new Rational_Obsolete(l * r.numerator, r.denominator);
	}
	
	public static Rational_Obsolete multiply(Rational_Obsolete l, int r) {
		return new Rational_Obsolete(l.numerator * r, l.denominator);
	}
	
	private int scm(int a, int b) {
		return a*b;
	}
	
	public void add(Rational_Obsolete r) {
		int denScm = scm(denominator, r.denominator);
		
		numerator = ((numerator*denScm)/denominator + (r.numerator*denScm)/r.denominator);
		denominator = denScm;
	}
	
	public Rational_Obsolete clone() {
		return new Rational_Obsolete(numerator, denominator);
	}

	public CharSequence convertToString(int elementLength, boolean includeMinus) {
		String deno = "";
		if (Math.abs(denominator) != 1) deno = String.format("/%d", Math.abs(denominator));
		
		String numb = String.format("%d%s", Math.abs(numerator), deno);
		
		if (includeMinus && ((numerator > 0) ^ (denominator > 0)) && (numerator != 0)) numb = "-" + numb;
		
		while (numb.length() < elementLength) numb = " " + numb;
		
		return numb;
	}
}
