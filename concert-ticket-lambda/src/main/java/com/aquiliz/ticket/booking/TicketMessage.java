package com.aquiliz.ticket.booking;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TicketMessage implements Serializable {

  private Long ticketPurchaseId;

  private String issuedTo;

  private Integer ticketsCount;

  private Long concertId;

  private String concertName;

}
