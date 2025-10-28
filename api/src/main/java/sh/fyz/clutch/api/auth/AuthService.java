package sh.fyz.clutch.api.auth;

import sh.fyz.clutch.api.entity.User;
import sh.fyz.clutch.api.service.RepositoryService;
import sh.fyz.fiber.core.authentication.AuthCookieConfig;
import sh.fyz.fiber.core.authentication.AuthenticationService;
import sh.fyz.fiber.core.authentication.SameSitePolicy;

public class AuthService extends AuthenticationService<User> {
    public AuthService() {

        super(RepositoryService.get().getUserRepository(), "/auth",new AuthCookieConfig()
                .setDomain(".freshperf.lan")
                .setPath("/")
                .setSameSite(SameSitePolicy.NONE)
                .setSecure(true)
                .setHttpOnly(true)
                .setAccessTokenMaxAge(3600)
                .setRefreshTokenMaxAge(604800));
    }
}
