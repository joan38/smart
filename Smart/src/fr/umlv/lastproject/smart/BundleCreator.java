package fr.umlv.lastproject.smart;

import java.util.ArrayList;
import java.util.List;

import org.osmdroid.util.GeoPoint;

import fr.umlv.lastproject.smart.form.Form;
import fr.umlv.lastproject.smart.form.Mission;
import fr.umlv.lastproject.smart.layers.Geometry;
import fr.umlv.lastproject.smart.layers.GeometryLayer;
import fr.umlv.lastproject.smart.layers.GeometryType;
import fr.umlv.lastproject.smart.layers.Symbology;
import android.os.Bundle;

public class BundleCreator {


	public static void saveMission(Bundle outState, boolean missionCreated){

		// Mission started
		outState.putSerializable("MissionCreated", missionCreated) ;

		if(Mission.getInstance() != null){

			// Mission form 
			outState.putSerializable("FORM", Mission.getInstance().getForm()) ;

			//MISSION NAME
			outState.putString("MISSIONNAME", Mission.getInstance().getTitle()) ;

			// Layer point mission
			outState.putString("MISSIONPOINTNAME", Mission.getInstance().getPointLayer().getName() ) ;
			outState.putInt("MISSIONPOINTCOUNT", Mission.getInstance().getPointLayer().getGeometries().size() ) ;
			for(int i =0 ; i < Mission.getInstance().getPointLayer().getGeometries().size() ; i++){
				outState.putSerializable(Mission.getInstance().getPointLayer().getName() + i, Mission.getInstance().getPointLayer().getGeometries().get(i)) ;
			}
			outState.putSerializable("MISSIONPOINTSYMBO", Mission.getInstance().getPointLayer().getSymbology()) ;


			// Layer ligne mission
			outState.putString("MISSIONLINENAME", Mission.getInstance().getLineLayer().getName() ) ;
			outState.putInt("MISSIONLINECOUNT", Mission.getInstance().getLineLayer().getGeometries().size() ) ;
			for(int i =0 ; i < Mission.getInstance().getLineLayer().getGeometries().size() ; i++){
				outState.putSerializable(Mission.getInstance().getLineLayer().getName() + i, Mission.getInstance().getLineLayer().getGeometries().get(i)) ;
			}
			outState.putSerializable("MISSIONLINESYMBO", Mission.getInstance().getLineLayer().getSymbology()) ;


			// Layer polygon mission
			outState.putString("MISSIONPOLYGONNAME", Mission.getInstance().getPolygonLayer().getName() ) ;
			outState.putInt("MISSIONPOLYGONCOUNT", Mission.getInstance().getPolygonLayer().getGeometries().size() ) ;
			for(int i =0 ; i < Mission.getInstance().getPolygonLayer().getGeometries().size() ; i++){
				outState.putSerializable(Mission.getInstance().getPolygonLayer().getName() + i, Mission.getInstance().getPolygonLayer().getGeometries().get(i)) ;
			}
			outState.putSerializable("MISSIONPOLYGONSYMBO", Mission.getInstance().getPolygonLayer().getSymbology()) ;
		}

	}

	public static void savePosition(Bundle outState, SmartMapView mapView) {
		outState.putInt("mapLat", mapView.getBoundingBox().getCenter().getLatitudeE6()) ;
		outState.putInt("mapLon", mapView.getBoundingBox().getCenter().getLongitudeE6()) ;
		outState.putInt("mapZoom", mapView.getZoomLevel()) ;
		
	}

	public static void loadPosition(Bundle savedInstanceState,
			SmartMapView mapView) {

		mapView.getController().setCenter(new GeoPoint(savedInstanceState.getInt("mapLat"), savedInstanceState.getInt("mapLon"))) ;
		mapView.getController().setZoom(savedInstanceState.getInt("mapZoom")) ;
		mapView.getTileProvider().clearTileCache();
		
	}
	
