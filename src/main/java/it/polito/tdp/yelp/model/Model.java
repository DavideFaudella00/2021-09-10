package it.polito.tdp.yelp.model;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;
import it.polito.tdp.yelp.db.YelpDao;

public class Model {
	YelpDao dao = new YelpDao();
	SimpleWeightedGraph<Business, DefaultWeightedEdge> grafo;
	List<Business> businessCity;
	List<Business> tragitto;

	public void creaGrafo(String s) {
		grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		businessCity = dao.getAllBusiness(s);
		Graphs.addAllVertices(grafo, businessCity);
		for (Business c1 : businessCity) {
			for (Business c2 : businessCity) {
				if (!c1.equals(c2)) {
					double peso = LatLngTool.distance(c1.getPosizione(), c2.getPosizione(), LengthUnit.KILOMETER);
					Graphs.addEdgeWithVertices(grafo, c1, c2, peso);
				}
			}
		}
		System.out.println(grafo.vertexSet().size());
		System.out.println(grafo.edgeSet().size());
	}

	public String localePiuLontano(Business b) {
		Business best = b;
		String s = "";
		for (Business b1 : businessCity) {
			if (LatLngTool.distance(b.getPosizione(), b1.getPosizione(), LengthUnit.KILOMETER) > LatLngTool
					.distance(b.getPosizione(), best.getPosizione(), LengthUnit.KILOMETER)) {
				best = b1;
			}
		}
		s = best.getBusinessName() + "= "
				+ LatLngTool.distance(b.getPosizione(), best.getPosizione(), LengthUnit.KILOMETER) + "\n";
		return s;
	}

	public List<String> getAllCities() {
		return dao.getAllCities();
	}

	public List<Business> getBusinessCity() {
		return businessCity;
	}

	double x = -1;

	public List<Business> doRicorsione(Business b1, Business b2, double x) {
		tragitto = new ArrayList<Business>();
		this.x = x;
		List<Business> parziale = new ArrayList<>();
		businessCity.remove(b1);
		businessCity.remove(b2);
		parziale.add(b1);
		cerca(parziale);
		tragitto.add(b2);
		return tragitto;
	}

	private void cerca(List<Business> parziale) {
		if (tragitto.size() == businessCity.size() + 1) {
			return;
		}
		if (parziale.size() > tragitto.size()) {
			tragitto = new ArrayList<>(parziale);
		}
		for (Business b : businessCity) {
			if (isValid(parziale, b)) {
				parziale.add(b);
				cerca(parziale);
				parziale.remove(b);
			}
		}
	}

	private boolean isValid(List<Business> parziale, Business b) {
		if (parziale.contains(b) || b.getStars() < x) {
			return false;
		}
		return true;
	}

}
