package sh.fyz.clutch.api.controller;

import sh.fyz.clutch.api.Main;
import sh.fyz.clutch.api.entity.User;
import sh.fyz.clutch.api.service.RepositoryService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sh.fyz.fiber.FiberServer;
import sh.fyz.fiber.annotations.params.AuthenticatedUser;
import sh.fyz.fiber.annotations.params.Param;
import sh.fyz.fiber.annotations.params.PathVariable;
import sh.fyz.fiber.annotations.params.RequestBody;
import sh.fyz.fiber.annotations.request.Controller;
import sh.fyz.fiber.annotations.request.RequestMapping;
import sh.fyz.fiber.annotations.security.AuthType;
import sh.fyz.fiber.core.JwtUtil;
import sh.fyz.fiber.core.ResponseEntity;
import sh.fyz.fiber.core.authentication.AuthScheme;
import sh.fyz.fiber.core.authentication.AuthenticationService;
import sh.fyz.fiber.core.authentication.entities.UserAuth;
import sh.fyz.fiber.core.authentication.entities.UserFieldUtil;
import sh.fyz.fiber.core.authentication.oauth2.OAuth2Provider;
import sh.fyz.fiber.core.security.annotations.AuditLog;
import sh.fyz.fiber.core.security.annotations.RateLimit;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller("/auth")
public class AuthController {

    @AuthType(AuthScheme.COOKIE)
    @RequestMapping(value = "/me", method = RequestMapping.Method.GET)
    public ResponseEntity<Map<String, Object>> getSelf(@AuthenticatedUser User user) {
        return ResponseEntity.ok(user.asDTO());
    }

    @RequestMapping(value = "/login", method = RequestMapping.Method.POST)
    @RateLimit(attempts = 5, timeout = 15, unit = TimeUnit.MINUTES)
    @AuditLog(action = "LOGIN_ATTEMPT", logParameters = true, maskSensitiveData = true)
    public ResponseEntity<String> login(@RequestBody Map<String, String> body, HttpServletRequest request, HttpServletResponse response) {
        String identifier = body.get("identifier") != null ? body.get("identifier") : "";
        String password = body.get("password") != null ? body.get("password") : "";

        AuthenticationService<?> authService = FiberServer.get().getAuthService();
        User user = (User) authService.findUserByIdentifer(identifier);
        if(user != null && authService.validateCredentials(user, password)) {
            user.setLastLogin(System.currentTimeMillis());
            user = RepositoryService.get().getUserRepository().save(user);
            authService.setAuthCookies(user, request, response);
            return ResponseEntity.ok("Login successful");
        } else {
            return ResponseEntity.unauthorized("Invalid credentials");
        }
    }

    @RequestMapping(value = "/register", method = RequestMapping.Method.POST)
    @AuditLog(action = "USER_REGISTRATION", logParameters = true, maskSensitiveData = true)
    public ResponseEntity<String> register(@RequestBody Map<String, Object> user) {
        User creating = new User();
        creating.setEmail((String)user.get("email"));

        creating.setFirstName((String) user.get("firstName"));
        creating.setLastName((String) user.get("lastName"));
        creating.setCompany((Boolean) user.get("isCompany"));
        creating.setCompanyName(user.get("companyName") != null ? (String) user.get("companyName") : "");
        creating.setVatNumber((String) user.get("vatNumber"));

        creating.setPhone((String) user.get("phone"));
        creating.setStreetAddress(user.get("streetAddress") != null ? (String) user.get("streetAddress") : "");
        creating.setCity(user.get("city") != null ? (String) user.get("city") : "");
        creating.setPostalCode(user.get("postalCode") != null ? (String) user.get("postalCode") : "");
        creating.setCountry(user.get("country") != null ? (String) user.get("country") : "");

        UserFieldUtil.setPassword(creating, (String) user.get("password"));

        creating.setRole("USER");
        creating.setCreatedAt(System.currentTimeMillis());

        UserAuth exist = FiberServer.get().getAuthService().findUserByIdentifer(creating.getEmail());
        if(exist != null) {
            return ResponseEntity.badRequest("User with this identifier already exists");
        }
        RepositoryService.get().getUserRepository().save(creating);
        return ResponseEntity.ok("User registered successfully");
    }

    @RequestMapping(value = "/refresh", method = RequestMapping.Method.POST)
    @AuditLog(action = "TOKEN_REFRESH", logParameters = true, maskSensitiveData = true)
    public ResponseEntity<Map<String, String>> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response) {

        AuthenticationService<?> authService = FiberServer.get().getAuthService();
        String ipAddress = authService.getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");

        // Get refresh token from cookie
        String refreshToken = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refresh_token".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (refreshToken == null) {
            return ResponseEntity.unauthorized(Map.of("error", "No refresh token provided"));
        }

        if (JwtUtil.validateRefreshToken(refreshToken, ipAddress, userAgent)) {
            Object userId = JwtUtil.extractId(refreshToken);
            UserAuth user = authService.getUserById(userId);

            if (user != null) {
                authService.setAuthCookies(user, request, response);

                Map<String, String> tokens = new HashMap<>();
                tokens.put("token_type", "Cookie");
                tokens.put("expires_in", "3600");

                return ResponseEntity.ok(tokens);
            }
        }

        return ResponseEntity.unauthorized(Map.of("error", "Invalid refresh token"));
    }

    @RequestMapping(value = "/oauth/{provider}/login", method = RequestMapping.Method.GET)
    @AuditLog(action = "OAUTH_LOGIN_START", logParameters = true)
    public void oauthLogin(@PathVariable("provider") String provider, HttpServletRequest request, HttpServletResponse response) throws IOException {
        OAuth2Provider<User> oauthProvider = (OAuth2Provider<User>) FiberServer.get().getOAuthService().getProvider(provider);
        if (oauthProvider != null) {
            String requestUrl = request.getRequestURL().toString();
            String callbackUrl = "http://localhost:5050/auth/oauth/"+provider+"/callback";
            String redirectUrl = FiberServer.get().getOAuthService().getAuthorizationUrl(provider, callbackUrl);
            response.setHeader("Location", redirectUrl);
            response.setStatus(302);
        } else {
            response.sendError(400, "Provider not found: " + provider);
        }

    }

    @RequestMapping(value = "/oauth/{provider}/callback", method = RequestMapping.Method.GET)
    @AuditLog(action = "OAUTH_CALLBACK", logParameters = true)
    public void oauthCallback(@Param("code") String code, @Param("state") String state, HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            System.out.println("Request URL : "+request.getRequestURL().toString());
            User exampleUser = (User) FiberServer.get().getOAuthService().handleCallback(code, state, request.getRequestURL().toString(), request, response);
            if (exampleUser != null) {
                response.setHeader("Location", Main.getConfig().getFrontendURL()+"/dashboard");
                response.setStatus(302);
                return;
            }
            response.setHeader("Location", Main.getConfig().getFrontendURL()+"/login?error=OAuth authentication failed");
            response.setStatus(302);
        } catch (Exception e) {
            response.setHeader("Location", Main.getConfig().getFrontendURL()+"/login?error="+e.getMessage());
            response.setStatus(302);
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/logout", method = RequestMapping.Method.POST)
    @AuditLog(action = "LOGOUT", logParameters = false)
    public ResponseEntity<String> logout(HttpServletResponse response) {
        AuthenticationService<?> authService = FiberServer.get().getAuthService();
        authService.clearAuthCookies(response);
        return ResponseEntity.ok("Logged out successfully");
    }

}
