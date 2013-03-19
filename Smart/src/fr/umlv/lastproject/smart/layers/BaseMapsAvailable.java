package fr.umlv.lastproject.smart.layers;

import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;

import fr.umlv.lastproject.smart.Preferences;
import fr.umlv.lastproject.smart.utils.SmartConstants;



public enum BaseMapsAvailable{
	

	
	OpenStreetMap(0, TileSourceFactory.MAPNIK),
	GoogleMapHybrid(1,SmartConstants.GOOGLEHYBRIDMAP ),
	GoogleMapTerrain(2,SmartConstants.GOOGLETERRAINMAP ),
	GooleMapStreet(3,SmartConstants.GOOGLESTREETMAP),
	GoogleMapSatellite(4,SmartConstants.GOOGLESATELLITEMAP);

	private final  int id ;
	private final OnlineTileSourceBase source ;
	
	



	private BaseMapsAvailable(int id, OnlineTileSourceBase source){
		this.id = id ;
		this.source = source ;
		
	}

	public int getId() {
		return id;
	}

	public OnlineTileSourceBase getSource() {
		return source;
	}

	public static BaseMapsAvailable getFromId(int id){
		for(BaseMapsAvailable bma : BaseMapsAvailable.values()){
			if(id == bma.getId()) return bma ;
		}
		Preferences.getInstance().setBase_map(0);
		return BaseMapsAvailable.values()[0] ;
	}


}

