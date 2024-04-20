package dot.help.model;

public class CommunityHelper extends User{

    private Profile profile;
    public CommunityHelper(String username, String password, Profile profile) {
        super(username, password);
        this.profile = profile;
    }
}
