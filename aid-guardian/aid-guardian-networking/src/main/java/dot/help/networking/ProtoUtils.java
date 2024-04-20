package dot.help.networking;

import dot.help.model.User;

public class ProtoUtils {
    public static Protobufs.Request createLoginRequest(User organiser){
        Protobufs.User organiserDTO=Protobufs.User.newBuilder().setUsername(organiser.getUsername()).setPassword(organiser.getPassword()).build();
        Protobufs.Request request= Protobufs.Request.newBuilder().setType(Protobufs.Request.Type.Login)
                .setUser(organiserDTO).build();
        return request;
    }

    public static String getError(Protobufs.Response response) {
        return response.getError();
    }
}
