package org.jtalks.jcommune.model.validation.validators;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.jtalks.jcommune.model.validation.annotations.SizeOrNull;

/**
 * Check that a string's trimmed length is not empty
 * and row size is between the max and min
 *
 * @author Alexandra Khekhneva
 */
public class SizeOrNullValidator implements ConstraintValidator<SizeOrNull, String> {

     private int min;
     private int max;


     /**
      * {@inheritDoc}
      */
     @Override
     public void initialize(SizeOrNull annotation) {
         this.min = annotation.min();
         this.max = annotation.max();
    }

     /**
      * {@inheritDoc}
      */
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if ( s == null ) {
            return true;
        }
        if  ((s.trim().length() >= min) && (s.trim().length() <= max)) {
            return true;
        }
        return false;
    }
}
