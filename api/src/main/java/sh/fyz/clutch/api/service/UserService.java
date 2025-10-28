package sh.fyz.clutch.api.service;

import sh.fyz.clutch.api.entity.User;

public class UserService extends AbstractService {

    private static UserService instance;

    public static UserService get() {
        return instance;
    }

    private UserService() {
        instance = this;
    }

    public User getUserFromDiscordID(String discordId) {
        return RepositoryService.get().getUserRepository().where("discordId", discordId);
    }

    @Override
    public void stop() {}
}