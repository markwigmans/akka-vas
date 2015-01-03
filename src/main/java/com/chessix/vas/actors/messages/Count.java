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

import akka.serialization.Serialization;
import akka.util.ByteString;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * Count the number of records
 */
public class Count {

    @ToString
    @EqualsAndHashCode(callSuper = false)
    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Request extends ZeroMQMessage<Request> {
        private static final long serialVersionUID = 5334898878450448273L;
        @Getter
        private final String clasId;

        private Request(final RequestBuilder requestBuilder) {
            super(Request.class);
            this.clasId = requestBuilder.clasId;
        }

        @Override
        public ByteString payload(final Serialization ser) {
            return ByteString.fromArray(ser.serialize(new Request(clasId)).get());
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
