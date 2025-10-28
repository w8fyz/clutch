package sh.fyz.clutch.api.auth;

import sh.fyz.clutch.api.Main;
import sh.fyz.clutch.api.auth.provider.DiscordProvider;
import sh.fyz.clutch.api.entity.User;
import sh.fyz.clutch.api.service.RepositoryService;
import sh.fyz.clutch.api.service.UserService;
import sh.fyz.fiber.core.authentication.AuthenticationService;
import sh.fyz.fiber.core.authentication.oauth2.OAuth2AuthenticationService;
import sh.fyz.fiber.core.authentication.oauth2.OAuth2Provider;

import java.util.Map;

public class OAuthService extends OAuth2AuthenticationService<User> {

    public OAuthService(String clientId, String clientSecret) {
        super((AuthenticationService<User>) Main.getServer().getAuthService(), RepositoryService.get().getUserRepository());
        registerProvider(new DiscordProvider(clientId, clientSecret));
    }

    @Override
    protected User findOrCreateUser(Map<String, Object> map, OAuth2Provider<User> oAuth2Provider) {
        User user = UserService.get().getUserFromDiscordID(map.get("id").toString());
        if (user == null) {
            user = new User();
            user.setDiscordId(map.get("id").toString());
            user.setCreatedAt(System.currentTimeMillis());
            user.setRole("USER");
        }
        //user.setUsername(map.get("username").toString());
        user.setEmail(map.get("email").toString());
        user.setLastLogin(System.currentTimeMillis());
        user = RepositoryService.get().getUserRepository().save(user);
        return user;
    }
}
