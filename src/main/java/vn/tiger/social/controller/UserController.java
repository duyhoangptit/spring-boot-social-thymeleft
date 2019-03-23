/**
 * 
 */
package vn.tiger.social.controller;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import vn.tiger.social.dao.AppUserDao;
import vn.tiger.social.entity.AppRole;
import vn.tiger.social.entity.AppUser;
import vn.tiger.social.form.AppUserForm;
import vn.tiger.social.utils.EncrytedPasswordUtils;
import vn.tiger.social.utils.SecurityUtil;
import vn.tiger.social.utils.WebUtils;
import vn.tiger.social.validator.AppUserFormValidator;

/**
 * @author HoangTD5
 *
 *         Mar 22, 2019
 */
@Controller
public class UserController {

	@Autowired
	private AppUserDao appUserDao;

	@Autowired
	private ConnectionFactoryLocator connectionFactoryLocator;

	@Autowired
	private UsersConnectionRepository usersConnectionRepository;

	@Autowired
	private AppUserFormValidator appUserValidator;

	@InitBinder
	protected void initBinder(WebDataBinder dataBinder) {
		// Form target
		Object target = dataBinder.getTarget();
		if (target == null) {
			return;
		}
		System.out.println("Target=" + target);

		if (target.getClass() == AppUserForm.class) {
			dataBinder.setValidator(appUserValidator);
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			dateFormat.setLenient(false);
			dataBinder.registerCustomEditor(Date.class, "dob", new CustomDateEditor(dateFormat, true));
		}
	}

	@RequestMapping(value = { "/", "/welcome" }, method = RequestMethod.GET)
	public String welcome(Model model) {
		model.addAttribute("title", "Welcome");
		model.addAttribute("message", "This is welcome page!");
		return "welcome-page";
	}

	@RequestMapping(value = "/admin", method = RequestMethod.GET)
	public String adminPage(Model model, Principal principal) {
		// After user login successfully.
		String username = principal.getName();

		System.out.println("User Name: " + username);

		UserDetails loginedUser = (UserDetails) ((Authentication) principal).getPrincipal();

		String userInfo = WebUtils.toString(loginedUser);
		model.addAttribute("userInfo", userInfo);

		return "admin-page";
	}

	@RequestMapping(value = "/logoutSuccessful", method = RequestMethod.GET)
	public String logoutSuccessfulPage(Model model) {
		model.addAttribute("title", "Logout");
		return "logout-successful-page";
	}

	@RequestMapping(value = "/userInfo", method = RequestMethod.GET)
	public String userInfo(Model model, Principal principal) {
		// After user login successfully.
		String username = principal.getName();
		System.out.println("User Name: " + username);

		UserDetails loginedUser = (UserDetails) ((Authentication) principal).getPrincipal();

		String userInfo = WebUtils.toString(loginedUser);
		model.addAttribute("userInfo", userInfo);

		return "userInfo-page";
	}

	@RequestMapping(value = "/403", method = RequestMethod.GET)
	public String accessDenied(Model model, Principal principal) {
		if (principal != null) {
			UserDetails loginedUser = (UserDetails) ((Authentication) principal).getPrincipal();
			String userInfo = WebUtils.toString(loginedUser);

			model.addAttribute("userInfo", userInfo);

			String message = "Hi " + principal.getName() //
					+ "<br> You do not have permission to access this page!";
			model.addAttribute("message", message);
		}

		return "403-page";
	}

	@RequestMapping(value = { "/login" }, method = RequestMethod.GET)
	public String login(Model model) {
		return "login-page";
	}

	// User login with social networking,
	// but does not allow the app to view basic information
	// application will redirect to page / signin.
	@RequestMapping(value = { "/signin" }, method = RequestMethod.GET)
	public String signInPage(Model model) {
		return "redirect:/login";
	}

	@RequestMapping(value = { "/signup" }, method = RequestMethod.GET)
	public String signupPage(WebRequest request, Model model) {
		ProviderSignInUtils providerSignInUtils = new ProviderSignInUtils(connectionFactoryLocator,
				usersConnectionRepository);

		// Retrieve social networking information
		Connection<?> connection = providerSignInUtils.getConnectionFromSession(request);
		//
		AppUserForm myForm = null;
		//
		if (connection != null) {
			myForm = new AppUserForm(connection);
		} else {
			myForm = new AppUserForm();
		}

		model.addAttribute("myForm", myForm);
		return "signup-page";
	}

	@RequestMapping(value = { "/signup" }, method = RequestMethod.POST)
	public String signupSave(WebRequest request, Model model,
			@ModelAttribute("myForm") @Validated AppUserForm appUserForm, BindingResult result,
			final RedirectAttributes redirectAttributes) {

		// validated error
		if (result.hasErrors()) {
			return "signup-page";
		}

		List<String> roleNames = new ArrayList<>();
		roleNames.add(AppRole.ROLE_USER);

		AppUser register = new AppUser();

		try {
			register.setUsername(appUserForm.getUsername());
			register.setEmail(appUserForm.getEmail());
			register.setFirstName(appUserForm.getFirstName());
			register.setLastName(appUserForm.getLastName());
			register.setEncrytedPassword(EncrytedPasswordUtils.encrytePassword(appUserForm.getPassword()));

			register = appUserDao.registerNewUserAccount(register, roleNames);
		} catch (Exception e) {
			model.addAttribute("errorMessage", "Error " + e.getMessage());
			return "signup-page";
		}

		if (appUserForm.getSignInProvider() != null) {
			ProviderSignInUtils providerSignInUtils = new ProviderSignInUtils(connectionFactoryLocator,
					usersConnectionRepository);

			// (Spring Social API):
			// if user login by social networking
			// this method saves social networking information to the UserConnection table
			providerSignInUtils.doPostSignUp(register.getUsername(), request);
		}

		// After registration in complete, automatic login
		SecurityUtil.loginUser(register, roleNames);

		return "redirect:/userInfo";
	}

	@RequestMapping(value = { "/logout-successful-page" }, method = RequestMethod.GET)
	public String logout() {
		return "logout-successful-page";
	}

}
