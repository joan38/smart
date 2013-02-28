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
	private GeometryLayer gl ; 
	private GeometryLayer gll ;
	private final MapView mapview ;

	
	/**
	 * private constructor because static
	 */
	public Measures(MapView mapview){
		this.mapview = mapview ;
		gl  = new GeometryLayer(mapview.getContext()) ;
		gll = new GeometryLayer(mapview.getContext()) ;
		gl.setType(GeometryType.POINT);
		gll.setType(GeometryType.LINE);
		gl.setSymbology(new PointSymbology()) ;
		gll.setSymbology(new LineSymbology());
		gl.setEditable(true);
		mapview.getOverlayManager().add(gl);
		mapview.getOverlayManager().add(gll);

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
		gl.addGeometryLayerSingleTapListener(new GeometryLayerSingleTapListener() {
			
			@Override
			public void actionPerformed(PointGeometry p) {
				
				LineGeometry l = new LineGeometry() ;
				l.addPoint(a) ; l.addPoint(p);
				gll.addGeometry(l) ;
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
		gl.addGeometryLayerSingleTapListener(new GeometryLayerSingleTapListener() {
			
			@Override
			public void actionPerformed(PointGeometry p) {
				int c =count.incrementAndGet();
				if(c == 1){
					list.add(p) ;
					gl.addGeometry(p);
					mapview.invalidate();
				}
				if(c == 2 ){
					list.add( p );
					LineGeometry l = new LineGeometry() ;
					l.addPoint(list.get(0));
					l.addPoint(list.get(1)) ;
					gll.addGeometry(l);
					mapview.getOverlayManager().remove(gl);
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
		mapview.getOverlayManager().remove(gl);
		mapview.getOverlayManager().remove(gll);
		
	}
	
}
