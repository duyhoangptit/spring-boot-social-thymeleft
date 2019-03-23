/**
 * 
 */
package vn.tiger.social.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.rememberme.InMemoryTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.social.security.SpringSocialConfigurer;

import vn.tiger.social.entity.AppRole;

/**
 * @author HoangTD5
 *
 *         Mar 22, 2019
 */
@Configurable
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private DataSource dataSource;

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		// Sét đặt dịch vụ để tìm kiếm User trong Database.
		// Và sét đặt PasswordEncoder.
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// disable csrf
		http.csrf().disable();

		// Pages do not require login
		http.authorizeRequests().antMatchers("/", "login", "sigup", "logout").permitAll();
		// For user only
		http.authorizeRequests().antMatchers("/userInfo").access("hasRole('" + AppRole.ROLE_USER + "')");
		// For admin only
		http.authorizeRequests().antMatchers("/admin").access("hasRole('" + AppRole.ROLE_ADMIN + "')");

		// When the user has logged in as XX
		// But access a page that requires role YY
		// AccessDeniedException will be thrown
		http.authorizeRequests().and().exceptionHandling().accessDeniedPage("/403");

		// Form login config
		http.authorizeRequests().and().formLogin()
				// submit URL of page login
				.loginProcessingUrl("/j_spring_security_check")// submit URL
				.loginPage("/login").defaultSuccessUrl("/userInfo").failureUrl("/login?error=true")
				.usernameParameter("username").passwordParameter("password");

		// Logout config
		http.authorizeRequests().and().logout().logoutUrl("/logout").logoutSuccessUrl("/logout-successful-page");

		// Remember me
		http.authorizeRequests().and().rememberMe().tokenRepository(this.persistentTokenRepository())
				.tokenValiditySeconds(1 * 24 * 60 * 60);// 24h

		// Spring socical config
		http.apply(new SpringSocialConfigurer()).signupUrl("/signup");

	}

	@Override
	protected UserDetailsService userDetailsService() {
		return this.userDetailsService;
	}

	// https://docs.spring.io/spring-security/site/docs/5.0.0.BUILD-SNAPSHOT/reference/htmlsingle/#getting-started-experience
	@Bean
	public static BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	// Token stored in Table
	@Bean
	public PersistentTokenRepository persistentTokenRepository() {
		JdbcTokenRepositoryImpl db = new JdbcTokenRepositoryImpl();
		db.setDataSource(dataSource);

		return db;
	}

	// Token stored in Memory Server
	@Bean
	public PersistentTokenRepository persistentTokenRepositoryM() {
		InMemoryTokenRepositoryImpl memory = new InMemoryTokenRepositoryImpl();
		return memory;
	}
}
