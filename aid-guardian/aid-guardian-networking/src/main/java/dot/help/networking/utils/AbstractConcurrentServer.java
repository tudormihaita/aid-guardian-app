package dot.help.networking.utils;

import java.net.Socket;

public abstract class AbstractConcurrentServer extends AbstractServer {
    public AbstractConcurrentServer(int port) {
        super(port);
    }

    protected abstract Thread createWorker(Socket client);

    @Override
    protected void processRequest(Socket client) {
        Thread workerThread = createWorker(client);
        workerThread.start();
    }
}
