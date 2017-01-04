package com.sectong.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * 验证字段实现
 * 
 * @author jiekechoo
 *
 */
@Component
public class UserCreateFormValidator implements Validator {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserCreateFormValidator.class);


	@Override
	public boolean supports(Class<?> aClass) {
		return false;
	}

	@Override
	public void validate(Object target, Errors errors) {
		LOGGER.debug("Validating {}", target);
	}

}
