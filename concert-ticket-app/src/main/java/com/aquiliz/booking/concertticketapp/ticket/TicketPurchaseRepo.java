package com.aquiliz.booking.concertticketapp.ticket;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "tickets", path = "tickets")
public interface TicketPurchaseRepo extends CrudRepository<TicketPurchaseData, Long> {}