	public static boolean loadMission(Bundle savedInstanceState,
			SmartMapView mapView, MenuActivity menu) {

		//Mission created
		boolean missionCreated = savedInstanceState.getBoolean("MissionCreated") ;

		if(missionCreated){

			//Mission name
			String mname = savedInstanceState.getString("MISSIONNAME") ;

			// Mission Form
			Form f = (Form) savedInstanceState.getSerializable("FORM") ;

			// Mission point layer
			GeometryLayer missionPoint = new GeometryLayer(menu) ;
			missionPoint.setType(GeometryType.POINT); 
			missionPoint.setSymbology((Symbology)savedInstanceState.getSerializable("MISSIONPOINTSYMBO")) ;
			missionPoint.setName(savedInstanceState.getString("MISSIONPOINTNAME")) ;
			for(int i = 0 ; i < savedInstanceState.getInt("MISSIONPOINTCOUNT") ; i ++){
				missionPoint.addGeometry((Geometry)savedInstanceState.getSerializable(missionPoint.getName()+i)); 				
			}
			

			// Mission line layer
			GeometryLayer missionLine = new GeometryLayer(menu) ;
			missionLine.setType(GeometryType.LINE); 
			missionLine.setSymbology((Symbology)savedInstanceState.getSerializable("MISSIONLINESYMBO")) ;
			missionLine.setName(savedInstanceState.getString("MISSIONLINENAME")) ;
			for(int i = 0 ; i < savedInstanceState.getInt("MISSIONLINECOUNT") ; i ++){
				missionLine.addGeometry((Geometry)savedInstanceState.getSerializable(missionLine.getName()+i)); 				
			}

			// Mission polygon layer
			GeometryLayer missionPolygon = new GeometryLayer(menu) ;
			missionPolygon.setType(GeometryType.POLYGON); 
			missionPolygon.setSymbology((Symbology)savedInstanceState.getSerializable("MISSIONPOLYGONSYMBO")) ;
			missionPolygon.setName(savedInstanceState.getString("MISSIONPOLYGONNAME")) ;
			for(int i = 0 ; i < savedInstanceState.getInt("MISSIONPOLYGONCOUNT") ; i ++){
				missionPolygon.addGeometry((Geometry)savedInstanceState.getSerializable(missionPolygon.getName()+i)); 				
			}

			Mission.createMission(mname,menu,mapView,f, missionPoint, missionLine, missionPolygon) ;
			mapView.addGeometryLayer(Mission.getInstance().getPointLayer());
			mapView.addGeometryLayer(Mission.getInstance().getLineLayer());
			mapView.addGeometryLayer(Mission.getInstance().getPolygonLayer());
			Mission.getInstance().startMission() ;
		}
		
		return missionCreated;
		
	}

	public static void saveGeomtryLayers(Bundle outState,
			List<GeometryLayer> geometryOberlays) {
		int count = 0 ;
		for(GeometryLayer g : geometryOberlays){
			if(! isInMission(g)){
				outState.putString("GEOMETRYLAYER"+count,g.getName() ) ;
				outState.putInt(g.getName(), g.getGeometries().size() ) ;
				outState.putSerializable(g.getName()+"TYPE", g.getType()) ;
				for(int i =0 ; i <g.getGeometries().size() ; i++){
					outState.putSerializable(g.getName() + i, g.getGeometries().get(i)) ;
				}
				outState.putSerializable(g.getName()+"SYMBO", g.getSymbology()) ;
				count ++ ;
			}
		}
		outState.putInt("GEOMETRYLAYERCOUNT", count) ;		
	}
	
	public static void loadGeometryLayers(Bundle savedInstanceState, MenuActivity menu, SmartMapView map){
		List<GeometryLayer> list = new ArrayList<GeometryLayer>() ;
		
		// read the number of layers :)
		int count = savedInstanceState.getInt("GEOMETRYLAYERCOUNT") ;
		for(int i = 0 ; i < count ; i++ ){
			String name = savedInstanceState.getString("GEOMETRYLAYER"+i) ;
			int geomCOunt = savedInstanceState.getInt(name); 
			GeometryLayer tmp = new GeometryLayer(menu);
			tmp.setType((GeometryType)savedInstanceState.getSerializable(name+"TYPE")) ;
			for(int j = 0 ; j < geomCOunt ; j++){
				tmp.addGeometry((Geometry)savedInstanceState.getSerializable(name+j)) ;
			}
			tmp.setName(name);
			tmp.setSymbology((Symbology)savedInstanceState.getSerializable(name+"SYMBO")) ;
			map.addGeometryLayer(tmp);
		}
		
		
	}

	
	public static boolean isInMission(GeometryLayer g){
		
		if(Mission.getInstance() != null){
			if(g.getName().equals(Mission.getInstance().getPointLayer().getName())) return true ;
			if(g.getName().equals(Mission.getInstance().getPolygonLayer().getName())) return true ;
			if(g.getName().equals(Mission.getInstance().getLineLayer().getName())) return true ;			
		}
		return false ;
	}
	


}
