package com.chessix.vas.actors.messages;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

/**
 * Count the number of records
 *
 * @author Mark Wigmans
 */
public class Count {

    @ToString
    @EqualsAndHashCode
    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    public static final class Request {
        @Getter
        private final String clasId;

        private Request(final RequestBuilder requestBuilder) {
            this.clasId = requestBuilder.clasId;
        }
    }

    @ToString
    @EqualsAndHashCode
    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    public static final class Response {
        @Getter
        private final boolean successful;
        @Getter
        private final String clasId;
        @Getter
        private final Long count;
        @Getter
        private final String message;

        private Response(final ResponseBuilder responseBuilder) {
            this.successful = responseBuilder.successful;
            this.clasId = responseBuilder.clasId;
            this.count = responseBuilder.count;
            this.message = responseBuilder.message;
        }
    }

    public static class RequestBuilder implements Builder<Request> {
        private String clasId;

        public RequestBuilder(final String clasId) {
            this.clasId = clasId;
        }

        public Request build() {
            return new Request(this);
        }
    }

    public static class ResponseBuilder implements Builder<Response> {
        private boolean successful;
        private String clasId;
        private Long count;
        private String message;

        public ResponseBuilder(final boolean successful) {
            this.successful = successful;
        }

        public ResponseBuilder clasId(final String clasId) {
            this.clasId = clasId;
            return this;
        }

        public ResponseBuilder count(final Long count) {
            this.count = count;
            return this;
        }

        public ResponseBuilder message(final String message) {
            this.message = message;
            return this;
        }

        public Response build() {
            return new Response(this);
        }
    }
}
