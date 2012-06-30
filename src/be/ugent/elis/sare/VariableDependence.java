package be.ugent.elis.sare;

import be.ugent.elis.sare.domains.Domain;

public class VariableDependence {
   Variable producedVariable = null;
   Variable consumedVariable = null;
   Dependence dependenceTransformation = null;
   Domain producedVariableDependenceDomain = null; // Sub-domain of the produced variable at which this dependence exists
   
   public VariableDependence(Variable iProducedVariable, Variable iConsumedVariable, Dependence iDependenceTransformation,
		                     Domain iProducedVariableDependenceDomain) {
	   producedVariable = iProducedVariable;
	   consumedVariable = iConsumedVariable;
	   
	   dependenceTransformation = iDependenceTransformation;
	   producedVariableDependenceDomain = iProducedVariableDependenceDomain;
   }
   
   public boolean isInternalDataConsistent() {
	   return false; // Not yet implemented
   }
}
