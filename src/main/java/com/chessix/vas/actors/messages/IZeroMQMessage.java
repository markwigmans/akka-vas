package com.chessix.vas.actors.messages;

import akka.serialization.Serialization;
import akka.util.ByteString;

import java.io.Serializable;

/**
 *
 */
public interface IZeroMQMessage {

    ByteString topicAsByteString();

    String topic();

    ByteString payload(Serialization ser);

}
