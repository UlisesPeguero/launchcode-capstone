package com.petboarding.models.app;

//TODO: Turn into an @Entity
public class Module {
    private String path;
    private String name;
    private String icon;
    private String role; // Role role

    public Module() {}

    public Module(String path, String name, String icon, String role) {
        this.path = path;
        this.name = name;
        this.icon = icon;
        this.role = role;
    }

    public static Module dummyFactory() {
        Module dummy = new Module();
        dummy.path = "notFound";
        dummy.name = "Module was not found, assign the attribute 'activeModule'.";
        return dummy;
    }

    public static Module submoduleFactory(String name) {
        Module sub = new Module();
        sub.setName(name);
        return sub;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
