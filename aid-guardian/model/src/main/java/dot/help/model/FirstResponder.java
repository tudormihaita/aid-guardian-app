package dot.help.model;

public class FirstResponder extends User{
    private boolean onDuty;
    public FirstResponder()
    {

    }
    public FirstResponder(String email, String username, String password) {
        super(email, username, password, UserRole.FIRST_RESPONDER);
    }

    public FirstResponder(String email, String username, String password, boolean onDuty) {
        super(email, username, password);
        this.onDuty = onDuty;
    }

    public boolean isOnDuty() {
        return onDuty;
    }

    public void setOnDuty(boolean onDuty) {
        this.onDuty = onDuty;
    }

    @Override
    public String toString() {
        return "FirstResponder{" +
                "onDuty=" + onDuty +
                '}';
    }
}

