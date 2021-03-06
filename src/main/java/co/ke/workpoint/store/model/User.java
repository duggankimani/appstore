package co.ke.workpoint.store.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "user")
@XmlAccessorType(XmlAccessType.FIELD)
public class User extends PO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer id;
	private String firstName;
	private String lastName;
	private String email;
	private String userId;

	public User() {
	}

	public User(String email) {

	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public boolean isSame(String email, String familyName, String givenName,
			int status) {

		if (isSame(familyName, this.lastName)
				&& isSame(givenName, this.firstName)
				&& isSame(this.getIsActive(), status)) {
			return true;
		}

		return false;
	}

	private boolean isSame(String str1, String str2) {
		if (str1 == null ^ str2 == null) {
			//xor
			return false;
		}

		if (str1 != null) {
			return str1.equals(str2);
		}
		
		return false;
	}

	private boolean isSame(int isActive, int status) {
		return isActive == status;
	}
}
