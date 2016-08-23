package co.flock.app.poker;

import com.google.common.collect.Maps;
import com.google.inject.Singleton;

import java.util.Map;

/**
 * Created by hemanshu.v on 8/23/16.
 */
@Singleton
public class UserStore {

    private final Map<String, String> store = Maps.newHashMap();

    public void registerUser(String userId, String userToken) {
        store.put(userId, userToken);
    }

    public String getToken(String userId) {
        return store.get(userId);
    }
}
