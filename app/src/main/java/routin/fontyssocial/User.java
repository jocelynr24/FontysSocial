package routin.fontyssocial;

/**
 * Created by conte on 04/12/2017.
 */

public class User {
    private static User INSTANCE = null;
    String name;

    public User() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                '}';
    }

    public static User getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new User();
        }
        return(INSTANCE);
    }
}


