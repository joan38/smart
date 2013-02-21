package fr.umlv.lastproject.smart;

import java.io.Serializable;

import android.util.Log;
import fr.umlv.lastproject.smart.layers.SmartIcon;

public class LayerState implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final String name;
	private boolean visible;
	private SmartIcon symbologie;

	public LayerState(String name, boolean visible, SmartIcon symbologie) {
		this.name = name;
		this.visible = visible;
		this.symbologie = symbologie;
		if (symbologie != null)
			Log.d("debug", " LayerState " + symbologie.getType());
	}

	public LayerState(String name, SmartIcon symbologie) {
		this(name, true, symbologie);
	}

	public LayerState(String name) {
		this(name, true, null);
	}

	public String getName() {
		return name;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public SmartIcon getSymbologie() {
		return symbologie;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + (visible ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LayerState other = (LayerState) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (visible != other.visible)
			return false;
		return true;
	}

}