/**
 * @(#)RequestEntity.java Created on Nov 5, 2009 11:55:24 AM
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.ws;

import java.io.Serializable;

/**
 * @description  
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 */
public class RequestArgs implements Serializable {

	private static final long serialVersionUID = 4073891678518097002L;
	private String userUint ;

	private String empiId;
	
	private String phrId;
	
	private String user ;
	
	private String password;
	
	private String role;

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getEmpiId() {
		return empiId;
	}

	public void setEmpiId(String empiId) {
		this.empiId = empiId;
	}

	public String getPhrId() {
		return phrId;
	}

	public void setPhrId(String phrId) {
		this.phrId = phrId;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUserUint() {
		return userUint;
	}

	public void setUserUint(String userUint) {
		this.userUint = userUint;
	}
	 

}
