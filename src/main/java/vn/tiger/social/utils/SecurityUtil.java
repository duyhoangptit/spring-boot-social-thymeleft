/**
 * 
 */
package vn.tiger.social.utils;

import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.security.SocialUserDetails;

import vn.tiger.social.entity.AppUser;
import vn.tiger.social.service.impl.SocialUserDetailsImpl;

/**
 * @author HoangTD5 Lớp SecurityUtil chứa phương thức tiện ích để tự động đăng
 *         nhập một người dùng vào ứng dụng. Mar 22, 2019
 */
public class SecurityUtil {

	public static void loginUser(AppUser appUser, List<String> roleNames) {
		SocialUserDetails userDetails = new SocialUserDetailsImpl(appUser, roleNames);

		Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
				userDetails.getAuthorities());

		SecurityContextHolder.getContext().setAuthentication(authentication);
	}
}
