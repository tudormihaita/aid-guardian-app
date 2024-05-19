package dot.help.client.controller;

import dot.help.model.CommunityDispatcher;
import dot.help.model.Profile;
import dot.help.services.IServices;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ScoreController {
    private static final Logger logger= LogManager.getLogger(ProfileController.class);
    @FXML
    public Label UsernameLabel;
    @FXML
    public Label TypeOfUserLabel;


    private Profile currentProfile;

    private IServices server;
    private TabPane appTabPane;
    private Tab startTab;
    private Tab profileTab;

    public void setServer(IServices sev) {
        logger.traceEntry("Entering setService");
        this.server = sev;
        logger.traceExit();
    }

    public void setTab(TabPane mainTabPane, Tab startTab, Tab profileTab) {
        logger.traceEntry("Entering setTab");
        this.appTabPane = mainTabPane;
        this.startTab = startTab;
        this.profileTab = profileTab;
        logger.traceExit();
    }

    public void setUser(Profile profile) {
        this.currentProfile = profile;
        UsernameLabel.setText(profile.getUser().getUsername());
        if (profile.getUser() instanceof CommunityDispatcher) {
            TypeOfUserLabel.setText("Community Dispatcher");
        }
        else {
            TypeOfUserLabel.setText("First Responder");
        }
    }
}
