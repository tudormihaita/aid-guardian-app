package dot.help.networking.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class AbstractServer {
    private final int port;
    private ServerSocket server = null;

    protected static final Logger log = LogManager.getLogger(AbstractServer.class);

    public AbstractServer(int port) {
        log.info("Initializing Server with port: " + port);
        this.port = port;
    }

    protected abstract void processRequest(Socket client);

    public void start() throws Exception {
        log.traceEntry("Starting Server...");
        try {
            server = new ServerSocket(port);

            while(true) {
                log.info("Waiting for clients...");
                Socket client = server.accept();
                log.info("Client connected...");
                processRequest(client);
                log.info("Request processed for accepted client");
            }
        } catch (IOException exception) {
            log.error("Staring server error: " + exception.getMessage());
            throw new Exception("Starting server error: " + exception.getMessage());
        }
        finally {
            shutdown();
        }
    }

    public void shutdown() throws Exception {
        log.traceEntry("Shutting down server...");
        try {
            server.close();
            log.traceExit("Server successfully shut down.");
        } catch (IOException exception) {
            throw new Exception("Closing server error: " + exception);
        }
    }
}

