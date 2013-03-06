package fr.umlv.lastproject.smart;

public enum Theme {
	DARK(R.id.buttonDarkTheme, R.style.AppBaseTheme), LIGHT(
			R.id.buttonLightTheme, R.style.AppLightTheme);

	private final int radioButtonId;
	private final int themeId;

	private Theme(int radioButtonId, int themeId) {
		this.radioButtonId = radioButtonId;
		this.themeId = themeId;
	}

	public int getRadioButtonId() {
		return radioButtonId;
	}

	public int getThemeId() {
		return themeId;
	}

	public static Theme getByRadioButtonId(int radioButtonId) {
		for (Theme theme : values()) {
			if (theme.radioButtonId == radioButtonId) {
				return theme;
			}
		}

		return null;
	}

	public static Theme getByThemeId(int themeId) {
		for (Theme theme : values()) {
			if (theme.themeId == themeId) {
				return theme;
			}
		}

		return null;
	}
}
