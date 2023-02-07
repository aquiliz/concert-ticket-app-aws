package com.aquiliz.booking.concertticketapp.ticket;

import com.aquiliz.booking.concertticketapp.concert.ConcertData;
import com.aquiliz.booking.concertticketapp.concert.ConcertRepo;
import com.aquiliz.booking.concertticketapp.messaging.MessageSender;
import com.aquiliz.booking.concertticketapp.messaging.TicketMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class TicketPurchaseService {

  private final TicketPurchaseRepo ticketPurchaseRepo;

  private final ConcertRepo concertRepo;

  private final MessageSender messageSender;

  public TicketPurchaseService(TicketPurchaseRepo ticketPurchaseRepo, ConcertRepo concertRepo,
      MessageSender messageSender) {
    this.ticketPurchaseRepo = ticketPurchaseRepo;
    this.concertRepo = concertRepo;
    this.messageSender = messageSender;
  }

  @Transactional(isolation = Isolation.SERIALIZABLE)
  public String makeTicketPurchase(TicketPurchaseData ticketPurchaseData) {
    ConcertData concertData = concertRepo.findById(ticketPurchaseData.getConcertId()).orElseThrow(
        () -> new IllegalArgumentException(
            "Concert with id=" + ticketPurchaseData.getConcertId() + " does not exist."));
    if (concertData.getAvailableTickets() < ticketPurchaseData.getTicketsCount()) {
      throw new TicketNotAvailableException(
          "There are no " + ticketPurchaseData.getTicketsCount() + " tickets available for concert "
              + concertData.getName());
    }
    concertData.setAvailableTickets(
        concertData.getAvailableTickets() - Long.valueOf(ticketPurchaseData.getTicketsCount()));
    concertRepo.save(concertData);
    log.info("Available ticket quantity for concert id={} name={} has been decremented with {}",
        concertData.getId(), concertData.getName(), ticketPurchaseData.getTicketsCount());

    TicketPurchaseData saved = ticketPurchaseRepo.save(ticketPurchaseData);
    log.info("Successfully saved ticket with id={} for concert name={}", saved.getId(),
        concertData.getName());

    messageSender.send(toMessage(ticketPurchaseData, concertData));

    return String.valueOf(saved.getId());
  }

  private TicketMessage toMessage(TicketPurchaseData ticketPurchaseData, ConcertData concertData) {
    TicketMessage ticketMessage = new TicketMessage();
    ticketMessage.setTicketPurchaseId(ticketPurchaseData.getId());
    ticketMessage.setTicketsCount(ticketPurchaseData.getTicketsCount());
    ticketMessage.setIssuedTo(ticketPurchaseData.getIssuedTo());
    ticketMessage.setConcertId(concertData.getId());
    ticketMessage.setConcertName(concertData.getName());
    return ticketMessage;
  }
}
