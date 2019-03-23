/**
 * 
 */
package vn.tiger.social.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import vn.tiger.social.entity.AppRole;
import vn.tiger.social.entity.AppUser;
import vn.tiger.social.entity.UserRole;

/**
 * @author HoangTD5
 *
 *         Mar 22, 2019
 */
@Repository
@Transactional
public class AppRoleDao {

	@Autowired
	private EntityManager entityManager;

	@SuppressWarnings("unchecked")
	public List<String> getRoleNames(Long userId) {
		String sql = "SELECT ur.appRole.roleName from " + UserRole.class.getName() + " ur "
				+ "WHERE ur.appUser.userId=:userId";
		Query query = this.entityManager.createQuery(sql, String.class);

		query.setParameter("userId", userId);

		return query.getResultList();
	}

	public AppRole findAppRoleByName(String roleName) {
		try {
			String sql = "Select e from " + AppRole.class.getName() + " e " //
					+ " where e.roleName = :roleName ";

			Query query = this.entityManager.createQuery(sql, AppRole.class);
			query.setParameter("roleName", roleName);
			return (AppRole) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public void ceateRoleFor(AppUser appUser, List<String> roleNames) {
		for (String roleName : roleNames) {
			AppRole appRole = this.findAppRoleByName(roleName);

			if (appRole == null) {
				appRole = new AppRole();

				appRole.setRoleName(roleName);

				this.entityManager.persist(appRole);
				// commit create appRole to DB
				this.entityManager.flush();
			}

			UserRole userRole = new UserRole();

			userRole.setAppRole(appRole);
			userRole.setAppUser(appUser);

			this.entityManager.persist(userRole);
			// commit create userRole to DB
			this.entityManager.flush();
		}
	}
}
