package webserver;

import controller.Controller;
import controller.ControllerConstructor;
import controller.model.HttpRequest;
import controller.model.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream();
             OutputStream out = connection.getOutputStream()) {
            final HttpRequest httpRequest = HttpRequest.from(in);
            final Controller controller = ControllerConstructor.getOf(httpRequest);
            final HttpResponse httpResponse = controller.process(httpRequest);
            httpResponse.write(out);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
