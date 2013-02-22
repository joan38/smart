package fr.umlv.lastproject.smart.database;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import fr.umlv.lastproject.smart.form.Form;
import fr.umlv.lastproject.smart.form.Mission;

/**
 * Object Mission which can be stored in table "missions"
 * 
 * @author Maelle Cabot
 * 
 */
public class MissionRecord {

	private long id;
	private String title;

	// True if the mission is in progress
	private boolean status;
	private String date;
	private Form form;

	public MissionRecord() {
		if (Mission.getInstance() != null) {
			this.title = Mission.getInstance().getTitle();
			this.form = Mission.getInstance().getForm();
			this.status = true;
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"dd/MM/yyyy HH:mm:ss", Locale.FRENCH);
			this.date = dateFormat.format(new Date());
		}
	}

	/**
	 * 
	 * @return the id of the missionRecord
	 */
	public long getId() {
		return id;
	}

	/**
	 * 
	 * @param id
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * 
	 * @return title of the missionRecord
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * 
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * 
	 * @return true, if the mission is started
	 */
	public boolean isStatus() {
		return status;
	}

	/**
	 * 
	 * @param status
	 */
	public void setStatus(boolean status) {
		this.status = status;
	}

	/**
	 * 
	 * @return the date of the mission
	 */
	public String getDate() {
		return date;
	}

	/**
	 * 
	 * @param date
	 */
	public void setDate(String date) {
		this.date = date;
	}

	/**
	 * 
	 * @return the form associated to the missionRecord
	 */
	public Form getForm() {
		return form;
	}

	/**
	 * 
	 * @param form
	 */
	public void setForm(Form form) {
		this.form = form;
	}

}
