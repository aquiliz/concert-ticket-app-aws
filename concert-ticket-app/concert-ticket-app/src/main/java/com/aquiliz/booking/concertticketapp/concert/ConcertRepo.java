package com.aquiliz.booking.concertticketapp.concert;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "concerts", path = "concerts")
public interface ConcertRepo extends CrudRepository<ConcertData, Long> {}
