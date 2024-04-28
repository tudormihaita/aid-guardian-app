package dot.help.networking.utils;

import dot.help.networking.ProtobufWorker;
import dot.help.services.IServices;

import java.net.Socket;

public class ProtobufConcurrentServer extends AbstractConcurrentServer {
    private final IServices server;

    public ProtobufConcurrentServer(int port, IServices server) {
        super(port);
        this.server = server;
    }

    @Override
    protected Thread createWorker(Socket client) {
        ProtobufWorker worker = new ProtobufWorker(server, client);

        return new Thread(worker);
    }
}
