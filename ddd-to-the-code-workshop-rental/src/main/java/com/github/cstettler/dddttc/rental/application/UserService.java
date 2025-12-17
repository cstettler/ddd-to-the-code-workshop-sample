package com.github.cstettler.dddttc.rental.application;

import com.github.cstettler.dddttc.rental.domain.user.FirstName;
import com.github.cstettler.dddttc.rental.domain.user.LastName;
import com.github.cstettler.dddttc.rental.domain.user.User;
import com.github.cstettler.dddttc.rental.domain.user.UserAlreadyExistsException;
import com.github.cstettler.dddttc.rental.domain.user.UserId;
import com.github.cstettler.dddttc.rental.domain.user.UserRepository;
import com.github.cstettler.dddttc.stereotype.ApplicationService;

import static com.github.cstettler.dddttc.rental.domain.user.User.newUser;

@ApplicationService
public class UserService {

    private final UserRepository userRepository;

    UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void addUser(UserId userId, FirstName firstName, LastName lastName) throws UserAlreadyExistsException {
        User user = newUser(userId, firstName, lastName);

        this.userRepository.add(user);
    }

}
