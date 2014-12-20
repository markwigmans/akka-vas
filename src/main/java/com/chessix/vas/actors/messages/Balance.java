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
public class Balance {

    @ToString
    @EqualsAndHashCode
    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    public static final class Request {
        @Getter
        String accountId;

        private Request(final RequestBuilder requestBuilder) {
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
        Integer amount;
        @Getter
        String message;

        private Response(final ResponseBuilder responseBuilder) {
            this.successful = responseBuilder.successful;
            this.amount = responseBuilder.amount;
            this.message = responseBuilder.message;
        }
    }

    public static class RequestBuilder implements Builder<Request> {
        private String accountId;

        public RequestBuilder(final String accountId) {
            this.accountId = accountId;
        }

        public Request build() {
            return new Request(this);
        }
    }

    public static class ResponseBuilder implements Builder<Response> {
        private boolean successful;
        private Integer amount;
        private String message;

        public ResponseBuilder(final boolean successful) {
            this.successful = successful;
        }

        public ResponseBuilder amount(final Integer amount) {
            this.amount = amount;
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
