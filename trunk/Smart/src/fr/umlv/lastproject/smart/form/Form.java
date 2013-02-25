package fr.umlv.lastproject.smart.form;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;
import fr.umlv.lastproject.smart.MenuActivity;
import fr.umlv.lastproject.smart.dialog.FormDialog;
import fr.umlv.lastproject.smart.layers.Geometry;

/**
 * Object form associated at a mission
 * 
 * @author Maelle Cabot
 * 
 */
public class Form implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -30424477427478579L;
	private String title;
	private List<Field> fieldsList;

	private static final String VALUESTAG = "values";
	private static final String MINTAG = "min";
	private static final String MAXTAG = "max";
	private static final String COMMENTS = "Commentaires";
	private static final String DEFAULT_NAME = "FormDefault";
	private static final String CHARSET = "UTF-8";
	private static final String FORMTAG = "form";
	private static final String TITLETAG = "title";
	private static final String FIELDSTAG = "fields";
	private static final String FIELDTAG = "field";
	private static final String TYPETAG = "type";

	/**
	 * Create a Form with a specified name.
	 * 
	 * @param name
	 *            of the form
	 */
	public Form(String name) {
		this.title = name;
		this.fieldsList = new ArrayList<Field>();
		this.fieldsList.add(new TextField(COMMENTS));
	}

	/**
	 * Create a default Form.
	 */
	public Form() {
		this(DEFAULT_NAME);
	}

	/**
	 * Get the name of the form.
	 * 
	 * @return the name of form
	 */
	public String getName() {
		return title;
	}

	/**
	 * Set the name of the form.
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.title = name;
	}

	/**
	 * 
	 * @return the fields list of the form
	 */
	public List<Field> getFieldsList() {
		return fieldsList;
	}

	/**
	 * 
	 * @param fieldsList
	 */
	public void setFieldsList(List<Field> fieldsList) {
		this.fieldsList = fieldsList;
	}

	/**
	 * 
	 * @param f
	 *            is the field to add at the form
	 */
	public void addField(Field f) {
		this.fieldsList.add(f);
	}

	/**
	 * 
	 * @param label
	 *            of the field to delete
	 */
	public void deleteField(String label) {
		for (int i = 0; i < fieldsList.size(); i++) {
			if (fieldsList.get(i).getLabel().equals(label)) {
				fieldsList.remove(i);
			}
		}
	}

	/**
	 * 
	 * @param label
	 *            of the field to search
	 * @return true if the field exists
	 */
	public boolean searchLabel(String label) {
		for (int i = 0; i < fieldsList.size(); i++) {
			if (fieldsList.get(i).getLabel().equals(label)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Open a form after a survey and save the informations
	 * 
	 * @param context
	 *            of application
	 * @param g
	 *            geometry to insert
	 */
	public void openForm(final MenuActivity context, final Geometry g,
			final Mission mission) {
		final FormDialog dialog = new FormDialog(context, this, g, mission);
		dialog.show();
	}

	/**
	 * 
	 * @param path
	 *            the file to load
	 * @throws XmlPullParserException
	 *             if the xml is malformatted
	 * @throws IOException
	 */
	public void read(String path) throws XmlPullParserException, IOException {
		XmlPullParserFactory xml = XmlPullParserFactory.newInstance();
		xml.setNamespaceAware(true);
		XmlPullParser xpp = xml.newPullParser();
		FileInputStream fis = new FileInputStream(new File(path));
		xpp.setInput(fis, CHARSET);
		int eventype = xpp.getEventType();

		while (eventype != XmlPullParser.END_DOCUMENT) {
			if (eventype == XmlPullParser.START_TAG) {
				String tag = xpp.getName();
				Log.d("", "tag" + xpp.getName());
				Log.d("", "tag count attribute " + xpp.getAttributeCount());

				if (FORMTAG.equals(tag)) {
					for (int i = 0; i < xpp.getAttributeCount(); i++) {
						Log.d("", "tag form " + xpp.getAttributeName(i));

						if (xpp.getAttributeName(i).equals(TITLETAG)) {
							setName(xpp.getAttributeValue(i).replace(" ", ""));
						}
					}
				} else if (FIELDTAG.equals(tag)) {
					String type = null;
					String title = null;
					int max = 0;
					int min = 0;
					String values = null;

					for (int i = 0; i < xpp.getAttributeCount(); i++) {
						Log.d("", "tag type" + xpp.getAttributeName(i));
						if (xpp.getAttributeName(i).equals(TYPETAG)) {
							type = xpp.getAttributeValue(i);
							Log.d("", "tag att" + xpp.getAttributeValue(i));

						} else if (xpp.getAttributeName(i).equals(TITLETAG)) {
							title = xpp.getAttributeValue(i).replace(" ", "");
						} else if (xpp.getAttributeName(i).equals(MAXTAG)) {
							max = Integer.valueOf(xpp.getAttributeValue(i));
						} else if (xpp.getAttributeName(i).equals(MINTAG)) {
							min = Integer.getInteger(xpp.getAttributeValue(i));
						} else if (xpp.getAttributeName(i).equals(VALUESTAG)) {
							values = xpp.getAttributeValue(i);
						}
					}

					if (type.equalsIgnoreCase(FieldType.PICTURE.name())) {
						addField(new PictureField(title));
					} else if (type.equalsIgnoreCase(FieldType.HEIGHT.name())) {
						addField(new HeightField(title));
					} else if (type.equalsIgnoreCase(FieldType.BOOLEAN.name())) {
						addField(new BooleanField(title));
					} else if (type.equalsIgnoreCase(FieldType.LIST.name())) {
						String[] list = values.split("/");
						addField(new ListField(title, new ArrayList<String>(
								Arrays.asList(list))));
					} else if (type.equalsIgnoreCase(FieldType.NUMERIC.name())) {
						addField(new NumericField(title, min, max));
					}
				}
			}

			eventype = xpp.next();
		}
	}

	public void write(String path) throws FormExportException {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document xml = docBuilder.newDocument();

			// Form element
			Element formElement = xml.createElement(FORMTAG);
			xml.appendChild(formElement);

			// Set attributes to form element
			Attr titleAttribute = xml.createAttribute(TITLETAG);
			titleAttribute.setValue(title);
			formElement.setAttributeNode(titleAttribute);

			// Fields element
			Element fieldsElement = xml.createElement(FIELDSTAG);
			formElement.appendChild(fieldsElement);

			if (fieldsList.size() < 1) {
				throw new FormExportException("No field in the given Form");
			}
			for (Field field : fieldsList) {
				// Field element
				Element fieldElement = xml.createElement(FIELDTAG);
				fieldsElement.appendChild(fieldElement);

				// Set attributes to field element
				Attr typeAttribute = xml.createAttribute(TYPETAG);
				typeAttribute.setValue(field.getType().name());
				fieldElement.setAttributeNode(typeAttribute);

				Attr titleFieldAttribute = xml.createAttribute(TITLETAG);
				titleFieldAttribute.setValue(field.getLabel());
				fieldElement.setAttributeNode(titleFieldAttribute);

				switch (field.getType()) {
				case BOOLEAN:
				case HEIGHT:
				case TEXT:
				case PICTURE:
					// Nothing more
					break;

				case NUMERIC:
					NumericField nf = (NumericField) field;
					
					Attr minAttribute = xml.createAttribute(MINTAG);
					minAttribute.setValue(String.valueOf(nf.getMin()));
					fieldElement.setAttributeNode(minAttribute);
					
					Attr maxAttribute = xml.createAttribute(MINTAG);
					maxAttribute.setValue(String.valueOf(nf.getMax()));
					fieldElement.setAttributeNode(maxAttribute);
					break;

				case LIST:
					ListField lf = (ListField) field;
					Attr valueAttribute = xml.createAttribute(VALUESTAG);
					
					StringBuilder values = new StringBuilder();
					for (String value : lf.getValues()) {
						values.append(value).append("/");
					}
					values.deleteCharAt(values.length() - 1);
					valueAttribute.setValue(values.toString());
					
					fieldElement.setAttributeNode(valueAttribute);
					break;

				default:
					throw new IllegalStateException("Unkown field");
				}
			}

			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(xml);
			StreamResult result = new StreamResult(path + "/" + title + ".xml");

			transformer.transform(source, result);
		} catch (ParserConfigurationException e) {
			throw new FormExportException("Unable to export the form", e);
		} catch (TransformerException e) {
			throw new FormExportException("Unable to export the form", e);
		}
	}
}
