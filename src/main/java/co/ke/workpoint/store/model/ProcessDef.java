package co.ke.workpoint.store.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="process")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProcessDef extends PO{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String NAME = "name";

	public static final String PROCESSID = "processid";

	public static final String BACKGROUNDCOLOR = "backgroundcolor";

	public static final String ICONSTYLE = "iconstyle";

	public static final String FILENAME = "filename";

	public static final String DESCRIPTION = "description";

	public static final String IMAGENAME = "imagename";

	public static final String CATEGORY = "category";

	public static final String ID = "id";

	private Integer id;
	private String name;
	private String description;
	private String iconStyle;
	private String backgroundColor;
	private String processIcon;
	private String category;
	private String downloadUrl;
	private Status status;
	private String processId;
	private String fileName;
	private String imageName;
	
	public ProcessDef() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getIconStyle() {
		return iconStyle;
	}

	public void setIconStyle(String iconStyle) {
		this.iconStyle = iconStyle;
	}

	public String getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public String getProcessIcon() {
		return processIcon;
	}

	public void setProcessIcon(String processIcon) {
		this.processIcon = processIcon;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}
}
