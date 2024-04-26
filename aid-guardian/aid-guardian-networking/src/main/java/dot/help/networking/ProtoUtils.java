package dot.help.networking;

import dot.help.model.User;

public class ProtoUtils {
    public static Protobufs.Request createLoginRequest(User user){
        Protobufs.User userProto=Protobufs.User.newBuilder().setUsername(user.getUsername()).setPassword(user.getPassword()).build();
        Protobufs.Request request= Protobufs.Request.newBuilder().setType(Protobufs.Request.Type.Login)
                .setUser(userProto).build();
        return request;
    }

    public static Protobufs.Request createLogoutRequest(User user) {
        Protobufs.User userProto=Protobufs.User.newBuilder().setUsername(user.getUsername()).setPassword(user.getPassword()).build();
        Protobufs.Request request= Protobufs.Request.newBuilder().setType(Protobufs.Request.Type.Logout)
                .setUser(userProto).build();
        return request;
    }

    public static String getError(Protobufs.Response response) {
        return response.getError();
    }

    public static User getUser(Protobufs.Request request) {
        User user = new User(request.getUser().getEmail(),request.getUser().getUsername(),request.getUser().getPassword());
        return user;
    }

    public static User getUser(Protobufs.Response response) {
        User user = new User(response.getUser().getEmail(),response.getUser().getUsername(),response.getUser().getPassword());
        return user;
    }

    public static Protobufs.Response createOkResponse() {
        Protobufs.Response response = Protobufs.Response.newBuilder().setType(Protobufs.Response.Type.Ok).build();
        return response;
    }

    public static Protobufs.Response createErrorResponse(String message) {
        Protobufs.Response response = Protobufs.Response.newBuilder().setType(Protobufs.Response.Type.Error).setError(message).build();
        return response;
    }
}
