package fr.umlv.lastproject.smart.layers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.views.overlay.Overlay;

import android.graphics.Bitmap;
import fr.umlv.lastproject.smart.data.TMSOverlay;

/**
 * Main interface for all kind of layers : {@link GeometryLayer} ,
 * {@link TMSOverlay}
 * 
 * @author Marc
 * 
 */
public interface Layer {

	class Extent implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private BoundingBoxE6 boundingBox;
		private int zoom;

		public Extent(BoundingBoxE6 bounding, int zoom) {
			this.boundingBox = bounding;
			this.zoom = zoom;
		}

		public Extent(BoundingBoxE6 bounding) {
			this(bounding, -1);
		}

		/**
		 * @return the boundingBox
		 */
		public BoundingBoxE6 getBoundingBox() {
			return boundingBox;
		}

		/**
		 * @return the zoom
		 */
		public int getZoom() {
			return zoom;
		}

		/**
		 * 
		 * @param out
		 *            the object to get
		 * @throws IOException
		 *             if canot read
		 */
		private void writeObject(ObjectOutputStream out) throws IOException {
			out.writeInt(zoom);
			out.writeDouble(boundingBox.getLatNorthE6());
			out.writeDouble(boundingBox.getLatSouthE6());
			out.writeDouble(boundingBox.getLonWestE6());
			out.writeDouble(boundingBox.getLonEastE6());
			out.close();

		}

		/**
		 * 
		 * @param in
		 *            object to read
		 * @throws IOException
		 *             if object not readable
		 * @throws ClassNotFoundException
		 *             if class does not exist
		 */
		private void readObject(ObjectInputStream in) throws IOException,
				ClassNotFoundException {
			this.zoom = in.readInt();
			final double north = in.readDouble();
			final double south = in.readDouble();
			final double west = in.readDouble();
			final double east = in.readDouble();
			this.boundingBox = new BoundingBoxE6(north, east, south, west);
			in.close();

		}

	}

	/**
	 * 
	 * @return name of the layer
	 */
	String getName();

	/**
	 * If layer instanceof TMSOverlay : generic image
	 * 
	 * If layer instanceof GeometryLayer : dynamic image generated with
	 * symbology
	 * 
	 * @return the bitmap associated to the layer
	 * 
	 * 
	 */
	Bitmap getOverview();

	/**
	 * Casts the layer in overlay
	 * 
	 * @return overlay wrapped in layer
	 */
	Overlay getOverlay();

	boolean hasSymbologyEditable();

	Extent getExtent();

}
