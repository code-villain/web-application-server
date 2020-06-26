package controller.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.joining;

public class HttpResponse {
    private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);

    private final int status;
    private final Map<String, String> headers;
    private final byte[] body;

    public HttpResponse(int status, Map<String, String> headers, byte[] body) {
        this.status = status;
        this.headers = headers;
        this.body = body;
    }

    public static Builder builder() {
        return new Builder();
    }

    public void write(OutputStream out) {
        DataOutputStream dos = new DataOutputStream(out);
        writeHeader(dos);
        writeBody(dos);
    }

    private void writeHeader(DataOutputStream dos) {
        try {
            dos.writeBytes("HTTP/1.1 " + status + " \r\n");
            for (Map.Entry<String, String> header : headers.entrySet()) {
                dos.writeBytes(header.getKey() + ": " + header.getValue() + "\r\n");
            }
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void writeBody(DataOutputStream dos) {
        try {
            if (Objects.nonNull(body)) {
                dos.write(body, 0, body.length);
            }
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public static class Builder {
        private int status;
        private final Map<String, String> headers;
        private final Map<String, String> cookies;
        private byte[] body;

        public Builder() {
            headers = new HashMap<>();
            cookies = new HashMap<>();
        }

        public Builder status(int status) {
            this.status = status;
            return this;
        }

        public Builder header(String key, String value) {
            headers.put(key, value);
            return this;
        }

        public Builder header(String key, int value) {
            headers.put(key, String.valueOf(value));
            return this;
        }

        public Builder cookie(String key, boolean value) {
            cookies.put(key, String.valueOf(value));
            return this;
        }

        public Builder redirect(String location) {
            headers.put("Location", location);
            return this;
        }

        public Builder body(byte[] body) {
            this.body = body;
            return this;
        }

        public HttpResponse build() {
            putCookiesOnHeader();
            return new HttpResponse(status, headers, body);
        }

        private void putCookiesOnHeader() {
            if (cookies.isEmpty()) {
                return;
            }

            final String joinedCookies = this.cookies.entrySet()
                    .stream()
                    .map(e -> e.getKey() + "=" + e.getValue())
                    .collect(joining(";"));

            headers.put("Set-Cookie", joinedCookies);
        }
    }
}
