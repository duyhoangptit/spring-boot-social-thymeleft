/**
 * 
 */
package vn.tiger.social.service.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import vn.tiger.social.dao.AppRoleDao;
import vn.tiger.social.dao.AppUserDao;
import vn.tiger.social.entity.AppUser;

/**
 * @author HoangTD5
 *
 *         Mar 22, 2019
 */
@Service
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private AppUserDao appUserDao;

	@Autowired
	private AppRoleDao appRoleDao;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.core.userdetails.UserDetailsService#
	 * loadUserByUsername(java.lang.String)
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// System.out.println("UserDetailsServiceImpl.loadUserByUsername=" + username);
		AppUser appUser = this.appUserDao.findAppUserByUsername(username);

		if (appUser == null) {
			System.out.println("User not found! " + username);
			throw new UsernameNotFoundException("User " + username + " was not found in the database");
		}

		System.out.println("Found User: " + appUser);

		// [ROLE_USER, ROLE_ADMIN,..]
		List<String> roles = this.appRoleDao.getRoleNames(appUser.getUserId());
		SocialUserDetailsImpl userDetails = new SocialUserDetailsImpl(appUser, roles);

		return userDetails;
	}

}
