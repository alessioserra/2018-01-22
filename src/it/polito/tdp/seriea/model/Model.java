package it.polito.tdp.seriea.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.shortestpath.TreeSingleSourcePathsImpl;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.seriea.db.SerieADAO;

public class Model {
	
	List<Team> teams = new ArrayList<>();
	List<Season> seasons = new ArrayList<>();
	private Team squadraSelezionata;
	private Map<Season, Integer> puntiClassifica;
	
	double bestPeso;
	
	public double getWeight(){
		return bestPeso;
	}
	
	Graph< Season, DefaultWeightedEdge> grafo;
	
	SerieADAO dao;
	
	//Carcico squadre e stagioni 1 sola volta all'inizio
	public Model() {
		dao = new SerieADAO();
		this.teams = dao.listTeams();
		this.seasons = dao.listAllSeasons();
	}

	/**
	 * Popolo menu' a tendina delle squadre
	 * @return
	 */
	public List<Team> getTeams(){
		return teams;
	}
	
	public Map<Season, Integer> getSeasonPoints(Team squadra){
		
		//Inizializzo
		this.squadraSelezionata = squadra;
		puntiClassifica = new HashMap<>();
		
		for ( Season s : this.seasons) {
			dao.getPuntiClassifica(s,this.squadraSelezionata,this.puntiClassifica);
		}
		
		return this.puntiClassifica;
	}
	
	public void creaGrafo() {
		
		grafo = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		
		//Aggiungo vertici
		for (Season s : puntiClassifica.keySet()) {
			if (puntiClassifica.get(s)>0) grafo.addVertex(s);
		}
		
		//Aggiungo archi
		for (Season s1 : this.grafo.vertexSet()) {
			for (Season s2 : this.grafo.vertexSet()) {
				if (!s1.equals(s2)) {
					
					//Se s1 ha più punti di s2 la direzione è s1 -> s2
					if (puntiClassifica.get(s1)>=puntiClassifica.get(s2)) {
						double peso = puntiClassifica.get(s1)-puntiClassifica.get(s2);
						Graphs.addEdge(this.grafo, s1, s2, peso);
					}
					//Altrimenti la direzione è s2->s1
					else {
						double peso = puntiClassifica.get(s2)-puntiClassifica.get(s1);
						Graphs.addEdge(this.grafo, s2, s1, peso);
					}
						
				}
			}
		}
		
		System.out.println("GRAFO CREATO");
		System.out.println("#NODI= "+this.grafo.vertexSet().size());
		System.out.println("#ARCHI= "+this.grafo.edgeSet().size()+"\n");
	}
	
	public Season getBestAnnata() {
		
		Season best = null;
		bestPeso = 0;
		
		for (Season s : this.grafo.vertexSet()) {
			
			double uscenti= 0.0;
			double entranti = 0.0;
			
			//Prendo peso archi uscenti
			for (DefaultWeightedEdge esc : this.grafo.outgoingEdgesOf(s)) uscenti = uscenti + this.grafo.getEdgeWeight(esc);
			
			//Prendo peso archi entranti
			for (DefaultWeightedEdge in : this.grafo.incomingEdgesOf(s)) entranti = entranti + this.grafo.getEdgeWeight(in);
			
			//Calcolo paramentro che mi interessa
			double differenza = entranti - uscenti;
			
			//Se è migliore di quel best la assegno
			if (differenza>=bestPeso) {
				
				best = s;
				bestPeso = differenza;
			}
		}
		
		//Ritorno il miglior best
		return best;
	}
	
}
