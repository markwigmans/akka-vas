package com.chessix.vas.actors.messages;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

/**
 * Create a new VAS account
 *
 * @author Mark Wigmans
 */
public class CreateAccount {

    @ToString
    @EqualsAndHashCode
    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    public static final class Request {
        @Getter
        String clasId;
        @Getter
        String accountId;

        private Request(final RequestBuilder requestBuilder) {
            this.clasId = requestBuilder.clasId;
            this.accountId = requestBuilder.accountId;
        }
    }

    @ToString
    @EqualsAndHashCode
    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    public static final class Response {
        @Getter
        boolean successful;
        @Getter
        String clasId;
        @Getter
        String accountId;
        @Getter
        String message;

        private Response(final ResponseBuilder responseBuilder) {
            this.successful = responseBuilder.successful;
            this.clasId = responseBuilder.clasId;
            this.accountId = responseBuilder.accountId;
            this.message = responseBuilder.message;
        }
    }

    public static class RequestBuilder implements Builder<Request> {
        private String clasId;
        private String accountId;

        public RequestBuilder(final String clasId) {
            this.clasId = clasId;
        }

        public RequestBuilder accountId(final String accountId) {
            this.accountId = accountId;
            return this;
        }

        public Request build() {
            return new Request(this);
        }
    }

    public static class ResponseBuilder implements Builder<Response> {
        private boolean successful;
        private String clasId;
        private String accountId;
        private String message;

        public ResponseBuilder(final boolean successful) {
            this.successful = successful;
        }

        public ResponseBuilder clasId(final String clasId) {
            this.clasId = clasId;
            return this;
        }

        public ResponseBuilder accountId(final String accountId) {
            this.accountId = accountId;
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
