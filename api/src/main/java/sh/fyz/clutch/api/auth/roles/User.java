package sh.fyz.clutch.api.auth.roles;

import sh.fyz.fiber.core.authentication.entities.Role;
import sh.fyz.fiber.core.authentication.entities.UserAuth;

public class User extends Role {
    protected User() {
        super("USER");
    }

    @Override
    protected void initializePermissions() {

    }

    @Override
    public void initializeParentRoles() {

    }
}
