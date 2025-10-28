package sh.fyz.clutch.api;

import sh.fyz.clutch.api.auth.AuthService;
import sh.fyz.clutch.api.auth.OAuthService;
import sh.fyz.clutch.api.auth.roles.Admin;
import sh.fyz.clutch.api.auth.roles.User;
import sh.fyz.clutch.api.config.APIConfig;
import sh.fyz.clutch.api.controller.AuthController;
import sh.fyz.clutch.api.controller.MainController;
import sh.fyz.clutch.api.service.AbstractService;
import sh.fyz.clutch.api.service.RepositoryService;
import sh.fyz.clutch.api.service.UserService;
import sh.fyz.fiber.FiberServer;
import sh.fyz.fiber.core.authentication.impl.CookieAuthenticator;
import sh.fyz.fiber.core.security.cors.CorsService;

import java.util.ArrayList;
import java.util.Arrays;

public class Main {
    private static FiberServer server;
    private static APIConfig config;

    private static final ArrayList<AbstractService> services = new ArrayList<>();

    public static APIConfig getConfig() {
        return config;
    }

    public static FiberServer getServer() {
        return server;
    }


    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(onShutdown);
        config = new APIConfig();
        initializeServices();
        server = new FiberServer(8080, true);
        server.enableDevelopmentMode();
        server.setCorsService(new CorsService()
                .allowNullOrigin()
                .addAllowedOrigin("https://freshperf.lan")
                .addAllowedOrigin("https://api.freshperf.lan")
                .addAllowedOrigin("http://127.0.0.1:8080")
                .addAllowedOrigin("http://localhost:8080")
                .addAllowedOrigin("http://localhost:3000")
                .addAllowedOrigin("https://freshperf.lan")
                .addAllowedOrigin("http://127.0.0.1:3000")
                .setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"))
                .setAllowedHeaders(Arrays.asList("Content-Type", "Authorization", "X-CSRF-Token"))
                .setAllowCredentials(true)
                .setMaxAge(3600L)
        );
        server.setAuthService(new AuthService());
        server.getAuthResolver().registerAuthenticator(new CookieAuthenticator());
        server.setOAuthService(new OAuthService(config.getDiscordClientID(), config.getDiscordClientSecret()));

        server.getRoleRegistry().registerRoleClasses(User.class, Admin.class);

        server.registerController(new AuthController());
        server.registerController(new MainController());

        try {
            System.out.println("Starting FreshPerf v2 API...");
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void initializeServices() {
        services.add(RepositoryService.get());
        services.add(UserService.get());
    }

    private static final Thread onShutdown = new Thread(() -> {
        if (server != null) {
            try {
                server.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
            for(AbstractService service : services) {
                service.stop();
            }
        }
    });

}