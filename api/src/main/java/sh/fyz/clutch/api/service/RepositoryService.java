package sh.fyz.clutch.api.service;

import sh.fyz.clutch.api.Main;
import sh.fyz.clutch.api.repository.UserRepository;
import sh.fyz.architect.Architect;
import sh.fyz.architect.persistant.DatabaseCredentials;

public class RepositoryService extends AbstractService{

    private static RepositoryService instance;
    private Architect architect;

    public static RepositoryService get() {
        if (instance == null) {
            instance = new RepositoryService();
        }
        return instance;
    }

    public Architect getArchitect() {
        return architect;
    }

    private final UserRepository userRepository;

    public UserRepository getUserRepository() {
        return userRepository;
    }

    private RepositoryService() {
        architect = new Architect()
                .setDatabaseCredentials(new DatabaseCredentials(Main.getConfig().getPostgreSQLCredentials(),
                        Main.getConfig().getPostgreSQLUsername(), Main.getConfig().getPostgreSQLPassword(), 10
                ))
                .setReceiver(true);
        architect.start();
        this.userRepository = new UserRepository();
        architect.addRepositories(userRepository);

    }

    @Override
    public void stop() {
        architect.stop();
    }
}
