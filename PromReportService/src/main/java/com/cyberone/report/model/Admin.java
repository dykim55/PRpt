package com.cyberone.report.model;

import java.util.Date;

public class Admin {

	private static final long serialVersionUID = 8759865517324068157L;
	private int adminCode = 0;
	private int groupCode = 0;
	private Boolean defaultAccount = false;
	private String loginID = "";
	private String salt = "";
	private String password = "";
	private int passFailedCount = 0;
	private int otpFailedCount = 0;
	private String status = "";
	private String adminName = "";
	private String mobilePhone = "";
	private String email = "";
	private String department = "";
	private String createID = "";
	private Date createTime = null;
	private Date lastLoginTime = null;
	private Date passwordUpdateTime = null;
	private String memo = "";
	private boolean agree = false;
	
	public Admin() {
		
	}	
	
	public Admin(Admin a) {			
		this.set(a);
	}
	
	public void set(Admin a) {
		this.adminCode = a.adminCode;
		this.groupCode = a.groupCode;
		this.defaultAccount = a.defaultAccount;
		this.loginID = a.loginID;
		this.createID = a.createID;
		this.salt = a.salt;
		this.password = a.password;
		this.passFailedCount = a.passFailedCount;
		this.otpFailedCount = a.otpFailedCount;
		this.status = a.status;
		this.adminName = a.adminName;
		this.mobilePhone = a.mobilePhone;
		this.email = a.email;
		this.department = a.department;
		this.createTime = a.createTime;
		this.lastLoginTime = a.lastLoginTime;
		this.passwordUpdateTime = a.passwordUpdateTime;
		this.memo = a.memo;
		this.agree = a.agree;
	}		


	public Boolean getDefaultAccount() {
		return defaultAccount;
	}

	public void setDefaultAccount(Boolean defaultAccount) {
		this.defaultAccount = defaultAccount;
	}

	public int getAdminCode() {
		return adminCode;
	}

	public void setAdminCode(int adminCode) {
		this.adminCode = adminCode;
	}

	public int getGroupCode() {
		return groupCode;
	}

	public void setGroupCode(int groupCode) {
		this.groupCode = groupCode;
	}

	public String getLoginID() {
		return loginID;
	}

	public void setLoginID(String loginID) {
		this.loginID = loginID;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getPassFailedCount() {
		return passFailedCount;
	}
	
	public int getLoginFailedCount() {
		return passFailedCount + otpFailedCount;
	}
	
	public void initLoginFailedCount() {
		passFailedCount = 0;
		otpFailedCount = 0;
	}

	public void setPassFailedCount(int passFailedCount) {
		this.passFailedCount = passFailedCount;
	}
	
	public void IncPassFailedCount(){
		this.passFailedCount++;
	}
	
	public int getOtpFailedCount() {
		return otpFailedCount;
	}

	public void setOtpFailedCount(int otpFailedCount) {
		this.otpFailedCount = otpFailedCount;
	}
	
	public void incOtpFailedCount() {
		this.otpFailedCount++;
	}	

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAdminName() {
		return adminName;
	}

	public void setAdminName(String adminName) {
		this.adminName = adminName;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getPasswordUpdateTime() {
		return passwordUpdateTime;
	}

	public void setPasswordUpdateTime(Date passwordUpdateTime) {
		this.passwordUpdateTime = passwordUpdateTime;
	}


	public Date getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getCreateID() {
		return createID;
	}

	public void setCreateID(String createID) {
		this.createID = createID;
	}

	public String getSalt() {
		return salt;		
	}
	
	public void setSalt(String salt) {
		this.salt = salt;
	}
	
	public boolean isAgree() {
		return agree;
	}

	public void setAgree(boolean agree) {
		this.agree = agree;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Admin [adminCode=");
		builder.append(adminCode);
		builder.append(", groupCode=");
		builder.append(groupCode);
		builder.append(", loginID=");
		builder.append(loginID);
		builder.append(", adminName=");
		builder.append(adminName);
		builder.append(", lastLoginTime=");
		builder.append(lastLoginTime);
		builder.append("]");
		return builder.toString();
	}	
	
	
}
