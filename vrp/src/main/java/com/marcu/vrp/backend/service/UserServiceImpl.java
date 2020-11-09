package com.marcu.vrp.backend.service;

import com.marcu.vrp.backend.dto.RegisterUserDTO;
import com.marcu.vrp.backend.dto.UserDTO;
import com.marcu.vrp.backend.dto.UserRoleDTO;
import com.marcu.vrp.backend.mapper.UserDTOMapper;
import com.marcu.vrp.backend.model.User;
import com.marcu.vrp.backend.model.UserRole;
import com.marcu.vrp.backend.model.UserRoleName;
import com.marcu.vrp.backend.repository.UserRepository;
import com.marcu.vrp.backend.repository.UserRoleRepository;
import com.marcu.vrp.backend.validator.RegisterUserDTOValidator;
import com.marcu.vrp.backend.validator.ValidatorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Objects;


@Service
public class UserServiceImpl implements UserService {
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final UserDTOMapper userDTOMapper;
    private final RegisterUserDTOValidator registerUserDTOValidator;
    private final UserRoleRepository userRoleRepository;

    public UserServiceImpl(final UserRepository userRepository,
                           final UserDTOMapper userDTOMapper,
                           final RegisterUserDTOValidator registerUserDTOValidator,
                           final UserRoleRepository userRoleRepository) {
        this.userRepository = userRepository;
        this.userDTOMapper = userDTOMapper;
        this.registerUserDTOValidator = registerUserDTOValidator;
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    public UserDTO registerUser(RegisterUserDTO registerUserDTO) {
        log.debug("Trying to register user {}.", registerUserDTO.toString());
        try {
            registerUserDTOValidator.validate(registerUserDTO);
        } catch (ValidatorException e) {
            log.error(e.getMessage());
            throw e;
        }

        User possibleExistingUser = userRepository.findFirstByUsernameOrEmailOrPhone(
            registerUserDTO.getUsername(), registerUserDTO.getEmail(), registerUserDTO.getPhone());
        if (possibleExistingUser != null) {
            if (Objects.equals(possibleExistingUser.getEmail(), registerUserDTO.getEmail()))
                throw new ValidatorException("There already exists an account with this email.");
            if (Objects.equals(possibleExistingUser.getUsername(), registerUserDTO.getUsername()))
                throw new ValidatorException("There already exists an account with this username.");
            if (Objects.equals(possibleExistingUser.getPhone(), registerUserDTO.getPhone()))
                throw new ValidatorException("There already exists an account with this Phone no.");
        }

        User newUser = userDTOMapper.toUserFromUserRegisterDTO(registerUserDTO);
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        newUser.setPassword(bCryptPasswordEncoder.encode(newUser.getPassword()));
        Long userRoleId = userRoleRepository.findByName(UserRoleName.CLIENT).getId();
        newUser.setRoleId(userRoleId);

        return userDTOMapper.toDto(userRepository.save(newUser));
    }

    @Override
    public UserDTO loadUserByUsername(String username) {
        log.debug("Loading user with username {}.", username);
        return userDTOMapper.toDto(userRepository.findByUsername(username));
    }

    @Override
    public UserDTO editProfile(Principal currentUserOld, RegisterUserDTO currentUserNew) {
        log.debug("Updating user with username {} to following details: {}", currentUserOld.getName(), currentUserNew.toString());
        User currentUserEntityOld = userRepository.findByUsername(currentUserOld.getName());

        try {
            registerUserDTOValidator.validate(currentUserNew);
        } catch (ValidatorException e) {
            log.error(e.getMessage());
            throw e;
        }

        List<User> possibleExistingUsers = userRepository.findByEmailOrPhone(currentUserNew.getEmail(), currentUserNew.getPhone());
        if (possibleExistingUsers != null && possibleExistingUsers.size() > 0) {
            possibleExistingUsers.forEach(possibleExistingUser -> {
                if (Objects.equals(possibleExistingUser.getEmail(), currentUserNew.getEmail()) && !possibleExistingUser.getId().equals(currentUserEntityOld.getId()))
                    throw new ValidatorException("There already exists an account with this email.");
                if (Objects.equals(possibleExistingUser.getPhone(), currentUserNew.getPhone()) && !possibleExistingUser.getId().equals(currentUserEntityOld.getId()))
                    throw new ValidatorException("There already exists an account with this Phone no.");
            });
        }

        if(currentUserNew.getPassword() != null && !currentUserNew.getPassword().isEmpty()) {
            currentUserEntityOld.setPassword(currentUserNew.getPassword());
        }
        currentUserEntityOld.setEmail(currentUserNew.getEmail());
        currentUserEntityOld.setFirstName(currentUserNew.getFirstName());
        currentUserEntityOld.setLastName(currentUserNew.getLastName());
        currentUserEntityOld.setPhone(currentUserNew.getPhone());

        return userDTOMapper.toDto(userRepository.save(currentUserEntityOld));
    }
}
