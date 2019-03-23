/**
 * 
 */
package vn.tiger.social.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * @author HoangTD5
 *
 *         Mar 22, 2019
 */
@Entity
@Table(name = "persistent_logins")
public class PersistentLogin {

	@Id
	@Column(name = "Series", length = 64, nullable = false)
	private String series;

	@Column(name = "Username", length = 64, nullable = false)
	private String username;

	@Column(name = "Token", length = 64, nullable = false)
	private String token;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "Last_Used", nullable = false)
	private Date lastUsed;

	public String getSeries() {
		return series;
	}

	public void setSeries(String series) {
		this.series = series;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Date getLastUsed() {
		return lastUsed;
	}

	public void setLastUsed(Date lastUsed) {
		this.lastUsed = lastUsed;
	}

}
