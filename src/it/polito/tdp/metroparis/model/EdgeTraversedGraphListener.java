package it.polito.tdp.metroparis.model;

import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.EdgeTraversalEvent;
import org.jgrapht.event.TraversalListener;
import org.jgrapht.event.VertexTraversalEvent;
import org.jgrapht.graph.DefaultEdge;

public class EdgeTraversedGraphListener implements TraversalListener<Fermata,DefaultEdge> {

//	---------AGGIUNGIAMO MAPPA
//	INSERIAMO LA MAPPA COME ATTRIBUTO E NON COME PARAMETRO DEL METODO INTERESSATO POICHE'
//	IN OVVERRIDE. QUESTA MAPPA SARA' RIEMPITA CON COPPIE DI VERTICI COLLEGATE ALL'INTERNO DEL
//	 CAMMINO CHE VOGLIAMO TRACCIARE
	
	Map<Fermata , Fermata> back ; 
	
//  -----AGGIUNGIAMO UN GRAFO
//	il listener per prendere le informazioni sugli archi ha bisogno di conoscere il grafo
//	per questo dobbiamo aggiungerglielo noi come attributo e passarlo nel costruttore esattamente
//	come con la mappa di backtrack
	
	Graph<Fermata,DefaultEdge> grafo ;
	
	public EdgeTraversedGraphListener(Graph<Fermata,DefaultEdge> grafo, Map<Fermata, Fermata> back) {
		this.grafo = grafo ;
		this.back = back ;
	}
	
	@Override
	public void connectedComponentFinished(ConnectedComponentTraversalEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void connectedComponentStarted(ConnectedComponentTraversalEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	// a  noi in questo caso interessava questo quindi gli altri non gli implementiamo
	@Override
	public void edgeTraversed(EdgeTraversalEvent<DefaultEdge> event) {

		/*
		 *  BACK coodifica le relazioni di tipo child ---> parent
		 *  
		 *  per un nuovo vertice CHILD scoperto devo avere che:
		 *  - child è ancora sconosciuto (non ancora visitato)
		 *  - parent è gia stato visitato 
		 *  
		 *  Una volta passato il grafo, usiamo i metodi di graph per ottenere i vertici sorgente e destinazione
		 */
		
		Fermata sourceVertex = grafo.getEdgeSource(event.getEdge());
		Fermata targetVertex = grafo.getEdgeTarget(event.getEdge());
		
//		--> se il grafo è orientato, allora SOURCE== PARENT e TARGET== CHILD
//		--> se il grafo non è orientato, allora potrebbe essere anche il contrario..
		
		if( !back.containsKey(targetVertex) && back.containsKey(sourceVertex)) {
			back.put(targetVertex, sourceVertex) ;			
		}else if ( !back.containsKey(sourceVertex) && back.containsKey(targetVertex)) {
			back.put(sourceVertex, targetVertex) ;
		}
		
	}



	@Override
	public void vertexFinished(VertexTraversalEvent<Fermata> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void vertexTraversed(VertexTraversalEvent<Fermata> arg0) {
		// TODO Auto-generated method stub
		
	}

	
}
