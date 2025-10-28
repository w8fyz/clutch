package sh.fyz.clutch.api.auth.roles;

import sh.fyz.fiber.core.authentication.entities.Role;

public class Admin extends Role {
    protected Admin() {
        super("ADMIN");
    }

    @Override
    protected void initializePermissions() {

    }

    @Override
    public void initializeParentRoles() {
        addParentRole(new User());
    }
}
