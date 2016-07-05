package com.jsantos.ps.objects.mail;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import com.jsantos.ps.objects.tag.TagBean;
import com.jsantos.ps.store.database.DS;

public class MailAccountBean {
	
	String accountname;
	String mailServer;
	String userName;
	String userPass;
	String protocol;
	
	public MailAccountBean(){
		
		
	}
	
	static public MailAccountBean findAccountByName(String name) throws SQLException{
		MailAccountBean retValue = null;
		Connection conn = DS.getConnection();
		try{
			String sql = "select * from mailaccounts where mailaccountname=?";
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setString(1, name);
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				retValue = new MailAccountBean();
				retValue.accountname = name;
				retValue.mailServer = rs.getString("mail_server");
				retValue.userName= rs.getString("mail_login");
				retValue.userPass = rs.getString("mail_pass");
				retValue.protocol = rs.getString("mail_protocol");
			}
		}
		finally{
			conn.close();
		}
		return retValue;
	}
	
	
	
	public void save() throws SQLException{
		
					
	Connection conn = DS.getConnection();
			String sql = "select count(*) from mailaccounts where mailaccountname= ?";
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, this.accountname);
			ResultSet rs = st.executeQuery();
			rs.next();
			if (0<rs.getInt(1)){//update
				sql = "update mailaccounts set mail_server=?,mail_login=?,mail_pass=?,mail_protocol=? where mailaccountname=?";
				st = conn.prepareStatement(sql);
				st.setString(1, this.mailServer);
				st.setString(2, this.userName);
				st.setString(3, this.userPass);
				st.setString(4, this.protocol);
				st.setString(5, this.accountname);
				st.execute();
				
				
			}else{//create
				
				
					sql = "insert into mailaccounts values (?,?,?,?,?,?)";
					st = conn.prepareStatement(sql);
					st.setString(1, UUID.randomUUID().toString());
					st.setString(2, this.accountname);
					st.setString(3, this.mailServer);
					st.setString(4, this.userName);
					st.setString(5, this.userPass);
					st.setString(6, this.protocol);
					st.execute();
					
				}
			
			
			conn.close();
		
	}
	
	
	
	public String getAccountname() {
		return accountname;
	}

	public void setAccountname(String accountname) {
		this.accountname = accountname;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	//setters and getters
	public String getMailServer() {
		return mailServer;
	}
	public void setMailServer(String mailServer) {
		this.mailServer = mailServer;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserPass() {
		return userPass;
	}
	public void setUserPass(String userPass) {
		this.userPass = userPass;
	}
	
	

}
