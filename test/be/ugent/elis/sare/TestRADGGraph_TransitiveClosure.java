package be.ugent.elis.sare;

import java.util.Vector;

import org.jscience.mathematics.numbers.LargeInteger;

import be.elis.ugent.graphLibrary.Edge;
import be.elis.ugent.graphLibrary.Graph;
import be.elis.ugent.graphLibrary.Vertex;
import be.elis.ugent.math.Vect;
import be.ugent.elis.AffineModuleRelation;
import be.ugent.elis.align.TestCaseExtended;
import be.ugent.elis.cg.PolyhedralDomainVertexData;
import be.ugent.elis.cg.RADGraph;
import be.ugent.elis.graphLibrary.GraphTesting;

public class TestRADGGraph_TransitiveClosure extends GraphTesting {
	LargeInteger _li = LargeInteger.valueOf(1);
	
	/*
	 * Not much symmetry testing here
	 */
	public void test_1() {
		RADGraph<LargeInteger> g = new RADGraph<LargeInteger>(_li);
		
		Vertex<PolyhedralDomainVertexData<LargeInteger>>[] v 
			= (Vertex<PolyhedralDomainVertexData<LargeInteger>>[]) new Vertex<?>[4];
		for (int q = 0; q < v.length; q++) v[q] = g.addVertex(new PolyhedralDomainVertexData<LargeInteger>(null, null));
		
		Vector<Vect<LargeInteger>> vA = new Vector<Vect<LargeInteger>>();
		Vector<Vect<LargeInteger>> vB = new Vector<Vect<LargeInteger>>();
		Vector<Vect<LargeInteger>> vC = new Vector<Vect<LargeInteger>>();
		Vector<Vect<LargeInteger>> vAB = new Vector<Vect<LargeInteger>>();
		Vector<Vect<LargeInteger>> vBC = new Vector<Vect<LargeInteger>>();
		Vector<Vect<LargeInteger>> vAC = new Vector<Vect<LargeInteger>>();
		
		vA.add(TestFuncs_Polyhedral.getVectLargeInteger(new int[]{1, 0,  0,  1, 11,  7}));
		vA.add(TestFuncs_Polyhedral.getVectLargeInteger(new int[]{0, 1,  0,  0,  3,  5}));
		vA.add(TestFuncs_Polyhedral.getVectLargeInteger(new int[]{0, 0,  1,  0,  9, 13}));
		
		vB.add(TestFuncs_Polyhedral.getVectLargeInteger(new int[]{1, 0,  0, -1, 78,  9}));
		vB.add(TestFuncs_Polyhedral.getVectLargeInteger(new int[]{0, 1, -1,  0, 17, -6}));
		
		vC.add(TestFuncs_Polyhedral.getVectLargeInteger(new int[]{-1, -2,  0, -1,  5,  7,  2}));
		
		// Expected result of B ° A
		vAB.add(TestFuncs_Polyhedral.getVectLargeInteger(new int[]{0,  0,   0, -1, 87, 22}));
		vAB.add(TestFuncs_Polyhedral.getVectLargeInteger(new int[]{1,  0,  -1,  0, 28,  1}));
		vAB.add(TestFuncs_Polyhedral.getVectLargeInteger(new int[]{0,  1,   0,  0,  3,  5}));
		
		// Expected result of C ° B
		vBC.add(TestFuncs_Polyhedral.getVectLargeInteger(new int[]{2,  1,   0, -1,  5, 180, 14}));
		
		// Expected result of C ° B ° A
		vAC.add(TestFuncs_Polyhedral.getVectLargeInteger(new int[]{1,  0,   0, -1,  5, 209, 47}));
		vAC.add(TestFuncs_Polyhedral.getVectLargeInteger(new int[]{0,  1,   0,  0,  0,   3,  5}));
		
		AffineModuleRelation<LargeInteger> mA = new AffineModuleRelation<LargeInteger>(_li, vA, 2, 2, 2);
		AffineModuleRelation<LargeInteger> mB = new AffineModuleRelation<LargeInteger>(_li, vB, 2, 2, 2);
		AffineModuleRelation<LargeInteger> mC = new AffineModuleRelation<LargeInteger>(_li, vC, 2, 3, 2);
		AffineModuleRelation<LargeInteger> mAB = new AffineModuleRelation<LargeInteger>(_li, vAB, 2, 2, 2);
		AffineModuleRelation<LargeInteger> mBC = new AffineModuleRelation<LargeInteger>(_li, vBC, 2, 3, 2);
		AffineModuleRelation<LargeInteger> mABC = new AffineModuleRelation<LargeInteger>(_li, vAC, 2, 3, 2);
		
		g.addEdge(v[0], v[1], mA);
		g.addEdge(v[1], v[2], mB);
		g.addEdge(v[2], v[3], mC);
		
		RADGraph<LargeInteger> radg = g.calcTransitiveClosure();
		
		Vertex<PolyhedralDomainVertexData<LargeInteger>>[] w 
			= (Vertex<PolyhedralDomainVertexData<LargeInteger>>[]) new Vertex<?>[4];
		for (int q = 0; q < v.length; q++) w[q] = radg.getVertex(q);
		
		Vector<Edge<AffineModuleRelation<LargeInteger>>> f12 = radg.getEdges(w[0], w[1]);
		assert (f12.size() == 1) && (f12.get(0).data.equals(mA));
		
		Vector<Edge<AffineModuleRelation<LargeInteger>>> f21 = radg.getEdges(w[1], w[0]);
		assert (f21.size() == 0);
		
		Vector<Edge<AffineModuleRelation<LargeInteger>>> f23 = radg.getEdges(w[1], w[2]);
		assert (f23.size() == 1) && (f23.get(0).data.equals(mB));
		
		Vector<Edge<AffineModuleRelation<LargeInteger>>> f32 = radg.getEdges(w[2], w[1]);
		assert (f32.size() == 0);
		
		Vector<Edge<AffineModuleRelation<LargeInteger>>> f13 = radg.getEdges(w[0], w[2]);
		assert (f13.size() == 1) && (f13.get(0).data.equals(mAB));
		
		Vector<Edge<AffineModuleRelation<LargeInteger>>> f24 = radg.getEdges(w[1], w[3]);
		assert (f24.size() == 1) && (f24.get(0).data.equals(mBC));
		
		Vector<Edge<AffineModuleRelation<LargeInteger>>> f14 = radg.getEdges(w[0], w[3]);
		assert (f14.size() == 1) && (f14.get(0).data.equals(mABC));
	}

}
