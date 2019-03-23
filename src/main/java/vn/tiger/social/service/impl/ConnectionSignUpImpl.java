/**
 * 
 */
package vn.tiger.social.service.impl;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;

import vn.tiger.social.dao.AppUserDao;
import vn.tiger.social.entity.AppUser;

/**
 * @author HoangTD5 Trong lần đầu tiên người dùng đăng nhập với tài khoản mạng
 *         xã hội của họ, ứng dụng sẽ có được đối tượng Connection, đối tượng
 *         này chứa hồ sơ ( Profile) của người dùng. Bạn sẽ viết code trong lớp
 *         ConnectionSignUpImpl để tạo ra một bản ghi của bảng APP_USER. (Xem
 *         thêm code trong lớp SocialConfig). Mar 22, 2019
 */
public class ConnectionSignUpImpl implements ConnectionSignUp {

	private AppUserDao appUserDao;

	public ConnectionSignUpImpl(AppUserDao appUserDao) {
		this.appUserDao = appUserDao;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.social.connect.ConnectionSignUp#execute(org.
	 * springframework.social.connect.Connection)
	 */
	@Override
	public String execute(Connection<?> connection) {
		AppUser account = appUserDao.createAppUser(connection);

		return account.getUsername();
	}

}
