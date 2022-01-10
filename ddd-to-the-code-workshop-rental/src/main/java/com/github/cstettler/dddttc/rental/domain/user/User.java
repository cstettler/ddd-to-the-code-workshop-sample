package com.github.cstettler.dddttc.rental.domain.user;

import com.github.cstettler.dddttc.stereotype.Aggregate;
import com.github.cstettler.dddttc.stereotype.AggregateFactory;

@Aggregate
public class User {

    private final UserId id;
    private final FirstName firstName;
    private final LastName lastName;

    private User(UserId id, FirstName firstName, LastName lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public UserId id() {
        return this.id;
    }

    @AggregateFactory(User.class)
    public static User newUser(UserId userId, FirstName firstName, LastName lastName) {
        return new User(userId, firstName, lastName);
    }

}
