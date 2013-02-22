package fr.umlv.lastproject.smart;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class LayerItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private  String name;
	private boolean visible;
	private  Bitmap overview;

	public LayerItem(String name, boolean visible, Bitmap overview) {
		this.name = name;
		this.visible = visible;
		this.overview = overview;
	}

	public LayerItem(String name, Bitmap overview) {
		this(name, true, overview);
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

	public Bitmap getOverview() {
		return overview;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((overview == null) ? 0 : overview.hashCode());
		result = prime * result + (visible ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		LayerItem other = (LayerItem) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name)){
			return false;
		}
		if (overview == null) {
			if (other.overview != null)
				return false;
		} else if (!overview.equals(other.overview)){
			return false;
		}
		if (visible != other.visible){
			return false;
		}
		return true;
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeObject(name);
		out.writeBoolean(visible);

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		overview.compress(Bitmap.CompressFormat.PNG, 100, stream);
		byte[] imageByteArray = stream.toByteArray();

		int length = imageByteArray.length;
		out.writeInt(length);
		out.write(imageByteArray);
	}

	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		this.name = (String) in.readObject();
		this.visible = in.readBoolean();

		int imageByteArrayLength = in.readInt();
		byte[] imageByteArray = new byte[imageByteArrayLength];
		in.read(imageByteArray, 0, imageByteArrayLength);

		this.overview = BitmapFactory.decodeByteArray(imageByteArray, 0,
				imageByteArrayLength);

	}

}
