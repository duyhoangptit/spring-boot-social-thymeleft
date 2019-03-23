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

import vn.tiger.social.entity.UserConnection;

/**
 * @author HoangTD5
 *
 *         Mar 22, 2019
 */
@Repository
@Transactional
public class UserConnectionDao {
	@Autowired
	private EntityManager entityManager;

	public UserConnection findUserConnectionByUserProviderId(String userProviderId) {
		try {
			String sql = "SELECT e FROM " + UserConnection.class.getName() + " e "
					+ "WHERE e.userProviderId=:userProviderId";
			Query query = this.entityManager.createQuery(sql, UserConnection.class);

			query.setParameter("userProviderId", userProviderId);

			@SuppressWarnings("unchecked")
			List<UserConnection> userConnectionList = query.getResultList();

			return userConnectionList.isEmpty() ? null : userConnectionList.get(0);
		} catch (NoResultException e) {
			return null;
		}
	}

}
