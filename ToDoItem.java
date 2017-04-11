package com.example.mapmanagement;

import com.google.android.gms.maps.model.LatLng;
/**
 * Representation of a to do item for the MapManagement app.
 * 
 * It contains a description of the item, a LatLng, and an id.
 *  
 * @author jesusmolina
 *
 */
public class ToDoItem {
	/** The description of the ToDoItem. */
	String description;
	
	/** The latitude and longitude of the ToDoItem. */
	LatLng latLng;
	
	/** The id of this ToDoItem (used to get its row in the database). */
	long id;
	
	/**
	 * Constructor, takes a description and a LatLng object.
	 * 
	 * @param d the description of the ToDoItem.
	 * @param latlng The Latitude and Longitude of the ToDoItem.
	 */
	public ToDoItem(String d, LatLng latlng){
		description = d;
		latLng = latlng;
		id = 0;
	}
	
	/**
	 * Returns the description of this ToDoItem.
	 *  
	 * */
	public String getDescription() {
		return description;
	}
	
	
	/**
	 * Sets the id of this ToDoItem to the given id.
	 * 
	 * @param id the given id.
	 */
	public void setId(long id){
		this.id = id;
	}
	
	/**
	 * Returns the id of this ToDoItem.
	 * 
	 * @return the id of this ToDoItem.
	 */
	public long getId(){
		return (int)id;
	}
	
	/**
	 * Return the Latitude of this ToDoItem.
	 * 
	 * @return the latitude of this ToDoItem.
	 */
	public double getLatitude(){
		return latLng.latitude;
	}
	
	/**
	 * Return the longitude of this ToDoItem.
	 * 
	 * @return the longitude of this ToDoItem.
	 */
	public double getLongitude(){
		return latLng.longitude;
	}
	
}
