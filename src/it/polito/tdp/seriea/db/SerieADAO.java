package it.polito.tdp.seriea.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.seriea.model.Season;
import it.polito.tdp.seriea.model.Team;

public class SerieADAO {

	public List<Season> listAllSeasons() {
		String sql = "SELECT season, description FROM seasons";
		List<Season> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				result.add(new Season(res.getInt("season"), res.getString("description")));
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<Team> listTeams() {
		String sql = "SELECT team FROM teams";
		List<Team> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				result.add(new Team(res.getString("team")));
			}

			conn.close();
			return result;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public void getPuntiClassifica(Season season, Team squadraSelezionata, Map<Season, Integer> puntiClassifica) {
		
		final String sql = "SELECT HomeTeam, AwayTeam, FTR AS result FROM matches WHERE (AwayTeam=? OR HomeTeam=?) AND Season=?";

		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			
			//Set parametri
			String squadra = squadraSelezionata.getTeam();
			st.setString(1, squadra);
			st.setString(2, squadra);
			st.setInt(3, season.getSeason());
			
			ResultSet res = st.executeQuery();
			
			while (res.next()) {
				
				//Primo giro per ogni stagione
				if (!puntiClassifica.containsKey(season)) puntiClassifica.put(season, 0);
				
				if ( res.getString("HomeTeam").equals(squadra) && res.getString("result").equals("H") ) {
					puntiClassifica.replace(season, puntiClassifica.get(season)+3);
				
				if ( res.getString("AwayTeam").equals(squadra) && res.getString("result").equals("A") )
					puntiClassifica.replace(season, puntiClassifica.get(season)+3);
					
				if ( res.getString("result").equals("D"))
					puntiClassifica.replace(season, puntiClassifica.get(season)+1);		
				}
			}

			conn.close();
	
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

}
