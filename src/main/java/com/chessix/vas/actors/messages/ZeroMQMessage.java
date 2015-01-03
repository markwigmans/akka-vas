package com.chessix.vas.actors.messages;

import akka.util.ByteString;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * Created by mawi on 28-12-2014.
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

