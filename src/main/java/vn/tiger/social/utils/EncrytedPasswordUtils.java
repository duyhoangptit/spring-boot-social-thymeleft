/**
 * 
 */
package vn.tiger.social.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @author HoangTD5
 *
 *         Mar 22, 2019
 */
public final class EncrytedPasswordUtils {

	public static String encrytePassword(String password) {
		BCryptPasswordEncoder emcoder = new BCryptPasswordEncoder();

		return emcoder.encode(password);
	}
}
