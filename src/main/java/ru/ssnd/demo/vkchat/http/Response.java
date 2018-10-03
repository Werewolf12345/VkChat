package ru.ssnd.demo.vkchat.http;

import com.google.gson.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class Response extends ResponseEntity<String> {

    private final static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").excludeFieldsWithoutExposeAnnotation().create();

    public enum Error {

        MALFORMED_REQUEST(1),
        INTERNAL_ERROR(2),
        EXTERNAL_ERROR(3),
        UNAUTHORIZED(4, HttpStatus.UNAUTHORIZED),
        NO_SUCH_USER(5),
        WRONG_PASSWORD(6),
        LOW_BALANCE(7),
        COOLDOWN(8),
        NO_SOCIAL_ACCOUNT(9),
        UNKNOWN_SOCIAL_NETWORK(10),
        UNPAID_FEATURE(11),
        INACTIVE_USER(12),
        TOO_SHORT_PERIOD(13),
        SESSION_ERROR(14);

        private final int code;
        private final HttpStatus status;
        private final String message;

        Error(int code, HttpStatus status) {
            this.code = code;
            this.message = name();
            this.status = status;
        }

        Error(int code) {
            this.code = code;
            this.message = name();
            this.status = HttpStatus.BAD_REQUEST;
        }

        public JSONObject getBody(String customMessage, JSONObject extra) {
            return new JSONObject()
                    .put("error", new JSONObject()
                            .put("code", code)
                            .put("message", customMessage == null ? message : customMessage)
                            .put("extra", extra)
                    );
        }

        public HttpStatus getStatus() {
            return status;
        }
    }

    public static class Builder {

        private JSONObject json;
        private HttpStatus status;

        public Builder() {
            this.json = new JSONObject();
            this.status = HttpStatus.OK;
        }

        public Builder(HttpStatus status) {
            json = new JSONObject();
            this.status = status;
        }

        public Builder withField(String key, Object value) {
            json.put(key, value);
            return this;
        }

        public <T> Builder withEntityField(String key, T entity) {
            json.put(key, new JSONObject(gson.toJson(entity)));
            return this;
        }

        public Builder withField(String key, JsonObject o) {
            json.put(key, new JSONObject(o.toString()));
            return this;
        }

        public Builder withField(String key, JsonArray a) {
            json.put(key, new JSONArray(a.toString()));
            return this;
        }

        public Response build() {
            return new Response(json, status);
        }

    }

    public static HttpHeaders getCommonHeaders() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "application/json; charset=utf-8");
        responseHeaders.add("X-XSS-Protection", "0");
        responseHeaders.set("P3P", "CP=\"IDC DSP COR ADM DEVi TAIi PSA` PSD IVAi IVDi CONi HIS OUR IND CNT\"");
        responseHeaders.set("Cache-Control", "no-cache, no-store, must-revalidate");
        responseHeaders.set("Pragma", "no-cache");
        responseHeaders.set("Expires", "0");
        return responseHeaders;
    }

    public Response(Error error) {
        this(error, null);
    }

    public Response() {
        this(new JSONObject());
    }

    public Response(Error error, String message) {
        this(error, message, null);
    }

    public Response(Error error, String message, JSONObject extra) {
        this(error.getBody(message, extra), error.getStatus());
    }

    public Response(String body, HttpStatus status) {
        super(body, getCommonHeaders(), status);
    }

    public Response(JSONObject body, HttpStatus status) {
        this(body.toString(), status);
    }

    public Response(JSONObject body) {
        this(body.toString(), HttpStatus.OK);
    }

    public Response(JsonElement body) {
        this(body.toString(), HttpStatus.OK);
    }

}
