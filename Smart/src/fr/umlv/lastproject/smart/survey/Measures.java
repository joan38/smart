package fr.umlv.lastproject.smart.survey;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.osmdroid.views.MapView;
import fr.umlv.lastproject.smart.layers.GeometryLayer;
import fr.umlv.lastproject.smart.layers.GeometryLayerSingleTapListener;
import fr.umlv.lastproject.smart.layers.GeometryType;
import fr.umlv.lastproject.smart.layers.LineGeometry;
import fr.umlv.lastproject.smart.layers.LineSymbology;
import fr.umlv.lastproject.smart.layers.PointGeometry;
import fr.umlv.lastproject.smart.layers.PointSymbology;

/**
 * 
 * This class is used to mesure the distance between two points
 * 
 * @author thibault brun
 *
 */
public final class Measures {
	
	private List<MeasureStopListener> listeners = new ArrayList<MeasureStopListener>() ;
	private GeometryLayer pointLayer ; 
	private GeometryLayer lineLayer ;
	private final MapView mapview ;

	
	/**
	 * private constructor because static
	 */
	public Measures(MapView mapview){
		this.mapview = mapview ;
		pointLayer  = new GeometryLayer(mapview.getContext()) ;
		lineLayer = new GeometryLayer(mapview.getContext()) ;
		pointLayer.setType(GeometryType.POINT);
		lineLayer.setType(GeometryType.LINE);
		pointLayer.setSymbology(new PointSymbology()) ;
		lineLayer.setSymbology(new LineSymbology());
		pointLayer.setEditable(true);
		mapview.getOverlayManager().add(pointLayer);
		mapview.getOverlayManager().add(lineLayer);

	} 
	
	/**
	 * 
	 * @param a the first point of the measure
	 * @param b the second point of the measure
	 * @return the distance before theses two points
	 */
	public  double measure(PointGeometry a, PointGeometry b){
		return  a.getCoordinates().distanceTo(b.getCoordinates())  ;
	}
	
	/**
	 * 
	 * @param a the position of the user
	 * @return the distance beetween a and an other point
	 */
	public  void measure(final PointGeometry a){
		pointLayer.addGeometryLayerSingleTapListener(new GeometryLayerSingleTapListener() {
			
			@Override
			public void actionPerformed(PointGeometry p) {
				
				LineGeometry l = new LineGeometry() ;
				l.addPoint(a) ; l.addPoint(p);
				lineLayer.addGeometry(l) ;
				mapview.invalidate();
				
				for(int i=0 ; i < listeners.size();i++){
					listeners.get(i).actionPerformed(measure(a, p)) ;
				}
			}
		}) ;		
	}
	
	/**
	 * 
	 * @return the distance beetween two points
	 */
	public  void measure(){
		
		final List<PointGeometry> list = new ArrayList<PointGeometry>() ;
		final AtomicInteger count = new AtomicInteger( 0) ;
		pointLayer.addGeometryLayerSingleTapListener(new GeometryLayerSingleTapListener() {
			
			@Override
			public void actionPerformed(PointGeometry point) {
				int numberOfPoints =count.incrementAndGet();
				if(numberOfPoints == 1){
					list.add(point) ;
					pointLayer.addGeometry(point);
					mapview.invalidate();
				}
				if(numberOfPoints == 2 ){
					list.add( point );
					LineGeometry l = new LineGeometry() ;
					l.addPoint(list.get(0));
					l.addPoint(list.get(1)) ;
					lineLayer.addGeometry(l);
					mapview.getOverlayManager().remove(pointLayer);
					mapview.invalidate();
					for(int i =0 ; i < listeners.size();i++){
						listeners.get(i).actionPerformed(measure(list.get(0), list.get(1))) ;
					}
				}
			}
		}) ;
	}
	
	/**
	 * 
	 * @param l the listener to use
	 */
	public void addStopListener(MeasureStopListener l){
		listeners.add(l);
	}
	
	/**
	 * 
	 * @param l the listener to remove
	 */
	public void removeListener(MeasureStopListener l){
		listeners.remove(l);
	}

	/**
	 * stop all listeners and remove overlays
	 */
	public void stop() {
		listeners = new ArrayList<MeasureStopListener>() ;
		mapview.getOverlayManager().remove(pointLayer);
		mapview.getOverlayManager().remove(lineLayer);
		
	}
	
}
