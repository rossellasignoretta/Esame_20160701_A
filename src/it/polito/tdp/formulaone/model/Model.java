package it.polito.tdp.formulaone.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.formulaone.db.FormulaOneDAO;

public class Model {

	private FormulaOneDAO dao;
	private List<Season> seasons;
	private SimpleDirectedWeightedGraph<Driver, DefaultWeightedEdge> graph;
	
	//variabili di stato della ricorsione
	int tassoMin;
	List<Driver> teamMin;
	
	public Model(){
		dao= new FormulaOneDAO();
	}
	
	public List<Season> getSeasons() {
		if(seasons==null){
			seasons=dao.getAllSeasons();
		}
		return seasons;
	}
	
	public void creaGrafo(Season season){
		graph= new SimpleDirectedWeightedGraph<Driver,DefaultWeightedEdge>(DefaultWeightedEdge.class);
		
		//VERTICI
		Graphs.addAllVertices(graph, dao.getDriversforSeason(season));
		
		//ARCHI
		for(Driver d1: this.graph.vertexSet()){
			for (Driver d2: this.graph.vertexSet()){
				if(!d1.equals(d2)){
					Integer vittorie= dao.contaVittorie(d1, d2, season);
					if(vittorie>0){
						Graphs.addEdgeWithVertices(graph, d1, d2, vittorie);
					}
				}
			}
		}
		
		//System.out.println(graph);
	}
	
	public Driver getBestDriver(){
		Driver best=null;
		int max=Integer.MIN_VALUE;
		
		for(Driver d: this.graph.vertexSet()){
			int peso=0;
			
			for(DefaultWeightedEdge e: graph.outgoingEdgesOf(d)){
				peso+=graph.getEdgeWeight(e);
			}
			for(DefaultWeightedEdge e: graph.incomingEdgesOf(d)){
				peso-=graph.getEdgeWeight(e);
			}
			
			if(peso>max){
				max=peso;
				best=d;
			}
		}
		
		return best;
	}

	public List<Driver> getDreamTeam(int K){
		//inizializzo le variabili di stato della ricorsione
		tassoMin =Integer.MAX_VALUE;
		teamMin= null;
		
		Set <Driver> team= new HashSet<>();
		
		ricorsiva(0,team, K);
		
		return teamMin;
	}
	/**
	 * In ingresso ricevo il team parziale composto da passo elementi
	 * La variabile passo parte da 0
	 * Il caso terminale è quando passo==K, ed in quel caso va calcolato il tasso di sconfitta
	 * Altrimenti si procede ricorsivamente ad aggiungere un nuovo vertice(il passo +1esimo)
	 * 		scegliendo tra i vertici non ancora presenti nel team
	 * @param passo
	 * @param team
	 * @param K
	 */
	private void ricorsiva(int passo, Set<Driver> team, int K){
		//caso terminale?
		if(passo==K){
			//calcolare tasso di sconfitta del team
			int tasso=this.tassoSconfitta(team);
			//eventualmente aggiornare il minimo 
			if(tasso<tassoMin){
				tassoMin=tasso;
				teamMin= new ArrayList<Driver>(team);
				//System.out.println(tasso+" "+team);
			}
		}else{
			//scelgo il prossimo vertice
			Set<Driver> candidati= new HashSet<>(graph.vertexSet());
			candidati.removeAll(team);
			
			for(Driver d: candidati){
				//genero nuova soluzione
				team.add(d);
				//ricorsione
				ricorsiva(passo+1, team, K);
				//backtrack
				team.remove(d);
			}
		}
	}

	private int tassoSconfitta(Set<Driver> team) {
		int tasso=0;
		//è più semplice ciclare gli archi e non fare due cicli for su tutti i vertici
		for(DefaultWeightedEdge dwe: graph.edgeSet()){
			//mi interessano solo gli archi con il vertice di partenza esterno al team e quello di destinazione interno al team
			if(!team.contains(graph.getEdgeSource(dwe)) && team.contains(graph.getEdgeTarget(dwe))){
				tasso+=graph.getEdgeWeight(dwe);
			}
		}
		return tasso;
	}
}
