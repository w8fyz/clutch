package sh.fyz.clutch.api.repository;

import sh.fyz.clutch.api.entity.User;
import sh.fyz.architect.repositories.GenericRepository;

public class UserRepository extends GenericRepository<User> {
    public UserRepository() {
        super(User.class);
    }
}
