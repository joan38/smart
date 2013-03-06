package fr.umlv.lastproject.smart;

public enum MenuAction {
	CREATE_MISSION(0), CREATE_FORM(1), POINT_SURVEY(2), POINT_SURVEY_POSITION(3), LINE_SURVEY(
			4), POLYGON_SURVEY(5), POLYGON_TRACK(6), GPS_TRACK(7), IMPORT_KML_SHP(
			8), IMPORT_GEOTIFF(9), IMPORT_WMS(10), MEASURE(11), AREA_MEASURE(12), EXPORT_MISSION(
			13), EXPORT_FORM(14), DELETE_MISSION(15), STOP_MISSION(16), STOP_GPS_TRACK(
			17), STOP_POLYGON_TRACK(18);

	private final int id;

	private MenuAction(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public static MenuAction getFromId(int id) {
		for (MenuAction menuAction : values()) {
			if (menuAction.id == id) {
				return menuAction;
			}
		}

		return null;
	}
}
