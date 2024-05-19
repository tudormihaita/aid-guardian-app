package dot.help.model;

public class CommunityDispatcher extends User {

    public CommunityDispatcher()
    {

    }
    public CommunityDispatcher(String username, String password) {
        super(username, password);
    }

    public CommunityDispatcher(String email, String username, String password) {
        super(email, username, password);
    }

}
