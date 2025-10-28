package sh.fyz.clutch.api.config;

import sh.fyz.clutch.api.utils.DockerHelper;
import sh.fyz.architect.persistant.sql.provider.PostgreSQLAuth;
import sh.fyz.yellowconfig.Config;
import sh.fyz.yellowconfig.ConfigField;

public class APIConfig extends Config {

    @ConfigField
    private PostgreSQLAuth postgreSQLCredentials = new PostgreSQLAuth("", 3306, "freshperf-web");

    @ConfigField
    private String postgreSQLUsername = "";

    @ConfigField
    private String postgreSQLPassword = "";

    @ConfigField
    private String frontendURL = "";

    @ConfigField
    private String discordClientID = "";

    @ConfigField
    private String discordClientSecret = "";


    public String getFrontendURL() {
        return frontendURL;
    }

    public String getDiscordClientID() {
        return discordClientID;
    }

    public String getDiscordClientSecret() {
        return discordClientSecret;
    }

    public PostgreSQLAuth getPostgreSQLCredentials() {
        return postgreSQLCredentials;
    }

    public String getPostgreSQLUsername() {
        return postgreSQLUsername;
    }

    public String getPostgreSQLPassword() {
        return postgreSQLPassword;
    }

    public APIConfig() {
        if(!DockerHelper.isDockerEnv()) {
            loadInstance("config", "freshperf", this);
        } else {
            postgreSQLCredentials = new PostgreSQLAuth(
                    System.getenv("DB_HOST"),
                    Integer.parseInt(System.getenv("DB_PORT")),
                    System.getenv("DB_NAME")
            );
            postgreSQLUsername = System.getenv("DB_USER");
            postgreSQLPassword = System.getenv("DB_PASSWORD");
            frontendURL = System.getenv("FRONTEND_URL");
            discordClientID = System.getenv("DISCORD_CLIENT_ID");
            discordClientSecret = System.getenv("DISCORD_CLIENT_SECRET");
        }

    }
}
