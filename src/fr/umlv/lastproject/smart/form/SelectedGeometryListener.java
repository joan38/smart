package fr.umlv.lastproject.smart.form;

import fr.umlv.lastproject.smart.layers.Geometry;
import fr.umlv.lastproject.smart.layers.GeometryLayer;

public interface SelectedGeometryListener {
	

	void actionPerformed(Geometry g, GeometryLayer l);

}
