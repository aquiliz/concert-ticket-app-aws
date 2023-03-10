package com.aquiliz.booking.concertticketapp.messaging;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessageChannel;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MessageSender {

  @Value("${ticket-app.ticket-purchase-queue-url}")
  private String queueUrl;

  private final AmazonSQSAsync amazonSqs;

  private final ObjectMapper objectMapper = new ObjectMapper();

  public MessageSender(AmazonSQSAsync amazonSqs) {
    this.amazonSqs = amazonSqs;
  }

  public void send(TicketMessage ticketMessage) {
    MessageChannel messageChannel
        = new QueueMessageChannel(amazonSqs, queueUrl);
    Message<String> msg = null;
    try {
      msg = MessageBuilder.withPayload(objectMapper.writeValueAsString(ticketMessage))
          .build();
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Failed to serialize TicketBooking to json string", e);
    }
    log.debug("About to send message to queue: '{}' , message: {}", queueUrl, ticketMessage);
    boolean sentStatus = messageChannel.send(msg, 5000);
    if (sentStatus) {
      log.info("Successfully sent ticket purchase message to queue={} message={}", queueUrl,
          ticketMessage);
    } else {
      log.warn("There was a problem sending message to queue={}", queueUrl);
    }
  }
}
