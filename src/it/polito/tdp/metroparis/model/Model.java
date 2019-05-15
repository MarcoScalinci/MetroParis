 package it.polito.tdp.metroparis.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.EdgeTraversalEvent;
import org.jgrapht.event.TraversalListener;
import org.jgrapht.event.VertexTraversalEvent;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.traverse.*;

import it.polito.tdp.metroparis.db.MetroDAO;

public class Model {
	
//	POSSIAMO CREARE DIRETTAMENTE UNA CLASSE INTERNA AL MODEL COME LISTENER POICHE' ESSA SERVE
//	ESCLUSIVAMENTE AL MODEL E POSSIAMO RENDERLA PRIVATA. IN QUESTO MODO NON AVREMO NEANCHE BISGONO
//	DI PASSARE I DATI DEL MODEL COME PARAMETRI
//	---> possiamo scegliere il modo che preferiamo, averla separata puoo' essere utile per debuggarla
//	--> si potrebbe anche fare un inner class anonima all'interno del metodo in cui ci serve ma non sempre è conveniente  
	
	private class EdgeTraversedGraphListener implements TraversalListener<Fermata,DefaultEdge>{

	@Override
	public void connectedComponentFinished(ConnectedComponentTraversalEvent arg0) {
	}
	@Override
	public void connectedComponentStarted(ConnectedComponentTraversalEvent arg0) {
	}
	@Override
	public void edgeTraversed(EdgeTraversalEvent<DefaultEdge> event) {
		Fermata sourceVertex = grafo.getEdgeSource(event.getEdge());
		Fermata targetVertex = grafo.getEdgeTarget(event.getEdge());
		
		if( !backVisit.containsKey(targetVertex) && backVisit.containsKey(sourceVertex)) {
			backVisit.put(targetVertex, sourceVertex) ;			
		}else if ( !backVisit.containsKey(sourceVertex) && backVisit.containsKey(targetVertex)) {
			backVisit.put(sourceVertex, targetVertex) ;
		}
    }
	@Override
	public void vertexFinished(VertexTraversalEvent<Fermata> arg0) {
	}
	@Override
	public void vertexTraversed(VertexTraversalEvent<Fermata> arg0) {
	}
		
	}
	
	private Graph<Fermata, DefaultEdge> grafo ;
	private List<Fermata> fermate;
    private Map<Integer, Fermata> idMap;
    
/**
 *    inserisco qui la mappa che useremo per creare l'albero di visita
 *    MA LA INSTANZIO direttamente NEL METODO così che ogni volta che viene chiamato
 *    la mappa viene svuotata e ricreata daccapo in base al vertice sorgente che vogliamo usare
 */
   private Map<Fermata , Fermata> backVisit ;

    public Model() {
    	//crea l'oggetto grafo e la mappa
        this.grafo = new SimpleDirectedGraph<>(DefaultEdge.class);
        this.idMap = new HashMap<Integer, Fermata>();
    }
    
	public void creaGrafo() {
	
	// 1--AGGIUNGI I VERTICI
		MetroDAO dao = new MetroDAO();
		this.fermate = dao.getAllFermate();
		Graphs.addAllVertices(grafo, this.fermate);
		
       // -- RIEMPIO IDMAP
		for(Fermata f : this.fermate) {
		   idMap.put(f.getIdFermata(), f) ;
		}
		
		
//   OPZIONE 1 ---> DOPPIO FOR
//		lenta e spesso non funzionante
		
/*		for(Fermata partenza : this.grafo.vertexSet()) {
		  for(Fermata arrivo : this.grafo.vertexSet()) {
			  if(dao.esisteConnessione(partenza, arrivo)) {
				 this.grafo.addEdge(arrivo, partenza);				  
		}}} 
*/
		
/**
  *       OPZIONE 2 : SINGOLO CICLO FOR
  *	      Per ogni oggetto, creaiamo un arco che lo collega a tutti gli altri a cui è collegato
 */
		for(Fermata partenza : this.grafo.vertexSet()) {
			List<Fermata> arrivi = dao.stazioniArrivo(partenza, idMap) ;
			
			for(Fermata arrivo: arrivi)
				grafo.addEdge(partenza, arrivo);		
		}
	
		
	}
	
	public List<Fermata> fermateRaggiungibili(Fermata source){
		
		backVisit=  new HashMap<>();
		
		List<Fermata> result = new LinkedList<Fermata>();
		
		GraphIterator<Fermata, DefaultEdge> it = new BreadthFirstIterator<>(this.grafo);
//		GraphIterator<Fermata, DefaultEdge> it = new DepthFirstIterator<>(this.grafo);
		
		/*
		 * dopo aver creato l'ITERATORE, prima di farlo "partire", aggiungiamo il listener
		 * che abbiamo creato tramite il metodo apposito dell'iteratore
		 * 
		 *NOTA: si fa il NEW della classe apposita che abbiamo creato, quindi attenzione
		 *ad usare il costruttore così come lo abbiamo definito
		 */
		it.addTraversalListener(new Model.EdgeTraversedGraphListener());
		
		
		// bisogna riempire la mappa con il primo elemento, il nodo radice, che abbiamo
		//passato come parametro "source" e che non ha genitore
		backVisit.put(source, null);
		
		while(it.hasNext()) {
			result.add(it.next());
		}
		return result;
	}
	
	public List<Fermata> percorsoFinoA(Fermata target){ 
		//nel caso di visite in ampiezza si può usare il metodo gia' implementato
		//che si chiama .getSpanningTreeEdge che dato un vertice restituisce l'arco(eddge) che lo collega al padre
		// o .getParent per ottenere il vertice parent
		
		if(!backVisit.containsKey(target))
			return null;  // tarrget non raggiungibile dalla source
		
		List<Fermata> percorso= new LinkedList<>();
		Fermata f = target;
		
		while( f!= null) {
		//aggiungiamo sempre in posizione 0 così tutto il resto viene shiftato e 
		//alla fine visualizzeremo il percorso in ordine "da source-->a target"
			percorso.add(0,f);  // partendo dal target, lo aggiungiamo al percorso, poi usando f come chiave sulla mappa
			f= backVisit.get(f);  //ad ogni iterazione con il get otteniamo il "value", a cui corrispone il padre
		}                        //questo finche' non si arriva al vertice di partenza che ha padre "null" per come abbiamo costruito la mappa
		return percorso;
	
	}

	public Graph<Fermata, DefaultEdge> getGrafo() {
		return grafo;
	}

	public List<Fermata> getFermate() {
		return fermate;
	}

}
