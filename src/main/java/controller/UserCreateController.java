package controller;

import controller.model.HttpRequest;
import db.DataBase;
import controller.model.HttpResponse;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static util.HttpRequestUtils.parseQueryString;

class UserCreateController implements Controller {
    private static final Controller instance = new UserCreateController();
    private static final Logger log = LoggerFactory.getLogger(UserCreateController.class);

    private UserCreateController() {}

    public static Controller getInstance() {
        return instance;
    }

    @Override
    public HttpResponse process(HttpRequest request) {
        final Map<String, String> content = request.getParsedBody();
        final User newUser = new User(content);

        log.info("Create User: {}", newUser);
        DataBase.addUser(newUser);

        return HttpResponse.builder()
                .status(302)
                .redirect("/")
                .build();
    }
}
