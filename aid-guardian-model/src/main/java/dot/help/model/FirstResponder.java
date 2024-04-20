package dot.help.model;

public class FirstResponder extends User{


    private boolean onDuty;
    private Profile profile;

    public FirstResponder(String username, String password) {
        super(username, password);
    }
    public FirstResponder(String username, String password, Profile profile) {
        super(username, password);
        this.profile = profile;
    }

    public boolean isOnDuty() {
        return onDuty;
    }

    public void setOnDuty(boolean onDuty) {
        this.onDuty = onDuty;
    }
}
