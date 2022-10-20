package com.ead.authuser.specifications;

import com.ead.authuser.models.UserModel;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;

public class SpecificationTemplate {

    /**
     * Utilizing Specification for API advanced filters. In this case, I can use the parameters defined on the @Spec annotation
     * As long as each parameter (I can use one or more) is attended when informed, then return a valid return from database.
     * Note that, because @And is used, for every specified filter they need to be attender, otherwise, return empty.
     *
     * NOTES:
     * 1. If I send any of these parameters as null/empty, it will be ignored during the filtering.
     * 2. If I send a request parameter, which is not set on this method, it will be ignored.
     */
    @And({
        @Spec(path="userType", spec= Equal.class),
        @Spec(path="userStatus", spec= Equal.class),
        @Spec(path="email", spec= Like.class)
    })
    public interface UserSpec extends Specification<UserModel> {}

}
