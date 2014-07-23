/******************************************************************************
 Copyright 2014 Mark Wigmans

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 ******************************************************************************/
package com.chessix.vas.actors.messages;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

/**
 * Transfer amount between two accounts
 *
 * @author Mark Wigmans
 */
public class Transfer {

    @ToString
    @EqualsAndHashCode
    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    public static final class Request {
        @Getter
        String from;
        @Getter
        String to;
        @Getter
        int amount;

        private Request(final RequestBuilder requestBuilder) {
            this.from = requestBuilder.from;
            this.to = requestBuilder.to;
            this.amount = requestBuilder.amount;
        }
    }

    @ToString
    @EqualsAndHashCode
    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    public static final class Response {
        @Getter
        boolean successful;
        @Getter
        String message;

        private Response(final ResponseBuilder responseBuilder) {
            this.successful = responseBuilder.successful;
            this.message = responseBuilder.message;
        }
    }

    public static class RequestBuilder implements Builder<Request> {
        private String from;
        private String to;
        private int amount;

        public RequestBuilder(final String from, final String to, final int amount) {
            this.from = from;
            this.to = to;
            this.amount = amount;
        }

        public Request build() {
            return new Request(this);
        }
    }

    public static class ResponseBuilder implements Builder<Response> {
        private boolean successful;
        private String message;

        public ResponseBuilder(final boolean successful) {
            this.successful = successful;
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
