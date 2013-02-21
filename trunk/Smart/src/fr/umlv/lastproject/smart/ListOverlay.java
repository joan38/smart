package fr.umlv.lastproject.smart;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.util.Log;
import fr.umlv.lastproject.smart.layers.SmartIcon;

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

	private final List<LayerState> overlays;

	public ListOverlay() {
		overlays = new ArrayList<LayerState>();
	}

	public ListOverlay(List<LayerState> layers) {
		this.overlays = new ArrayList<LayerState>();
		for (LayerState layer : layers) {
			overlays.add(new LayerState(layer.getName(), layer.isVisible(),
					layer.getSymbologie()));
		}
	}

	/**
	 * 
	 * @param layer
	 * @return
	 */
	public boolean add(String layer) {
		return this.overlays.add(new LayerState(layer));
	}

	/**
	 * 
	 * @param layer
	 * @param symbologie
	 * @return
	 */
	public boolean add(String layer, SmartIcon symbologie) {
		Log.d("debug", "hahhahah " + symbologie.getType());
		return this.overlays.add(new LayerState(layer, symbologie));
	}

	/**
	 * 
	 * @param location
	 * @return
	 */
	public LayerState get(int location) {
		return this.overlays.get(location);
	}

	/**
	 * 
	 * @param location
	 * @param visible
	 */
	public void setVisible(int location, boolean visible) {
		this.overlays.get(location).setVisible(visible);
	}

	/**
	 * 
	 * @param location
	 * @return
	 */
	public LayerState remove(int location) {
		return this.overlays.remove(location);
	}

	/**
	 * 
	 * @param overlay
	 * @return
	 */
	public boolean remove(String layer) {
		return this.overlays.remove(layer);
	}

	/**
	 * 
	 * @param source
	 * @param destination
	 */
	public void reorganize(int source, int destination) {
		this.overlays.add(destination, this.overlays.remove(source));
	}

	/**
	 * 
	 * @return
	 */
	public int size() {
		return this.overlays.size();
	}

	/**
	 * 
	 * @return
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
	 * @return
	 */
	public List<LayerState> toList() {
		return Collections.unmodifiableList(this.overlays);
	}

	/**
	 * 
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