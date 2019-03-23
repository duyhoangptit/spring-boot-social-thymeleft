/**
 * 
 */
package vn.tiger.social.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.social.security.SocialUserDetails;

import vn.tiger.social.entity.AppUser;

/**
 * @author HoangTD5
 *
 *         Mar 22, 2019
 */
public class SocialUserDetailsImpl implements SocialUserDetails {

	private static final long serialVersionUID = -5246117266247684905L;

	private List<GrantedAuthority> gratedList = new ArrayList<>();
	private AppUser appUser;

	public SocialUserDetailsImpl(AppUser appUser, List<String> roleNames) {
		this.appUser = appUser;
		if (roleNames != null) {
			for (String role : roleNames) {
				GrantedAuthority authority = new SimpleGrantedAuthority(role);
				this.gratedList.add(authority);
			}
		}
	}

	@Override
	public String getUserId() {
		return this.appUser.getUserId() + "";
	}

	@Override
	public String getUsername() {
		return appUser.getUsername();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return gratedList;
	}

	@Override
	public String getPassword() {
		return appUser.getEncrytedPassword();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
