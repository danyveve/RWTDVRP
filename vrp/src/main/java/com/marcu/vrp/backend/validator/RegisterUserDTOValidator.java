package com.marcu.vrp.backend.validator;
import com.marcu.vrp.backend.dto.RegisterUserDTO;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class RegisterUserDTOValidator implements Validator<RegisterUserDTO>{

    @Override
    public void validate(RegisterUserDTO registerUserDTO) throws ValidatorException {
        if(!Objects.equals(registerUserDTO.getPassword(), registerUserDTO.getPasswordConfirmation()))
            throw new ValidatorException("Password and password confirmation do not match!");
    }
}
