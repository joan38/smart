package fr.umlv.lastproject.smart;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import fr.umlv.lastproject.smart.layers.Layer;

/**
 * A class for stock the name of the map overlays
 * 
 * @author Thibault Douilly
 * 
 */
public class ListOverlay implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final List<LayerItem> overlays;

	/**
	 * constructor of list overlay
	 */
	public ListOverlay() {
		overlays = new ArrayList<LayerItem>();
	}

	/*
	 * constructor of list overlay
	 */
	public ListOverlay(List<Layer> layers) {
		this.overlays = new ArrayList<LayerItem>();

		for (Layer layer : layers) {
			overlays.add(new LayerItem(layer.getName(), layer.getOverview()));

		}
	}

	/**
	 * 
	 * @param layer to add
	 * @return if true ok
	 */
	public boolean add(LayerItem item) {
		return overlays.add(item);
	}


	/**
	 * 
	 * @param location
	 * @return the overlya
	 */
	public LayerItem get(int location) {
		return this.overlays.get(location);
	}

	/**
	 * 
	 * @param location the layer which will change the visibility
	 * @param visible if true or false
	 */
	public void setVisible(int location, boolean visible) {
		this.overlays.get(location).setVisible(visible);
	}

	/**
	 * 
	 * @param location the layer which will be removed
	 * @return the layeritem
	 */
	public LayerItem remove(int location) {
		return this.overlays.remove(location);
	}

	/**
	 * 
	 * @param overlay 
	 * @return if the overlay has been removed
	 */
	public boolean remove(String layer) {
		return this.overlays.remove(layer);
	}

	/**
	 * 
	 * @param source the overlay to change with
	 * @param destination this overlay
	 */
	public void reorganize(int source, int destination) {
		this.overlays.add(destination, this.overlays.remove(source));
	}

	/**
	 * 
	 * @return the number of overlay
	 */
	public int size() {
		return this.overlays.size();
	}

	/**
	 * 
	 * @return names of the layers
	 */
	public String[] toArray() {
		String[] array = new String[this.overlays.size()];
		for (int i = 0; i < this.overlays.size(); i++) {
			array[i] = this.overlays.get(i).getName();
		}
		return array;
	}

	/**
	 * 
	 * @return list of layers
	 */

	public List<LayerItem> toList() {
		return this.overlays;

	}

	/**
	 * clear all layers
	 */
	public void clear() {
		this.overlays.clear();
	}

	@Override
	public String toString() {
		String data = "ListOverlay [ ";
		for (int i = 0; i < this.overlays.size(); i++) {
			data += this.overlays.get(i).getName() + " : "
					+ this.overlays.get(i).isVisible() + " ";
		}
		return data + "]";
	}

}
