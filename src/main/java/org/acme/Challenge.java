package org.acme;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/search")
public class Challenge {

    @Inject
    Search search;

    @GET
    @Path("/offers/{departureAirport}+{countAdults}+{countChildren}+{days}+{earliestDepartureDate}+{latestDepartureDate}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Offer> getOffers(@PathParam("departureAirport") String departureAirport,
            @PathParam("countAdults") int countAdults, @PathParam("countChildren") int countChildren,
            @PathParam("days") int days, @PathParam("earliestDepartureDate") String earliestDepartureDate,
            @PathParam("latestDepartureDate") String latestDepartureDate) {
        return search.searchHotelsWithOffers(departureAirport, countAdults, countChildren,
                days, earliestDepartureDate, latestDepartureDate);
    }

    @GET
    @Path("/hotels/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Hotel getHotel(@PathParam("id") int id) {
        return search.searchHotel(id);
    }

    @GET
    @Path("/hotels")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Hotel> getHotels() {
        return search.parseHotelData();
    }

    @GET
    @Path("/split")
    @Produces(MediaType.TEXT_PLAIN)
    public int splitData() {
        return search.splittingOffersByDays();
    }

}