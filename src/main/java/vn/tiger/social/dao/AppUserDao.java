/**
 * 
 */
package vn.tiger.social.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.UserProfile;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import vn.tiger.social.entity.AppRole;
import vn.tiger.social.entity.AppUser;
import vn.tiger.social.utils.EncrytedPasswordUtils;

/**
 * @author HoangTD5
 *
 *         Mar 22, 2019
 */
@Repository
@Transactional
public class AppUserDao {

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private AppRoleDao appRoleDao;

	public AppUser findAppUserByUserId(Long userId) {
		try {
			String sql = "SELECT e FROM " + AppUser.class.getName() + " e " + "WHERE e.userId=:userId";

			Query query = entityManager.createQuery(sql, AppUser.class);

			query.setParameter("userId", userId);

			return (AppUser) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public AppUser findAppUserByUsername(String username) {
		try {
			String sql = "SELECT e FROM " + AppUser.class.getName() + " e " + "WHERE e.username=:username";

			Query query = this.entityManager.createQuery(sql, AppUser.class);

			query.setParameter("username", username);

			return (AppUser) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	// Auto create App User Account.
	public AppUser createAppUser(Connection<?> connection) {

		ConnectionKey key = connection.getKey();

		System.out.println("key= (" + key.getProviderId() + "," + key.getProviderUserId() + ")");
		UserProfile userProfile = connection.fetchUserProfile();
		String email = userProfile.getEmail();
		AppUser appUser = this.findByEmail(email);

		if (appUser != null) {
			return appUser;
		}

		String username_prefix = userProfile.getFirstName().trim().toLowerCase() + "_"
				+ userProfile.getLastName().trim().toLowerCase();
		String username = this.findAvailableUsername(username_prefix);

		// Random password
		String randomPassword = UUID.randomUUID().toString().substring(0, 5);
		String encrytedPassword = EncrytedPasswordUtils.encrytePassword(randomPassword);
		//
		appUser = new AppUser();
		appUser.setEnabled(true);
		appUser.setEncrytedPassword(encrytedPassword);
		appUser.setUsername(username);
		appUser.setEmail(email);
		appUser.setFirstName(userProfile.getFirstName());
		appUser.setLastName(userProfile.getFirstName());

		this.entityManager.persist(appUser);

		// Create default ROLE
		List<String> roles = new ArrayList<>();

		roles.add(AppRole.ROLE_USER);
		this.appRoleDao.ceateRoleFor(appUser, roles);

		return appUser;
	}

	public AppUser findByEmail(String email) {
		try {
			String sql = "SELECT e FROM " + AppUser.class.getName() + " e " + "WHERE e.email=:email";

			Query query = this.entityManager.createQuery(sql, AppUser.class);

			query.setParameter("email", email);

			return (AppUser) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	private String findAvailableUsername(String username_prefix) {
		AppUser account = this.findAppUserByUsername(username_prefix);

		if (account == null) {
			return username_prefix;
		}

		int i = 0;
		while (true) {
			String username = username_prefix + "_" + i;
			account = this.findAppUserByUsername(username);

			if (account == null) {
				return username;
			}
		}
	}

	public AppUser registerNewUserAccount(AppUser appUser, List<String> roleNames) {
		appUser.setEnabled(true);

		this.entityManager.persist(appUser);
		this.entityManager.flush();

		this.appRoleDao.ceateRoleFor(appUser, roleNames);

		return appUser;
	}

}
