package co.flock.app.poker;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.Map;

/**
 * Created by hemanshu.v on 8/23/16.
 */
@Singleton
public class UserStore {

    private final Map<String, User> store = Maps.newHashMap();

    @Inject
    public UserStore() {
        registerUser("u:rl9bixbr6fbiqiqf", "31e85346-9209-4620-bcb1-8b52689f4d69");
    }

    public void registerUser(String userId, String userToken) {
        store.put(userId, new User(userId, userToken));
    }

    public String getToken(String userId) {
        return store.get(userId).userToken;
    }

    private class User {
        private final String userId;
        private final String userToken;

        public User(String userId, String userToken) {

            this.userId = userId;
            this.userToken = userToken;
        }
    }
}
