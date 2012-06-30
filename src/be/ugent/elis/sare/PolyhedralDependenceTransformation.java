package be.ugent.elis.sare;

import java.util.Vector;

public class PolyhedralDependenceTransformation implements Dependence {
	int embeddingSpaceDimension = -1;
	Vector<int[]> rays = new Vector<int[]>();
	Vector<int[]> lines = new Vector<int[]>();
	
	public PolyhedralDependenceTransformation(int iEmbeddingSpaceDimension) {
		embeddingSpaceDimension = iEmbeddingSpaceDimension;
	}
	
	public boolean isInternalDataConsistent() {
		for (int q = 0; q < rays.size(); q++) {
			if (rays.get(q).length != embeddingSpaceDimension) return false;
		}
		
		for (int q = 0; q < lines.size(); q++) {
			if (lines.get(q).length != embeddingSpaceDimension) return false;
		}
		
		return true;
	}
}
