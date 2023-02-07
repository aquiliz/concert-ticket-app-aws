package com.aquiliz.booking.concertticketapp.ticket;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "ticket_purchases")
public class TicketPurchaseData {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "issued_to", nullable = false)
  private String issuedTo;

  @Column(name = "tickets_count", nullable = false)
  private Integer ticketsCount;

  @Column(name = "concert_id", nullable = false)
  private Long concertId;
}
