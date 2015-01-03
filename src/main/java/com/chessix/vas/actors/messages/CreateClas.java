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

import java.time.LocalDateTime;

/**
 * Create a new VAS CLAS
 */
public abstract class CreateClas {

    @ToString
    @EqualsAndHashCode(callSuper = false)
    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Request extends ZeroMQMessage<Request> {
        private static final long serialVersionUID = 665333946148389852L;
        @Getter
        String clasId;
        @Getter
        LocalDateTime timestamp;

        private Request(final RequestBuilder requestBuilder) {
            this.clasId = requestBuilder.clasId;
            this.timestamp = requestBuilder.timestamp;
        }

        @Override
        public ByteString payload(final Serialization ser) {
            return ByteString.fromArray(ser.serialize(new Request(clasId, timestamp)).get());
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
        String message;

        private Response(final ResponseBuilder responseBuilder) {
            this.successful = responseBuilder.successful;
            this.clasId = responseBuilder.clasId;
            this.message = responseBuilder.message;
        }
    }

    public static class RequestBuilder implements Builder<Request> {
        private String clasId;
        private LocalDateTime timestamp;

        public RequestBuilder(final String clasId) {
            this.clasId = clasId;
            this.timestamp = LocalDateTime.now();
        }

        public Request build() {
            return new Request(this);
        }
    }

    public static class ResponseBuilder implements Builder<Response> {
        private boolean successful;
        private String clasId;
        private String message;

        public ResponseBuilder(final boolean successful) {
            this.successful = successful;
        }

        public ResponseBuilder clasId(final String clasId) {
            this.clasId = clasId;
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
