/**
 * 
 */
package vn.tiger.social.utils;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author HoangTD5
 *
 *         Mar 22, 2019
 */
public class WebUtils {
	public static String toString(UserDetails user) {
		StringBuilder sb = new StringBuilder();

		sb.append("Username:").append(user.getUsername());
		Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

		if (authorities != null && !authorities.isEmpty()) {
			sb.append(" (");
			boolean first = true;
			for (GrantedAuthority a : authorities) {
				if (first) {
					sb.append(a.getAuthority());
					first = false;
				} else {
					sb.append(", ").append(a.getAuthority());
				}
			}
			sb.append(")");
		}
		return sb.toString();
	}
}
