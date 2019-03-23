/**
 * 
 */
package vn.tiger.social.validator;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import vn.tiger.social.dao.AppUserDao;
import vn.tiger.social.entity.AppUser;
import vn.tiger.social.form.AppUserForm;

/**
 * @author HoangTD5
 *
 *         Mar 22, 2019
 */
@Component
public class AppUserFormValidator implements Validator {

	// common-validator library.
	private EmailValidator emailValidator = EmailValidator.getInstance();

	@Autowired
	private AppUserDao appUserDAO;

	@Override
	public boolean supports(Class<?> clazz) {
		return clazz == AppUserForm.class;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.validation.Validator#validate(java.lang.Object,
	 * org.springframework.validation.Errors)
	 */
	@Override
	public void validate(Object target, Errors errors) {
		AppUserForm form = (AppUserForm) target;

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "", "Email is required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "", "User name is required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "", "First name is required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "", "Last name is required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "", "Password is required");

		if (errors.hasErrors()) {
			return;
		}

		if (!emailValidator.isValid(form.getEmail())) {

			errors.rejectValue("email", "", "Email is not valid");
			return;
		}

		AppUser userAccount = appUserDAO.findAppUserByUsername(form.getUsername());
		if (userAccount != null) {
			if (form.getUserId() == null) {
				errors.rejectValue("username", "", "User name is not available");
				return;
			} else if (!form.getUserId().equals(userAccount.getUserId())) {
				errors.rejectValue("username", "", "User name is not available");
				return;
			}
		}

		userAccount = appUserDAO.findByEmail(form.getEmail());
		if (userAccount != null) {
			if (form.getUserId() == null) {
				errors.rejectValue("email", "", "Email is not available");
				return;
			} else if (!form.getUserId().equals(userAccount.getUserId())) {
				errors.rejectValue("email", "", "Email is not available");
				return;
			}
		}
	}

}
