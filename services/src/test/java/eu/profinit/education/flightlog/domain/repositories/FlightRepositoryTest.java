package eu.profinit.education.flightlog.domain.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import eu.profinit.education.flightlog.AbstractIntegrationTest;
import eu.profinit.education.flightlog.domain.entities.Flight;


public class FlightRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private FlightRepository testSubject;

    @Test
    public void shouldLoadFlights() {

        List<Flight> flights = testSubject.findAll();

        assertEquals(5, flights.size(), "There should be 5 flights");

    }

    //@Disabled("Testovana metoda neni implementovana")
    @Test
    public void shouldLoadGliderFlights() {
        // TODO 2.2: Zmente volanou testovaci metodu, aby vratila vsechny lety kluzaku a smažte anotaci @Disabled
        List<Flight> flights = testSubject.findAllByFlightType(Flight.Type.GLIDER);

        assertEquals(2, flights.size(), "There should be 2 glider flights");
    }

    //@Disabled("Testovana metoda neni implementovana")
    @Test
    public void shouldLoadFlightsInTheAir() {
        // TODO 2.4: Doplňte název testované metody a smažte anotaci @Disabled
        List<Flight> flights = testSubject.findAllByLandingTimeNullOrderByTakeoffTimeAscIdAsc();// testSubject.*
        
        assertEquals(3, flights.size(), "There should be 3 flights");
        assertEquals(5L, flights.get(0).getId().getId().longValue(),
                "Flight with ID 5 started first and should be first");
        assertEquals(1L, flights.get(1).getId().getId().longValue(), "Flight with ID 1 should be second");
        assertEquals(2L, flights.get(2).getId().getId().longValue(), "Flight with ID 2 should be third");
    }
}