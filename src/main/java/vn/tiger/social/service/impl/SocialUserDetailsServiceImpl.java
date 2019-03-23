/**
 * 
 */
package vn.tiger.social.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.social.security.SocialUserDetailsService;
import org.springframework.stereotype.Service;

/**
 * @author HoangTD5
 *
 *         Mar 23, 2019
 */
@Service
public class SocialUserDetailsServiceImpl implements SocialUserDetailsService {

	@Autowired
	private UserDetailsService userDetailsService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.social.security.SocialUserDetailsService#loadUserByUserId
	 * (java.lang.String)
	 */
	@Override
	public SocialUserDetails loadUserByUserId(String username) throws UsernameNotFoundException, DataAccessException {
		System.out.println("SocialUserDetailsServiceImpl.loadUserByUserId=" + username);

		UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

		return (SocialUserDetailsImpl) userDetails;
	}

}
