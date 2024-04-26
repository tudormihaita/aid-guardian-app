package dot.help.services;

import dot.help.model.Emergency;

public interface IObserver {

    void emergencyReported(Emergency emergency);
}
