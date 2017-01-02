package co.ke.workpoint.store.model;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import co.ke.workpoint.store.helpers.IDUtils;
import co.ke.workpoint.store.helpers.SessionHelper;

@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class PO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@XmlAttribute
	protected String refId;
	
	@XmlTransient
	private String createdBy;
	
	@XmlTransient
	private String updatedBy;
	
	@XmlTransient
	private Date created;
	
	@XmlTransient
	private Date updated;
	
	@XmlTransient
	private int isActive=1;

	public void init(){
		if(this.getId()==null){
			created = new Date(System.currentTimeMillis());
			createdBy = null;
		}else{
			updated= new Date(System.currentTimeMillis());
			createdBy=null;
		}
	}
	
	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getUpdated() {		
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}
	
	public abstract Integer getId();
	
	public void onPrePersist(){
		this.created=new Date();
		this.createdBy = SessionHelper.getCurrentUser()==null? null : SessionHelper.getCurrentUser().getUserId();
		
		if(refId==null){
			refId = IDUtils.generateId();
		}
	}
	
	public void onPreUpdate(){
		this.updated=new Date();
		this.updatedBy = SessionHelper.getCurrentUser()==null? null : SessionHelper.getCurrentUser().getUserId();
	}

	public int getIsActive() {
		return isActive;
	}

	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}		
	
	public Date getLastModified(){
		if(updated!=null){
			return updated;
		}
		
		return created;
	}

	public String getRefId() {
		return refId;
	}

	public void setRefId(String refId) {
		this.refId = refId;
	}
}
