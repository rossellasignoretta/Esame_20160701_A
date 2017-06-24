package it.polito.tdp.formulaone.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.formulaone.model.Circuit;
import it.polito.tdp.formulaone.model.Constructor;
import it.polito.tdp.formulaone.model.Driver;
import it.polito.tdp.formulaone.model.Season;


public class FormulaOneDAO {

	public List<Integer> getAllYearsOfRace() {
		
		String sql = "SELECT year FROM races ORDER BY year" ;
		
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			ResultSet rs = st.executeQuery() ;
			
			List<Integer> list = new ArrayList<>() ;
			while(rs.next()) {
				list.add(rs.getInt("year"));
			}
			
			conn.close();
			return list ;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Query Error");
		}
	}
	
	public List<Season> getAllSeasons() {
		
		String sql = "SELECT year, url FROM seasons ORDER BY year" ;
		
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			ResultSet rs = st.executeQuery() ;
			
			List<Season> list = new ArrayList<>() ;
			while(rs.next()) {
				list.add(new Season(Year.of(rs.getInt("year")), rs.getString("url"))) ;
			}
			
			conn.close();
			return list ;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}
	
	public List<Circuit> getAllCircuits() {

		String sql = "SELECT circuitId, name FROM circuits ORDER BY name";

		try {
			Connection conn = DBConnect.getConnection();

			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet rs = st.executeQuery();

			List<Circuit> list = new ArrayList<>();
			while (rs.next()) {
				list.add(new Circuit(rs.getInt("circuitId"), rs.getString("name")));
			}

			conn.close();
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Query Error");
		}
	}
	
	public List<Constructor> getAllConstructors() {

		String sql = "SELECT constructorId, name FROM constructors ORDER BY name";

		try {
			Connection conn = DBConnect.getConnection();

			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet rs = st.executeQuery();

			List<Constructor> constructors = new ArrayList<>();
			while (rs.next()) {
				constructors.add(new Constructor(rs.getInt("constructorId"), rs.getString("name")));
			}

			conn.close();
			return constructors;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Query Error");
		}
	}
	
	/*
	 * Restiuisce la lista di piloti che hanno gareggiato nella stagione season 
	 */
	public List<Driver> getDriversforSeason(Season season){
		String sql= "SELECT DISTINCT d.* " + 
				"FROM races AS r, results AS res, drivers AS d " + 
				"WHERE r.year=? " + 
				"AND res.raceId=r.raceId " + 
				"AND res.position is not null " + 
				"AND res.driverId=d.driverId ";
		
		List<Driver> drivers=new ArrayList<>();
		Connection conn= DBConnect.getConnection();
		
		try {

			PreparedStatement st = conn.prepareStatement(sql);
			
			st.setInt(1, season.getYear().getValue());//getValue per ottenere int da year
			
			ResultSet rs = st.executeQuery();
			
			while(rs.next()){
				Driver d= new Driver(rs.getInt("driverId"), rs.getString("driverref"), rs.getInt("number"),
						rs.getString("code"), rs.getString("forename"), rs.getString("surname"),
						rs.getDate("dob").toLocalDate(), rs.getString("nationality"), rs.getString("url"));
				drivers.add(d);
			}
			
			conn.close();
			
			return drivers;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Query Error");
		}
	}
	
	/*
	 * Conta il numero di vittorie di d1 su d2 nella stagione season
	 */
	public Integer contaVittorie(Driver d1, Driver d2, Season season) {
		String sql="SELECT count(r.raceId) AS cnt " + 
				"FROM results AS r1, results AS r2, races AS r " + 
				"WHERE r1.raceId=r2.raceId " + 
				"AND r.raceId=r1.raceId " + 
				"AND r.year=? " + 
				"AND r1.position<r2.position " + 
				"AND r1.driverId=? " + 
				"AND r2.driverId=? ";
		//non devo mettere la condizione (position is not null) perchè quando faccio il confronto tra le due posizioni mi restituisce sempre falso
	
		Integer count;
		Connection conn= DBConnect.getConnection();
		
		try {

			PreparedStatement st = conn.prepareStatement(sql);
			
			st.setInt(1, season.getYear().getValue());
			st.setInt(2, d1.getDriverId());
			st.setInt(3, d2.getDriverId());
			
			ResultSet rs = st.executeQuery();
			
			rs.next();
			count=rs.getInt("cnt");
			
			conn.close();
			
			return count;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	
	
	}


	public static void main(String[] args) {
		FormulaOneDAO dao = new FormulaOneDAO() ;
		
		List<Integer> years = dao.getAllYearsOfRace() ;
		System.out.println(years);
		
		List<Season> seasons = dao.getAllSeasons() ;
		System.out.println(seasons);

		
		List<Circuit> circuits = dao.getAllCircuits();
		System.out.println(circuits);

		List<Constructor> constructors = dao.getAllConstructors();
		System.out.println(constructors);
		
	}
	
}
