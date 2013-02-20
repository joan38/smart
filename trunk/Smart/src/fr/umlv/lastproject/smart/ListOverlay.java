package fr.umlv.lastproject.smart;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.osmdroid.views.overlay.Overlay;

import fr.umlv.lastproject.smart.geotiff.TMSOverlay;
import fr.umlv.lastproject.smart.layers.GeometryLayer;

public class ListOverlay implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final List<String> overlays;

	public ListOverlay() {
		overlays = new ArrayList<String>();
	}

	public ListOverlay(List<GeometryLayer> layers) {
		this.overlays = new ArrayList<String>();
		for (GeometryLayer layer : layers) {
			overlays.add(layer.getName());
		}
	}

	public boolean add(GeometryLayer layer) {
		return this.overlays.add(layer.getName());
	}

	public boolean addAll(List<GeometryLayer> layers) {
		for (GeometryLayer layer : layers) {
			overlays.add(layer.getName());
		}
		return true;
	}

	public String get(int location) {
		return this.overlays.get(location);
	}

	public String remove(int location) {
		return this.overlays.remove(location);
	}

	public boolean remove(Overlay overlay) {
		return this.overlays.remove(overlay);
	}

	public void reorganize(int source, int destination) {
		this.overlays.add(destination, this.overlays.remove(source));
	}

	public int size() {
		return this.overlays.size();
	}

	public String[] toArray() {
		String[] array = new String[this.overlays.size()];
		for (int i = 0; i < this.overlays.size(); i++) {
			array[i] = this.overlays.get(i);
		}
		return array;
	}

	@Override
	public String toString() {
		String data = "ListOverlay [ ";
		for (int i = 0; i < this.overlays.size(); i++) {
			data += this.overlays.get(i) + " ";
		}
		return data + "]";
	}

	public boolean add(TMSOverlay overlay) {
		return this.overlays.add(overlay.getName());
	}

}
