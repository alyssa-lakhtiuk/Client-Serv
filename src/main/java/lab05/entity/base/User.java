package lab05.entity.base;

public class User {
    private int id;
    private String login;
    private String password;
    private String Role;

    public User(int id, String login, String password, String role) {
        this.id = id;
        this.login = login;
        this.password = password;
        Role = role;
    }

    public User(String login, String password, String role) {
        this.login = login;
        this.password = password;
        Role = role;
    }

    public int getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return Role;
    }
}
