package com.ead.authuser.dtos;

import com.ead.authuser.validation.UsernameConstraint;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.UUID;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL) // Do not display attributes that are null.
public class UserDto {

    // Creating multiple visions (JsonViews) for this DTO
    public interface UserView {
        public static interface RegistrationPost{}
        public static interface UserPut{} // Vision to update a user.
        public static interface PasswordPut{}
        public static interface ImagePut{}
    }

    private UUID userId;

    //Creating validation using Spring Validation. Verifies null and blank.
    @NotBlank(groups = UserView.RegistrationPost.class) // 'groups' means that this validation will ONLY be applied to the RegistrationPost view (not all other calls/views).
    @Size(min = 4, max = 50, groups = UserView.RegistrationPost.class)
    @UsernameConstraint(groups = UserView.RegistrationPost.class) // Creating a customised Validation.
    @JsonView(UserView.RegistrationPost.class)
    private String username;

    @NotBlank(groups = UserView.RegistrationPost.class)
    @Email(groups = UserView.RegistrationPost.class)
    @JsonView(UserView.RegistrationPost.class) // For this example, email will just be created, not updated.
    private String email;

    @NotBlank(groups = {UserView.RegistrationPost.class, UserView.PasswordPut.class})
    @Size(min = 6, max = 20, groups = {UserView.PasswordPut.class, UserView.PasswordPut.class})
    @JsonView({UserView.RegistrationPost.class, UserView.PasswordPut.class})
    private String password;

    @NotBlank@NotBlank(groups = {UserView.PasswordPut.class})
    @Size(min = 6, max = 20, groups = {UserView.PasswordPut.class, UserView.PasswordPut.class})
    @JsonView(UserView.PasswordPut.class)
    private String oldPassword;

    @JsonView({UserView.RegistrationPost.class, UserView.UserPut.class})
    private String fullName;

    @JsonView({UserView.RegistrationPost.class, UserView.UserPut.class})
    private String phoneNumber;

    @JsonView({UserView.RegistrationPost.class, UserView.UserPut.class})
    private String cpf;

    @NotBlank@NotBlank(groups = UserView.ImagePut.class)
    @JsonView(UserView.ImagePut.class)
    private String imageUrl;

}
