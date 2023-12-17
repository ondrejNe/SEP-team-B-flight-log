package eu.profinit.education.flightlog.domain.repositories;

import java.time.LocalDateTime;
import java.util.List;

import eu.profinit.education.flightlog.domain.entities.Person;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import eu.profinit.education.flightlog.domain.entities.Flight;

public interface FlightRepository extends JpaRepository<Flight, Long> {

    List<Flight> findAll();

    List<Flight> findAllByFlightType(Flight.Type flightType);

    List<Flight> findAllByLandingTimeNullOrderByTakeoffTimeAscIdAsc();

    List<Flight> findAllByFlightTypeOrderByTakeoffTimeAscIdAsc(Flight.Type flightType);
}

