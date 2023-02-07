package com.aquiliz.booking.concertticketapp.config;

import com.aquiliz.booking.concertticketapp.concert.ConcertData;
import com.aquiliz.booking.concertticketapp.ticket.TicketPurchaseData;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

public class RestConfiguration implements RepositoryRestConfigurer {

  /**
   * Override Spring data rest's default settings and include entity's id to the returned json from
   * the REST API
   */
  @Override
  public void configureRepositoryRestConfiguration(
      RepositoryRestConfiguration config, CorsRegistry cors) {
    config.exposeIdsFor(ConcertData.class);
    config.exposeIdsFor(TicketPurchaseData.class);
  }
}
