package dot.help.networking;

import dot.help.model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ProtoUtils {

    /* REQUESTS */
    public static Protobufs.Request createLoginRequest(User user){
        Protobufs.User userProto = Protobufs.User.newBuilder().setUsername(user.getUsername()).setPassword(user.getPassword()).build();

        return Protobufs.Request.newBuilder().setType(Protobufs.Request.Type.LOGIN)
                .setUser(userProto).build();
    }

    public static Protobufs.Request createLogoutRequest(User user) {
        Protobufs.User userProto = Protobufs.User.newBuilder().setId(user.getId()).setEmail(user.getEmail()).setUsername(user.getUsername()).setPassword(user.getPassword()).build();

        return Protobufs.Request.newBuilder().setType(Protobufs.Request.Type.LOGOUT)
                .setUser(userProto).build();
    }

    public static Protobufs.Request createGetProfileRequest(User user) {
        Protobufs.User userProto = Protobufs.User.newBuilder().setId(user.getId()).setEmail(user.getEmail()).setUsername(user.getUsername()).setPassword(user.getPassword()).build();

        return Protobufs.Request.newBuilder().setType(Protobufs.Request.Type.GET_PROFILE)
                .setUser(userProto).build();
    }


    public static Protobufs.Request createReportEmergencyRequest(Emergency emergency) {
        Protobufs.User reporterProto = getUserProto(emergency.getReporter());
        Protobufs.Emergency emergencyProto = Protobufs.Emergency.newBuilder().
                setReporter(reporterProto).
                setDate(emergency.getReportedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).
                setDescription(emergency.getDescription()).
                setStatus(Protobufs.Emergency.Status.valueOf(emergency.getStatus().toString())).
                setLocation(emergency.getLocation()).build();

        return Protobufs.Request.newBuilder().setType(Protobufs.Request.Type.REPORT_EMERGENCY).setEmergency(emergencyProto).build();
    }

    public static Protobufs.Request createRespondToEmergencyRequest(FirstResponder responder, Emergency emergency) {
        emergency.setResponder(responder);

        Protobufs.Emergency emergencyProto = getEmergencyProto(emergency);

        return Protobufs.Request.newBuilder().setType(Protobufs.Request.Type.RESPOND_EMERGENCY).setEmergency(emergencyProto).
                setUser(getUserProto(responder)).build();
    }

    /* RESPONSES */
    public static Protobufs.Response createOkResponse() {
        return Protobufs.Response.newBuilder().setType(Protobufs.Response.Type.OK).build();
    }

    public static Protobufs.Response createErrorResponse(String message) {
        return Protobufs.Response.newBuilder().setType(Protobufs.Response.Type.ERROR).setError(message).build();
    }

    public static Protobufs.Response createLoginResponse(User user) {
        Protobufs.Response.Builder response = Protobufs.Response.newBuilder().setType(Protobufs.Response.Type.OK);
        if (user.getRole() == UserRole.FIRST_RESPONDER) {
            FirstResponder responder = (FirstResponder) user;
            response.setUser(Protobufs.User.newBuilder().setId(responder.getId()).setUsername(responder.getUsername()).setEmail(responder.getEmail()).setType(Protobufs.User.Type.FIRST_RESPONDER).setOnDuty(responder.isOnDuty())).build();
        }
        else {
            response.setUser(Protobufs.User.newBuilder().setId(user.getId()).setUsername(user.getUsername()).setEmail(user.getEmail()).setType(Protobufs.User.Type.COMMUNITY_DISPATCHER)).build();

        }

        return response.build();
    }

    public static Protobufs.Response createLogoutResponse() {
        return Protobufs.Response.newBuilder().setType(Protobufs.Response.Type.OK).build();
    }

    public static Protobufs.Response createProfileResponse(Profile profile) {
        Protobufs.Profile profileProto = Protobufs.Profile.newBuilder().setId(profile.getId()).
                setFirstName(profile.getFirstName()).
                setLastName(profile.getLastName()).
                setGender(Protobufs.Profile.Gender.valueOf(profile.getGender().toString())).
                setBirthDate(profile.getBirthDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).
                setBloodGroup(Protobufs.Profile.BloodGroup.valueOf(profile.getBloodGroup().toString())).
                setHeight(profile.getHeight()).
                setWeight(profile.getWeight()).
                setMedicalConditions(profile.getMedicalHistory()).
                setScore(profile.getScore()).build();

        return Protobufs.Response.newBuilder().setType(Protobufs.Response.Type.OK).setProfile(profileProto).build();
    }

    public static Protobufs.Response createReportEmergencyResponse(Emergency emergency) {
        Protobufs.Emergency emergencyProto = getEmergencyProto(emergency);

        return Protobufs.Response.newBuilder().setType(Protobufs.Response.Type.OK).setEmergency(emergencyProto).build();
    }

    public static Protobufs.Response createRespondToEmergencyResponse(Emergency emergency) {
        Protobufs.Emergency emergencyProto = getEmergencyProto(emergency);

        return Protobufs.Response.newBuilder().setType(Protobufs.Response.Type.OK).setEmergency(emergencyProto).build();
    }

    public static Protobufs.Response createEmergencyReportedResponse(Emergency emergency) {
        Protobufs.Emergency emergencyProto = getEmergencyProto(emergency);

        return Protobufs.Response.newBuilder().setType(Protobufs.Response.Type.EMERGENCY_REPORTED).setEmergency(emergencyProto).build();
    }

    public static Protobufs.Response createEmergencyRespondedResponse(Emergency emergency) {
        Protobufs.Emergency emergencyProto = getEmergencyProto(emergency);

        return Protobufs.Response.newBuilder().setType(Protobufs.Response.Type.EMERGENCY_RESPONDED).setEmergency(emergencyProto).build();
    }

    /* RESPONSE GETTERS */
    public static String getError(Protobufs.Response response) {
        return response.getError();
    }

    public static FirstResponder getResponder(Protobufs.User userProto) {
        FirstResponder responder = new FirstResponder(userProto.getEmail(), userProto.getUsername(), userProto.getPassword(), userProto.getOnDuty());
        responder.setId(userProto.getId());
        return responder;
    }

    public static User getUser(Protobufs.Response response) {
        Protobufs.User userProto = response.getUser();

        return getUser(userProto);
    }

    public static Profile getProfile(Protobufs.Response response) {
        Protobufs.Profile profileProto = response.getProfile();

        return getProfile(profileProto);
    }

    public static Emergency getEmergency(Protobufs.Response response) {
        Protobufs.Emergency emergencyProto = response.getEmergency();

       return getEmergency(emergencyProto);
    }

    /* REQUEST GETTERS */
    public static User getUser(Protobufs.Request request) {
        Protobufs.User userProto = request.getUser();

       return getUser(userProto);
    }

    public static Emergency getEmergency(Protobufs.Request request) {
        Protobufs.Emergency emergencyProto = request.getEmergency();

       return getEmergency(emergencyProto);
    }


    /* PRIVATE METHODS */
    private static User getUser(Protobufs.User userProto) {
        if (userProto.getType() == Protobufs.User.Type.FIRST_RESPONDER) {
            return getResponder(userProto);
        }
        else {
            User user = new User(userProto.getEmail(), userProto.getUsername(), userProto.getPassword());
            user.setId(userProto.getId());
            return user;
        }
    }

    private static Protobufs.User getUserProto(User user) {
        if (user.getRole() == UserRole.FIRST_RESPONDER) {
            FirstResponder responder = (FirstResponder) user;
            Protobufs.User userProto = Protobufs.User.newBuilder().setId(responder.getId()).setEmail(responder.getEmail()).setUsername(responder.getUsername()).setPassword(responder.getPassword()).setOnDuty(responder.isOnDuty()).build();
            return userProto;
        }
        else {
            Protobufs.User userProto = Protobufs.User.newBuilder().setId(user.getId()).setEmail(user.getEmail()).setUsername(user.getUsername()).setPassword(user.getPassword()).build();
            return userProto;
        }
    }

    private static Emergency getEmergency(Protobufs.Emergency emergencyProto) {
        FirstResponder responder = getResponder(emergencyProto.getResponder());
        Emergency emergency = new Emergency(getUser(emergencyProto.getReporter()),
                LocalDateTime.parse(emergencyProto.getDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                emergencyProto.getDescription(),
                Status.valueOf(emergencyProto.getStatus().toString()),
                responder,
                emergencyProto.getLocation());

        emergency.setId(emergencyProto.getId());
        return emergency;
    }

    private static Protobufs.Emergency getEmergencyProto(Emergency emergency) {
        Protobufs.User reporterProto = getUserProto(emergency.getReporter());
        if (emergency.getResponder() == null) {
            return Protobufs.Emergency.newBuilder().setId(emergency.getId()).
                    setReporter(reporterProto).
                    setDate(emergency.getReportedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).
                    setDescription(emergency.getDescription()).
                    setStatus(Protobufs.Emergency.Status.valueOf(emergency.getStatus().toString())).
                    setLocation(emergency.getLocation()).build();
        }
        else {
            Protobufs.User responderProto = getUserProto(emergency.getResponder());

            return Protobufs.Emergency.newBuilder().setId(emergency.getId()).
                    setReporter(reporterProto).
                    setDate(emergency.getReportedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).
                    setDescription(emergency.getDescription()).
                    setStatus(Protobufs.Emergency.Status.valueOf(emergency.getStatus().toString())).
                    setLocation(emergency.getLocation()).setResponder(responderProto).build();
        }
    }

    private static Profile getProfile(Protobufs.Profile profileProto)  {
        Profile profile = new Profile(null,
                profileProto.getFirstName(),
                profileProto.getLastName(),
                GenderType.valueOf(profileProto.getGender().toString()),
                LocalDate.parse(profileProto.getBirthDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                BloodGroupType.valueOf(profileProto.getBloodGroup().toString()),
                profileProto.getHeight(),
                profileProto.getWeight(),
                profileProto.getMedicalConditions(),
                profileProto.getScore());

        profile.setId(profileProto.getId());
        return profile;
    }
}
