package module6;

import java.util.List;

import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import processing.core.PConstants;
import processing.core.PGraphics;

/** 
 * A class to represent AirportMarkers on a world map.
 *   
 * @author Adam Setters and the UC San Diego Intermediate Software Development
 * MOOC team
 *
 */
public class AirportMarker extends CommonMarker {
	
	public static int TRI_SIZE = 3;
	public static List<SimpleLinesMarker> routes;
	
	public AirportMarker(Feature airport) {
		super(((PointFeature)airport).getLocation(), airport.getProperties());
		//System.out.println(airport.getProperties());
	}
	
	@Override
	public void drawMarker(PGraphics pg, float x, float y) {
		pg.fill(255, 255, 0);
		pg.ellipse(x, y, 4, 4);
        pg.pushStyle();
		
		// IMPLEMENT: drawing triangle for each city
		//pg.fill(150, 30, 30);
		//pg.triangle(x, y-TRI_SIZE, x-TRI_SIZE, y+TRI_SIZE, x+TRI_SIZE, y+TRI_SIZE);
		
		// Restore previous drawing style
		pg.popStyle();
	
	}

	@Override
	public void showTitle(PGraphics pg, float x, float y)
	{
		String country = getCountry();
		String city = getCity();
		String name = getName();
		
		pg.pushStyle();
		
		pg.fill(255, 255, 255);
		pg.textSize(12);
		pg.rectMode(PConstants.CORNER);
		pg.rect(x, y-TRI_SIZE-40, Math.max(pg.textWidth(country),pg.textWidth(name)) + 6, 50);
		pg.fill(0, 0, 0);
		pg.textAlign(PConstants.LEFT, PConstants.TOP);
		pg.text(country, x+3, y-TRI_SIZE-33);
		pg.text(city, x+3, y - TRI_SIZE -21);
		pg.text(name, x+3, y - TRI_SIZE -8);
		pg.popStyle();
	}
	
	private String getCity()
	{
		return getStringProperty("city");
	}
	
	private String getCountry()
	{
		return getStringProperty("country");
	}
	
	private String getName()
	{
		return getStringProperty("name");
	}
	
}
