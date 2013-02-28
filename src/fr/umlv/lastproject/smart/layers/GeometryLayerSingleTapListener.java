package fr.umlv.lastproject.smart.layers;

/**
 * This class is used to send an event when the layer is taped only once
 * 
 * @author thibault
 * 
 */
public interface GeometryLayerSingleTapListener {

	/**
	 * 
	 * @param p
	 *            the point where the layer has been taped
	 */
	void actionPerformed(PointGeometry p);

}
