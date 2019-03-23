/**
 * 
 */
package vn.tiger.social.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.social.UserIdSource;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurer;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.connect.web.ConnectController;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.google.connect.GoogleConnectionFactory;
import org.springframework.social.linkedin.connect.LinkedInConnectionFactory;
import org.springframework.social.security.AuthenticationNameUserIdSource;
import org.springframework.social.twitter.connect.TwitterConnectionFactory;

import vn.tiger.social.dao.AppUserDao;
import vn.tiger.social.service.impl.ConnectionSignUpImpl;

/**
 * @author HoangTD5
 *
 *         Mar 22, 2019
 */
@Configuration
@EnableSocial
@PropertySource("classpath:socical-cfg.properties")
public class SocialConfig implements SocialConfigurer {

	private boolean autoSignUp = false;

	@Autowired
	private DataSource dataSource;

	@Autowired
	private AppUserDao appUserDao;

	// @env: read from social-cfg.properties file.
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.social.config.annotation.SocialConfigurer#
	 * addConnectionFactories(org.springframework.social.config.annotation.
	 * ConnectionFactoryConfigurer, org.springframework.core.env.Environment)
	 */
	@Override
	public void addConnectionFactories(ConnectionFactoryConfigurer cfg, Environment env) {
		try {
			this.autoSignUp = Boolean.parseBoolean(env.getProperty("social.auto-signup"));
		} catch (Exception e) {
			this.autoSignUp = false;
		}

		// Twitter
		TwitterConnectionFactory tfactory = new TwitterConnectionFactory(env.getProperty("twitter.consumer.key"),
				env.getProperty("twitter.consumer.secret"));
		// tfactory.setScope(env.getProperty("twitter.scope"));
		cfg.addConnectionFactory(tfactory);

		// Facebook
		FacebookConnectionFactory ffactory = new FacebookConnectionFactory(env.getProperty("facebook.app.id"),
				env.getProperty("facebook.app.secret"));
		ffactory.setScope(env.getProperty("facebook.app.scope"));

		// auth_type = reauthenticate
		cfg.addConnectionFactory(ffactory);

		// Linkedin
		LinkedInConnectionFactory lfactory = new LinkedInConnectionFactory(//
				env.getProperty("linkedin.consumer.key"), //
				env.getProperty("linkedin.consumer.secret"));

		lfactory.setScope(env.getProperty("linkedin.scope"));

		cfg.addConnectionFactory(lfactory);

		// Google
		GoogleConnectionFactory gfactory = new GoogleConnectionFactory(env.getProperty("google.client.id"),
				env.getProperty("google.client.secret"));

		gfactory.setScope(env.getProperty("google.scope"));
		cfg.addConnectionFactory(gfactory);

	}

	/*
	 * The UserIdSource determines the userId of the user
	 * 
	 * @see
	 * org.springframework.social.config.annotation.SocialConfigurer#getUserIdSource
	 * ()
	 */
	@Override
	public UserIdSource getUserIdSource() {
		return new AuthenticationNameUserIdSource();
	}

	/*
	 * User connection
	 * 
	 * @see org.springframework.social.config.annotation.SocialConfigurer#
	 * getUsersConnectionRepository(org.springframework.social.connect.
	 * ConnectionFactoryLocator)
	 */
	@Override
	public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator cfl) {
		// org.springframework.social.security.SocialAuthenticationServiceRegistry
		JdbcUsersConnectionRepository usersConnectionRepository = new JdbcUsersConnectionRepository(dataSource, cfl,
				Encryptors.noOpText());

		if (autoSignUp) {
			// After logging in to social networking
			// Automatically creates corresponding APP_USER if it does not exits
			ConnectionSignUp connectionSignUp = new ConnectionSignUpImpl(appUserDao);
			usersConnectionRepository.setConnectionSignUp(connectionSignUp);
		} else {
			// After loggin in to social networking
			// If the corresponding APP_USER record is not found
			// Navigate to registration page
			usersConnectionRepository.setConnectionSignUp(null);
		}

		return usersConnectionRepository;
	}

	/**
	 * This bean manages the connection flow between the account provider and the
	 * example application
	 * 
	 * @param connectionFactoryLocator
	 * @param connectionRepository
	 * @return
	 */
	@Bean
	public ConnectController connectController(ConnectionFactoryLocator connectionFactoryLocator,
			ConnectionRepository connectionRepository) {
		return new ConnectController(connectionFactoryLocator, connectionRepository);
	}
}
