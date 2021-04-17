package service;

import org.apache.commons.validator.GenericValidator;
import org.apache.commons.validator.routines.DateValidator;

public class DateValidationClass extends DateValidator {

    @Override
    public boolean isValid(String dateStr) {
        return GenericValidator.isDate(dateStr, "mm/dd/yyyy", true);
    }
}
