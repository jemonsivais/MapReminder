package com.example.mapmanagement;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.google.android.gms.identity.intents.Address;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.FragmentManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.database.Cursor;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Main activity for the Map Management app.
 * It contains a MapFragment where the to do items locations
 * are shown as markers.
 * 
 * The options menu contains two options. One for adding a to do item
 * at the current location, and the second one to specify an address
 * where to add a to do item.
 * 
 * All the to do items are stored in a database.
 * 
 * @author jesusmolina
 *
 */
public class MainActivity extends ActionBarActivity {
	/** The adapter of the database. */
	DBAdapter dba;
	
	/** The fragment manager (used to get the MapFragment). */
	FragmentManager fm;
	
	/** The MapFragment from which we will get the GoogleMap. */
	MapFragment mapFragment;
	
	/** The GoogleMap that is displayed. */
	GoogleMap map;
	
	/** Dialog to create a new to do item. */
	ToDoItemDialog dialog;
	
	/** The list where the markers will be stored. */
	LinkedList<Marker> destinations;
	
	/** The list where the to do items will be stored. */
	LinkedList<ToDoItem> items;
	
	/** Manager from which we will get the current location. */
	LocationManager locationManager;
	
	/** Location where the current position of the device will be stored. */
	Location location;
	
	/** LatLng where the latitude and longitude of the device will be stored. */
	LatLng latLng;
	
	/** Marker for the position of this device. */
	Marker myPosition;
	
	/** General marker that will point to a specified marker, either by touching, or by 
	 * adding a new one. */
	Marker marker;
	
	/**
	 * Creates a new activity.
	 * Sets the GoogleMap to listen for long click events, and marker click events.
	 * Sets the LocationManager to listen for location changes events.
	 * Initializes the DBAdapter.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		fm = getFragmentManager();
		mapFragment = (MapFragment) fm.findFragmentById(R.id.map);
		map = mapFragment.getMap();
		
		locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);

		setLocationManager();
				
		setMapListeners();

		dba = new DBAdapter(this);
				
	}

	/**
	 * Moves the map to the device current location, it also initializes the
	 * Markers and ToDoItem's lists.
	 * 
	 * It reads the database and fills the lists.
	 */
	public void onResume(){
		super.onResume();
		location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if(location == null){
			location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}
		latLng = new LatLng(location.getLatitude(),location.getLongitude());
		
		map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
		map.animateCamera(CameraUpdateFactory.zoomTo(15));
		
		myPosition = map.addMarker(new MarkerOptions().position(latLng).title("Current Position"));
		destinations = new LinkedList<Marker>();
		items = new LinkedList<ToDoItem>();
		
		dba.open();
		Cursor c = dba.getAllItems(); 
		if (c.moveToFirst()){
			do {
				LatLng position = new LatLng(Double.parseDouble(c.getString(2)),Double.parseDouble(c.getString(3)));
				destinations.add(map.addMarker(new MarkerOptions().position(position).title(c.getString(1))));
				items.add(new ToDoItem(c.getString(1), position));
				items.getLast().setId(Long.parseLong(c.getString(0)));
				} while (c.moveToNext()); 
		}
		dba.close();
	}
	
	/**
	 * Sets the listeners for the GoogleMap.
	 * The OnMarkerClickListener is used to remove markers.
	 * 
	 * The OnMapLongClickListener is used to add a new marker.
	 * 
	 */
	private void setMapListeners() {
		
		map.setOnMarkerClickListener(new OnMarkerClickListener(){

			@Override
			public boolean onMarkerClick(Marker arg0) {
				// TODO Auto-generated method stub
				MarkerDialog.newInstance("Remove to do item?").show(getFragmentManager(), "dialog");
				marker = arg0;
				return false;
			}
			
		});
		
		map.setOnMapLongClickListener(new OnMapLongClickListener(){

			@Override
			public void onMapLongClick(final LatLng arg0) {
				// TODO Auto-generated method stub
				dialog = ToDoItemDialog.newInstance("Add to do item here?");
				dialog.show(getFragmentManager(), "dialog");
				marker = map.addMarker(new MarkerOptions().position(arg0));
			}
			
		});
	}
	
