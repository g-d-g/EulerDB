package org.eulerdb.kernel;

import java.util.Set;

import org.eulerdb.kernel.iterator.EdbIterableFromDatabase;
import org.eulerdb.kernel.iterator.EdbSecondaryCursor;
import org.eulerdb.kernel.storage.EdbStorage.storeType;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.KeyIndexableGraph;
import com.tinkerpop.blueprints.Vertex;

/*
 * KeyIndexableGraph: auto index base on the property name
 * IndexableGraph: manual index, user needs to put into the vertex by themselves 
 */

public class EdbKeyIndexableGraph extends EdbGraph implements KeyIndexableGraph {
	
	static {
		FEATURES.supportsKeyIndices = true;
	}

	public EdbKeyIndexableGraph(String path) {
		super(path, false, true);
		logger.info("EulerDB is running in mode transactional: false, autoindex: true at path:" + path);
	}

	EdbKeyIndexableGraph(String path, boolean isTransactional,
			boolean autoIndex) {
		super(path, isTransactional, autoIndex);
		logger.info("EulerDB is running in mode transactional: "+isTransactional+", autoindex: "+ autoIndex+"at path:" + path);
	}

	@Override
	public <T extends Element> void createKeyIndex(String key, Class<T> type) {
		logger.debug("createKeyIndex for "+ type.getCanonicalName()+" of key "+ key);
		if (type.equals(Vertex.class) || type.equals(EdbVertex.class)) {
			mStorage.createSecondaryIfNeed(storeType.NODEPROPERTY,null, key);
		} else if (type.equals(Edge.class) || type.equals(EdbEdge.class)) {
			mStorage.createSecondaryIfNeed(storeType.EDGEPROPERTY,null, key);
		}
	}

	@Override
	public <T extends Element> void dropKeyIndex(String key, Class<T> type) {
		logger.debug("dropkey for "+ type.getCanonicalName()+" of key "+ key);
		if (type.equals(Vertex.class) || type.equals(EdbVertex.class)) {
			mStorage.deleteSecondary(storeType.NODEPROPERTY, mStorage.getTransaction(),
					key);
		} else if (type.equals(Edge.class) || type.equals(EdbEdge.class)) {
			mStorage.deleteSecondary(storeType.EDGEPROPERTY, mStorage.getTransaction(),
					key);
		}

	}

	@Override
	public <T extends Element> Set<String> getIndexedKeys(Class<T> type) {
		logger.debug("getIndexedKeys");
		if (type.equals(Vertex.class) || type.equals(EdbVertex.class)) {
			return mStorage.getKeys(storeType.NODEPROPERTY);
		} else if (type.equals(Edge.class) || type.equals(EdbEdge.class)) {
			return mStorage.getKeys(storeType.EDGEPROPERTY);
		}
		return null;
	}

	/**
	 * we are overriding the get vertices here to make use of the index
	 */
	@Override
	public Iterable<Vertex> getVertices(String arg0, Object arg1) {
		final String key = arg0;
		final Object value = arg1;
		
		if(!mAutoIndex||!mStorage.containsIndex(storeType.NODEPROPERTY, key)) return super.getVertices(arg0,arg1);
		
		logger.debug("Using index to get Vertices with property name  "+ key+" of value "+value);

		Function<String, Vertex> idToObject = new Function<String, Vertex>() {
			public Vertex apply(String id) {
				return (Vertex) mStorage.getObj(storeType.VERTEX,
						mStorage.getTransaction(), id);
			}
		};

		Iterable<String> id = new EdbIterableFromDatabase(
				mStorage.getSecondaryCursor(storeType.NODEPROPERTY, mStorage.getTransaction(), key, value));
		Iterable<Vertex> eit = (Iterable<Vertex>) Iterables.transform(id,
				idToObject);
		return eit;
	}

	/**
	 * we are overriding the get Edges here to make use of the index
	 */
	@Override
	public Iterable<Edge> getEdges(String arg0, Object arg1) {
		String key = arg0;
		Object value = arg1;
		if(!mAutoIndex||!mStorage.containsIndex(storeType.EDGEPROPERTY, key)) return super.getEdges(arg0,arg1);
		
		logger.debug("Using index to get Edges with property name  "+ key+" of value "+value);
		

		Function<String, Edge> idToObject = new Function<String, Edge>() {
			public Edge apply(String id) {
				return (Edge) mStorage.getObj(storeType.EDGE, mStorage.getTransaction(),
						id);
			}
		};

		Iterable<String> id = (Iterable<String>) new EdbIterableFromDatabase(
				mStorage.getSecondaryCursor(storeType.EDGEPROPERTY, mStorage.getTransaction(), key, value));
		;
		Iterable<Edge> eit = (Iterable<Edge>) Iterables.transform(id,
				idToObject);

		return eit;
	}

}
