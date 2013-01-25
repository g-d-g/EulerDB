package org.eulerdb.kernel.test;

import org.eulerdb.kernel.EdbTransactionalGraph;
import org.eulerdb.kernel.helper.EdbHelper;
import org.junit.Assert;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.TransactionalGraph.Conclusion;

public class failedCase2 {
	//failed case, cross update
	public static void main(String[] args) {
		EdbTransactionalGraph graph = new EdbTransactionalGraph(
				"./temp/basicTest3");
		graph.startTransaction();
		Edge edge = graph.addEdge(null, graph.addVertex(null),graph.addVertex(null), "test");
		graph.stopTransaction(Conclusion.SUCCESS);
		graph.startTransaction();
		edge.setProperty("transaction-2", "failure");
		Assert.assertEquals("failure", edge.getProperty("transaction-2"));
		graph.stopTransaction(Conclusion.FAILURE);
		System.out.println(edge.getProperty("transaction-2"));
		
		graph.shutdown();
		/*Vertex v1 = graph.addVertex( "1");
        Vertex v2 = graph.addVertex( "2");
        graph.addEdge(null, v1, v2,  "knows");


        graph.removeVertex(v1);
        
        Assert.assertEquals(0, EdbHelper.count(graph.getEdges()));
        
        Assert.assertEquals(1, EdbHelper.count(graph.getVertices()));
        
        Assert.assertEquals(0, EdbHelper.count(v2.getEdges(Direction.IN)));
        
        System.out.print("Done===");*/
		
	}
}
