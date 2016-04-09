package module6;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.data.ShapeFeature;
import de.fhpotsdam.unfolding.marker.AbstractShapeMarker;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.MultiMarker;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.geo.Location;
import parsing.ParseFeed;
import processing.core.PApplet;

/** An applet that shows airports (and routes)
 * on a world map.  
 * @author Adam Setters and the UC San Diego Intermediate Software Development
 * MOOC team
 *
 */
public class AirportMap extends PApplet {
	
	UnfoldingMap map;
	private List<Marker> airportList;
	List<Marker> routeList;
	private String countryFile = "countries.geo.json";
	private List<Marker> countryMarkers;
	// NEW IN MODULE 5
    private CommonMarker lastSelected;
    private CommonMarker lastClicked;
	
	
	public void setup() {
		// setting up PAppler
		size(800,600, OPENGL);
		
		// setting up map and default events
		map = new UnfoldingMap(this, 50, 50, 750, 550);
		MapUtils.createDefaultEventDispatcher(this, map);
		
		List<Feature> countries = GeoJSONReader.loadData(this, countryFile);
		//countryMarkers is a list of markers represent graphically a country in list feature countries
		//Data in contryMarkers is created by MapUtils. Now, contryMarkers include all countries on the world
		// A List of country markers
	
		countryMarkers = MapUtils.createSimpleMarkers(countries);
		
		// get features from airport data
		List<PointFeature> airport_features = ParseFeed.parseAirports(this, "airports.dat");
		
		// list for markers, hashmap for quicker access when matching with routes
		airportList = new ArrayList<Marker>();
		HashMap<Integer, Location> airports = new HashMap<Integer, Location>();
		
		// create markers from features
		
		for (Marker country : countryMarkers) 
		{
		   int number =0;
		   for(PointFeature ap_feature : airport_features) {
			   AirportMarker m = new AirportMarker(ap_feature);
	           m.setRadius(6);
	            
				if (isAirportInCountry(ap_feature, country)) {
				  //System.out.println(country.getProperty("name"));
				  if(country.getProperty("name").equals("Mexico")||country.getProperty("name").equals("China")||country.getProperty("name").equals("Argentina")
				||country.getProperty("name").equals("Pakistan")||country.getProperty("name").equals("Brazil")||country.getProperty("name").equals("Mongolia")||country.getProperty("name").equals("Australia")
				||country.getProperty("name").equals("India"))
						  {
					  number++;
					  if(number<=6)
					  {
						airportList.add(m); 
						// put airport in hashmap with OpenFlights unique id for key
						airports.put(Integer.parseInt(ap_feature.getId()), ap_feature.getLocation());
					  }  
				  }
				  if (country.getProperty("name").equals("Canada")||country.getProperty("name").equals("United States of America")
							||country.getProperty("name").equals("Russia"))
							
							{
								  number++;
								  if(number<=10)
								  {
									airportList.add(m); 
									// put airport in hashmap with OpenFlights unique id for key
									airports.put(Integer.parseInt(ap_feature.getId()), ap_feature.getLocation());
								  }  
							  }
				  else
				  {
					  number++;
					  if(number<=1)
					  {
						airportList.add(m); 
						// put airport in hashmap with OpenFlights unique id for key
						airports.put(Integer.parseInt(ap_feature.getId()), ap_feature.getLocation());
					  }
				  }
				}
		   }//inside for
		}// outside for
		
		
		// parse route data
		List<ShapeFeature> routes = ParseFeed.parseRoutes(this, "routes.dat");
		routeList = new ArrayList<Marker>();
		for(ShapeFeature route : routes) {
			
			// get source and destination airportIds
			int source = Integer.parseInt((String)route.getProperty("source"));
			int dest = Integer.parseInt((String)route.getProperty("destination"));
			
			// get locations for airports on route
			if(airports.containsKey(source) && airports.containsKey(dest)) {
				route.addLocation(airports.get(source));
				route.addLocation(airports.get(dest));
			}
			
			SimpleLinesMarker sl = new SimpleLinesMarker(route.getLocations(), route.getProperties());
		
			//System.out.println(sl.getProperties());
			
			//UNCOMMENT IF YOU WANT TO SEE ALL ROUTES
			routeList.add(sl);
		}
		
		
		
		//UNCOMMENT IF YOU WANT TO SEE ALL ROUTES
		//map.addMarkers(routeList);
		
		map.addMarkers(airportList);
		
	}
	
	public void draw() {
		background(0);
		map.draw();
		
	}
	
	public void mouseMoved()
	{
		// clear the last selection
		if (lastSelected != null) {
			lastSelected.setSelected(false);
			lastSelected = null;
		
		}
		selectMarkerIfHover(airportList);
		
		//loop();
	}
	
	// If there is a marker selected 
	private void selectMarkerIfHover(List<Marker> markers)
	{
		// Abort if there's already a marker selected
		if (lastSelected != null) {
			return;
		}
		
		for (Marker m : markers) 
		{
			CommonMarker marker = (CommonMarker)m;
			if (marker.isInside(map,  mouseX, mouseY)) {
				lastSelected = marker;
				marker.setSelected(true);
				return;
			}
		}
	}
	
	//This method is to check if airport is located in an single country
	private boolean isAirportInCountry(PointFeature airport, Marker country) {
		// getting location of feature
		Location checkLoc = airport.getLocation();

		// some countries represented it as MultiMarker
		// looping over SimplePolygonMarkers which make them up to use isInsideByLoc
		if(country.getClass() == MultiMarker.class) {
				
			// looping over markers making up MultiMarker
			for(Marker marker : ((MultiMarker)country).getMarkers()) {
					
				// checking if inside
				if(((AbstractShapeMarker)marker).isInsideByLocation(checkLoc)) {
					airport.addProperty("country", country.getProperty("name"));
						
					// return if is inside one
					return true;
				}
			}
		}
			
		// check if inside country represented by SimplePolygonMarker
		else if(((AbstractShapeMarker)country).isInsideByLocation(checkLoc)) {
			airport.addProperty("country", country.getProperty("name"));
			
			return true;
		}
		return false;
	   }
	
	///////////////////////////////////////////////////////////////
    

	

}
