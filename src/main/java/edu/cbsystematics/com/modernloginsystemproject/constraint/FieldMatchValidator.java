package edu.cbsystematics.com.modernloginsystemproject.constraint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {

    private static final Logger logger = LoggerFactory.getLogger(FieldMatchValidator.class);
    private String firstFieldName;
    private String secondFieldName;

    @Override
    public void initialize(final FieldMatch constraintAnnotation) {
        firstFieldName = constraintAnnotation.first();
        secondFieldName = constraintAnnotation.second();
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        try {
            BeanWrapper beanWrapper = new BeanWrapperImpl(value);
            final Object firstObj = beanWrapper.getPropertyValue(firstFieldName);
            final Object secondObj = beanWrapper.getPropertyValue(secondFieldName);
            return firstObj == null && secondObj == null || firstObj != null && firstObj.equals(secondObj);
        } catch (final Exception e) {
            logger.error("Exception during validation", e);
        }
        return true;
    }
}