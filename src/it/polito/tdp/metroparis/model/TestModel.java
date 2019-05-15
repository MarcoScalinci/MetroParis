package it.polito.tdp.metroparis.model;

import java.util.List;

public class TestModel {

	public static void main(String[] args) {
		
		Model m = new Model();
		
		m.creaGrafo();
		
		System.out.format("creato grafo con %d vertici e %d archi", m.getGrafo().vertexSet().size(), m.getGrafo().edgeSet().size());
		
		Fermata source = m.getFermate().get(0) ;
		System.out.println("parto da "+source+"\n") ;
		List<Fermata> raggiungibili = m.fermateRaggiungibili(source) ;
		System.out.println("fermate raggiunte ("+raggiungibili.size()+") :"+raggiungibili) ;
		

	}

}
