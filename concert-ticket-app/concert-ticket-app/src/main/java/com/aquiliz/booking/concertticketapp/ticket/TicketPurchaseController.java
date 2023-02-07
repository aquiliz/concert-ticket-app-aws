package com.aquiliz.booking.concertticketapp.ticket;

import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Override some of the paths provided by the Spring data rest repository adding custom behavior
 */
@BasePathAwareController
public class TicketPurchaseController {

  private final TicketPurchaseService ticketPurchaseService;

  public TicketPurchaseController(TicketPurchaseService ticketPurchaseService) {
    this.ticketPurchaseService = ticketPurchaseService;
  }

  @PostMapping(path = "/tickets")
  public String makeTicketPurchase(@RequestBody TicketPurchaseData ticketPurchaseData) {
    return this.ticketPurchaseService.makeTicketPurchase(ticketPurchaseData);
  }

}
