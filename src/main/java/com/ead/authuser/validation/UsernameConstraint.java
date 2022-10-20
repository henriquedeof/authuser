package com.ead.authuser.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UsernameConstraintImpl.class) // Class that implements the validation
@Target({ElementType.METHOD, ElementType.FIELD}) // Define where this annotation can be used. In this example, I can use it on methods and fields.
@Retention(RetentionPolicy.RUNTIME) // It happens at execution time (runtime)
public @interface UsernameConstraint {

    String message() default "invalid username";
    Class<?>[] groups() default {}; // I can define validation groups
    Class<? extends Payload>[] payload() default {}; // Level where the error occurs.

}
