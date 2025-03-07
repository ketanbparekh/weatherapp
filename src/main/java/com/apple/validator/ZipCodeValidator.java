package com.apple.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ZipCodeValidator implements ConstraintValidator<ValidZipCode, String> {

    private static final String ZIP_CODE_REGEX = "^\\d{5}(?:-\\d{4})?$";
    private static final Pattern zipCodePattern = Pattern.compile(ZIP_CODE_REGEX);

    @Override
    public boolean isValid(String zipCode, ConstraintValidatorContext context) {
        return zipCode != null && zipCodePattern.matcher(zipCode).matches();
    }

    @Override
    public void initialize(ValidZipCode validZipCode) {

    }
}
