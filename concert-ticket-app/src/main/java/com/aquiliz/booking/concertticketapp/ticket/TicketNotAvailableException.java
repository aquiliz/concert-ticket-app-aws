package com.aquiliz.booking.concertticketapp.ticket;

public class TicketNotAvailableException extends RuntimeException {

  public TicketNotAvailableException(String msg) {
    super(msg);
  }

}
