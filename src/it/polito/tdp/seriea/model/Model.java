package it.polito.tdp.seriea.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polito.tdp.seriea.db.SerieADAO;

public class Model {
	
	List<Team> teams = new ArrayList<>();
	List<Season> seasons = new ArrayList<>();
	
	private Team squadraSelezionata;
	private Map<Season, Integer> puntiClassifica;
	
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
	
}
