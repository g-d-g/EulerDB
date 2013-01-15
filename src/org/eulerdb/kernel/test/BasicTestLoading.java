package org.eulerdb.kernel.test;

import org.eulerdb.kernel.EdbGraph;
import org.eulerdb.kernel.EdbVertex;
import org.eulerdb.kernel.helper.FileHelper;

import com.tinkerpop.blueprints.Vertex;

public class BasicTestLoading {

	public static void main(String[] args) {
		
		String path = "./temp2";
		
		System.out.println("loading a graph with 1m vertices");
		
		EdbGraph g =  new EdbGraph(path);

		System.out.println("All nodes:");
		/*for (Vertex v : g.getVertices()) {
			System.out.println(v.getId() + " : ");
			System.out.println("Who he like :"+((EdbVertex) v).getOut("likes"));
			System.out.println("Who hates him :"+((EdbVertex) v).getIn("hates"));
		}*/
		System.out.println(g.getVertex(10000).getId());
	}
}
