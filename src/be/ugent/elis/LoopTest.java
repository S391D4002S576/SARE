package be.ugent.elis;

import java.awt.Color;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import be.elis.ugent.graphLibrary.Edge;
import be.elis.ugent.graphLibrary.Graph;
import be.elis.ugent.graphLibrary.Vertex;
import be.ugent.rnaligner.CSVGGraphics;

public class LoopTest {
	static int xC = 25;
	static int yC = 25;

	static int w = 50000;
	static int h = 50000;
	static int CellDim = w / (2*xC);
	static int CellRadius = ((60 * CellDim) / 100);
	static int CellStrokeWidth = ((20 * CellRadius) / 100);

	static DataOutputStream DOS;
	static CSVGGraphics svg;
	
	static class VD {
		int[] iterationVector;
		int x;
		int y;
		
		VD(int ... iIterationVector) {
			iterationVector = iIterationVector.clone();
			x = (2*(iterationVector[0] + xC/2) + 1)*CellDim;
			y = (2*(iterationVector[1] + yC/2) + 1)*CellDim;
		}
	}
	
	static class ED {
		
	}
	
	static class G extends Graph<VD, ED> {
		public void draw() {
			float Hue = 0.5f, 
			      Saturation = 0.55f, 
			      Brightness = 0.95f;
			svg.StrokeWidth = CellStrokeWidth;
			
			for (int x = 0; x < getEdgeCount(); x++) { Edge<ED> e = getEdge(x);
				VD pD = (VD) e.getFromVertex().data;
				VD qD = (VD) e.getToVertex().data;
				Hue = 0.7f;
				
				svg.FillColor = Color.HSBtoRGB(Hue, Saturation, Brightness);
				svg.Line(pD.x, pD.y, qD.x, qD.y);
			}
			
			for (int x = 0; x < getVertexCount(); x++) { Vertex<VD> p = getVertex(x);
				Hue = 0.7f;
				
				svg.FillColor = Color.HSBtoRGB(Hue, Saturation, Brightness);
			 	svg.Circle(p.data.x, p.data.y, CellRadius);
			}
		}
	}
	
	public static void main(String[] args) throws IOException {
		G g = new G();
		
		for (int i = 0; i < xC; i++) { 
			for (int j = 0; j < yC; j++) {
				g.addVertex(new VD(i, j));
			}
		}
		
		for (int x = 0; x < g.getVertexCount(); x++) { Vertex<VD> p = g.getVertex(x);
			for (int y = 0; y < g.getVertexCount(); y++) { Vertex<VD> q = g.getVertex(y);
				if (x != y) {
					int pI = p.data.iterationVector[0];
					int pJ = p.data.iterationVector[1];
					int qI = q.data.iterationVector[0];
					int qJ = q.data.iterationVector[1];
					
					if (1*pI - 5*pJ == 1*qI - 5*qJ) {
						g.addEdge(p, q, new ED());
					}
					
					if (1*pI + 1*pJ == 1*qI + 1*qJ) {
						g.addEdge(p, q, new ED());
					}
				}
			}
		}

		//
		G gReflSpaceA = new G();
		
		for (int i = 0; i < xC; i++) { 
			for (int j = 0; j < yC; j++) {
				gReflSpaceA.addVertex(new VD(i - xC/2, j - yC/2));
			}
		}
		
		for (int x = 0; x < gReflSpaceA.getVertexCount(); x++) { Vertex<VD> p = gReflSpaceA.getVertex(x);
			for (int y = 0; y < gReflSpaceA.getVertexCount(); y++) { Vertex<VD> q = gReflSpaceA.getVertex(y);
				if (x != y) {
					int pI = p.data.iterationVector[0];
					int pJ = p.data.iterationVector[1];
					int qI = q.data.iterationVector[0];
					int qJ = q.data.iterationVector[1];
					
					if ((1*pI - 5*pJ == 1*qI - 5*qJ) && (1*pI - 5*pJ == 0)) {
						gReflSpaceA.addEdge(p, q, new ED());
					}
					
					if ((1*pI + 1*pJ == 1*qI + 1*qJ) && (1*pI + 1*pJ == 0)) {
						gReflSpaceA.addEdge(p, q, new ED());
					}
				}
			}
		}
		
		DOS = new DataOutputStream(new FileOutputStream("test.svg"));
		
		svg = new CSVGGraphics(w, h);
		
		//g.draw();
		gReflSpaceA.draw();
		
		DOS.writeBytes(svg.toString());	
	}

}
