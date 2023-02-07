package com.aquiliz.booking.concertticketapp.messaging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TicketMessage {

  private Long ticketPurchaseId;

  private String issuedTo;

  private Integer ticketsCount;

  private Long concertId;

  private String concertName;

}