	/**
	 * Sets the location manager to listen for location updates.
	 * It will listen for every 1km of location change, and every 10 minutes.
	 * 
	 * It removes the previous marker of the device positions and puts a new one
	 * in the new position.
	 * 
	 * If a to do item is less than 1 km away, it will notify the user.
	 * 
	 */
	private void setLocationManager() {
		// TODO Auto-generated method stub
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 1000,
					 new LocationListener(){
							@Override
							public void onLocationChanged(Location location) {
								// TODO Auto-generated method stub
								myPosition.remove();
								myPosition = map.addMarker(new MarkerOptions().
										position(new LatLng(location.getLatitude(), location.getLongitude()))
										.title("Current Position"));
								final float[] dist = new float[1];
								for(int i = 0; i < destinations.size(); i++){
									LatLng destination = destinations.get(i).getPosition();
									Location.distanceBetween(location.getLatitude(), location.getLongitude(),
											destination.latitude, destination.longitude, dist);
									if(dist[0] < 1000){
										displayNotification(destinations.get(i).getTitle());
									}
								}
							}

							@Override
							public void onStatusChanged(String provider, int status,
									Bundle extras) {
								// TODO Auto-generated method stub
								
							}

							@Override
							public void onProviderEnabled(String provider) {
								// TODO Auto-generated method stub
								
							}

							@Override
							public void onProviderDisabled(String provider) {
								// TODO Auto-generated method stub
								
							}
					
				});
		}
	
	/**
	 * Creates a notification and makes the device vibrate.
	 * 
	 * @param title the title of the ToDoItem.
	 */
	protected void displayNotification(String title){
		NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		Notification notif = new Notification.Builder(this)
		.setContentTitle("Reminder!")
		.setContentText("Remember "+title+"!")
		.setSmallIcon(R.drawable.ic_launcher).build();
		notif.vibrate = new long[]{100,250,100,500};
		nm.notify(1, notif);
	}
	
	/**
	 * Called when the user taps "OK" in the Dialog to add a ToDoItem.
	 * 
	 * Names the new marker, and creates a new ToDoItem.
	 * Then it updates the database.
	 * 
	 * @param text The text inputed by the user.
	 * 
	 */
	public void doPositiveClick(String text){
		marker.setTitle(text);
		ToDoItem tdi = new ToDoItem(text,marker.getPosition());
		addToDatabase(tdi);
		destinations.add(marker);
		dialog.dismiss();
	}
	
	/**
	 * Adds a ToDoItem to the database.
	 * It also adds the ToDoItem to the list.
	 * 
	 * @param tdi
	 */
	private void addToDatabase(ToDoItem tdi) {
		// TODO Auto-generated method stub
		dba.open();
		tdi.setId(dba.insertItem(tdi.getDescription(), ""+tdi.getLatitude(), ""+tdi.getLongitude()));
		items.add(tdi);
		dba.close();
	}

	/**
	 * Called when the user clicks "Cancel" on the Dialog to add a new ToDoItem.
	 * Removes the previously added marker.
	 * 
	 */
	public void doNegativeClick(){
		marker.remove();
		dialog.dismiss();
	}
	
	/**
	 * Called when the user clicks "OK" when trying to remove a marker.
	 * 
	 * It removes the marker, deletes the row in the database, and removes
	 * the ToDoItem and the Marker from the lists.
	 * 
	 */
	public void doPositiveRemoverClick(){
		dba.open();
		long id = items.get(destinations.lastIndexOf(marker)).getId();
		if(dba.deleteItem(id))
			Toast.makeText(getBaseContext(), "Successfully deleted", Toast.LENGTH_SHORT).show();
		dba.close();
		items.remove(destinations.lastIndexOf(marker));
		destinations.remove(destinations.lastIndexOf(marker));
		marker.remove();
	}
	
	/**
	 * If the user clicks "Cancel" when trying to remove a marker.
	 * 
	 * Nothing happens.
	 */
	public void doNegativeRemoverClick(){
		
	}
	
	/**
	 * Creates an option menu.
	 * 
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		createMenu(menu);
		return true;
	}

	/**
	 * Sets what happens when an item is selected from the menu.
	 * 
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return menuChoice(item);
	}
	
	/**
	 * When a menu item is chosen.
	 * 
	 * If the first option is chosen:
	 * a new to do item will be created with the current
	 * position of the device as the location.
	 * 
	 * A dialog is used to specify the to do item name.
	 * 
	 * If the second option is chosen:
	 * a new SpecifyAddressDialog will be created and used
	 * to add a new marker and to do item wil the location
	 * of the given address.
	 * 
	 * @param item
	 * @return
	 */
	private boolean menuChoice(MenuItem item){
		switch(item.getItemId()){
		case 0:
			dialog = ToDoItemDialog.newInstance("Add to do item.");
			dialog.show(getFragmentManager(), "dialog");
			marker = map.addMarker(new MarkerOptions().position(latLng));			
			return true;
		case 1:
			SpecifyAddressDialog sad;
			sad = SpecifyAddressDialog.newInstance("Specify address");
			sad.show(getFragmentManager(), "dialog");
			
			
			return true;
		}
		return false;
	}
	/**
	 * The menu will have two options.
	 * 
	 * The first one to add a ToDoItem in the current location.
	 * 
	 * The second one to add a ToDoItem by specifying its address.
	 * 
	 * @param menu
	 */
	private void createMenu(Menu menu){
		menu.add(0,0,0,"Add To-do Item here.");
		menu.add(0,1,1,"Specify address to add a to do item.");
	}

	/**
	 * When the user clicks "OK" when chosen to add 
	 * a ToDoItem by specifying its address. 
	 * 
	 * It uses the user specified address to add a new ToDoItem.
	 * 
	 * @param addr
	 */
	public void doPositiveSpecifyClick(String addr) {
		// TODO Auto-generated method stub
		List<android.location.Address> address;
		Geocoder coder = new Geocoder(this);
		try {
			address = coder.getFromLocationName(addr,5);
			android.location.Address location = address.get(0);
			LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
			marker = map.addMarker(new MarkerOptions().position(position));
			dialog = ToDoItemDialog.newInstance("Add to do item.");
			dialog.show(getFragmentManager(), "dialog");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * When the user clicks "Cancel" when choosing to add a new
	 * ToDoItem by specifying its address.
	 * 
	 * Nothing happens.
	 */
	public void doNegativeSpecifyClick() {
		// TODO Auto-generated method stub
		
	}
	
}
