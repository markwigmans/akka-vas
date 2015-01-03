/******************************************************************************
 Copyright 2014,2015 Mark Wigmans

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

import akka.util.ByteString;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 *
 */
public abstract class ZeroMQMessage<T> implements IZeroMQMessage, Serializable {
    private static final long serialVersionUID = -1138990912299565887L;

    // transient so the serializer doesn't serialize this variable
    private final transient Optional<Class<T>> clazz;

    protected ZeroMQMessage() {
        this.clazz = Optional.empty();
    }

    protected ZeroMQMessage(final Class<T> clazz) {
        this.clazz = Optional.of(clazz);
    }

    @Override
    public ByteString topicAsByteString() {
        return ByteString.fromString(clazz.map(Class::getName).orElse(""), StandardCharsets.UTF_8.name());
    }

    @Override
    public String topic() {
        return clazz.map(Class::getName).orElse("");
    }
}

