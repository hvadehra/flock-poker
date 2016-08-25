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
        registerUser("u:wigyh6rwfchfghhw", "a8012a8d-b1b0-4717-98e4-f7f6e2a7803c");
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
