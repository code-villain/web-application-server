package controller;

import controller.model.HttpRequest;
import controller.model.HttpResponse;

import java.io.IOException;

public interface Controller {
    HttpResponse process(HttpRequest httpRequest) throws IOException;
}
