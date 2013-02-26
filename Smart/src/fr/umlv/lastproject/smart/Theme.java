package fr.umlv.lastproject.smart;

public class Theme {

	private static Theme theme = null;

	private String name;

	private Theme(String name) {
		this.name = name;
	}

	public static Theme getInstance() {
		return theme;
	}

	public static Theme createTheme(String name) {
		theme = new Theme(name);
		return theme;
	}

	public int getIntTheme() {
		if (name == null)
			return R.style.AppBaseTheme;
		if (name.equals("light"))
			return R.style.AppLightTheme;
		else
			return R.style.AppBaseTheme;
	}

}
