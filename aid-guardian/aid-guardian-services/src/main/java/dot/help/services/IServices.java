package dot.help.services;

import dot.help.model.Emergency;
import dot.help.model.FirstResponder;
import dot.help.model.User;

public interface IServices {
    User logIn(String credential, String password, IObserver client);

    void logOut(User user, IObserver client);

    void reportEmergency(Emergency emergency, IObserver client);

    void respondToEmergency(FirstResponder responder, Emergency emergency, IObserver client);
}
