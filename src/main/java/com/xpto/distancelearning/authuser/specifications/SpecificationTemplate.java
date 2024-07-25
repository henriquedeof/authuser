package com.xpto.distancelearning.authuser.specifications;

import com.xpto.distancelearning.authuser.models.UserModel;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;

public class SpecificationTemplate {

    /**
     * Specification template for UserModel.
     *
     * Converts the parameters (filters) in the URL to a Specification object.
     * For example, it can convert these parameters to Enums, Dates, BigDecimal, etc.
     *
     * @See ResolverConfig.
     */
    @And({
        @Spec(path = "userType", spec = Equal.class), // Enum type
        @Spec(path = "userStatus", spec = Equal.class), // Enum type
        @Spec(path = "email", spec = Like.class)
    })
    public interface UserSpec extends Specification<UserModel> {}
}
