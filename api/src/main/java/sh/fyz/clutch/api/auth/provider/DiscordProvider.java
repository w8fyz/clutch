package sh.fyz.clutch.api.auth.provider;


import sh.fyz.clutch.api.entity.User;
import sh.fyz.fiber.core.authentication.oauth2.impl.DiscordOAuth2Provider;

import java.util.Map;

public class DiscordProvider extends DiscordOAuth2Provider<User> {
    public DiscordProvider(String clientId, String clientSecret) {
        super(clientId, clientSecret, "identify email guilds");
    }

    @Override
    public void mapUserData(Map<String, Object> userInfo, User user) {
        user.setDiscordId(userInfo.get("id").toString());
        user.setEmail(userInfo.get("email").toString());
    }
}
