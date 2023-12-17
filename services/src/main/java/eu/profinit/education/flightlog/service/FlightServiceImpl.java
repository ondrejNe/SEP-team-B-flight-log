package eu.profinit.education.flightlog.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import eu.profinit.education.flightlog.common.Clock;
import eu.profinit.education.flightlog.domain.entities.Airplane;
import eu.profinit.education.flightlog.domain.entities.Flight;
import eu.profinit.education.flightlog.domain.entities.FlightId;
import eu.profinit.education.flightlog.domain.entities.Person;
import eu.profinit.education.flightlog.domain.fields.Task;
import eu.profinit.education.flightlog.domain.repositories.ClubAirplaneRepository;
import eu.profinit.education.flightlog.domain.repositories.FlightRepository;
import eu.profinit.education.flightlog.exceptions.NotFoundException;
import eu.profinit.education.flightlog.exceptions.ValidationException;
import eu.profinit.education.flightlog.to.AirplaneTo;
import eu.profinit.education.flightlog.to.FlightTakeoffTo;
import eu.profinit.education.flightlog.to.FlightTo;
import eu.profinit.education.flightlog.to.FlightTuppleTo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class FlightServiceImpl implements FlightService {

    private static final int MAX_RECORDS_IN_GUI = 2000;

    private final FlightRepository flightRepository;
    private final ClubAirplaneRepository clubAirplaneRepository;
    private final Clock clock;

    private final PersonService personService;

    /**
     * Initiates takeoff for a flight.
     *
     * @param flightStart Details for initiating the flight takeoff.
     * @throws ValidationException if required data for takeoff is missing.
     */
    @Override
    public void takeoff(FlightTakeoffTo flightStart) {
        if (flightStart.getTakeoffTime() == null) {
            throw new ValidationException("Takeoff time is null.");
        }
        Flight towPlaneFlight = createTowPlaneFlight(flightStart);
        Flight gliderFlight = createGliderFlight(flightStart);
        if (gliderFlight != null) {
            towPlaneFlight.setGliderFlight(gliderFlight);
            flightRepository.save(towPlaneFlight);
            gliderFlight.setTowplaneFlight(towPlaneFlight);
            flightRepository.save(gliderFlight);
        }
    }


    private Flight createTowPlaneFlight(FlightTakeoffTo flightStart) {
        if (flightStart.getTowplane() == null) {
            throw new ValidationException("Towplane must be set.");
        }
        Airplane airplane = getAirplane(flightStart.getTowplane().getAirplane());

        Person pilot = personService.getExistingOrCreatePerson(flightStart.getTowplane().getPilot());
        Person copilot = personService.getExistingOrCreatePerson(flightStart.getTowplane().getCopilot());

        Flight flight = new Flight(Flight.Type.TOWPLANE, Task.TOWPLANE_TASK, flightStart.getTakeoffTime(), airplane, pilot, copilot, flightStart.getTowplane().getNote());
        return flightRepository.save(flight);
    }

    private Flight createGliderFlight(FlightTakeoffTo flightStart) {
        if (flightStart.getGlider() == null) {
            return null;
        }
        Airplane airplane = getAirplane(flightStart.getGlider().getAirplane());

        Person pilot = personService.getExistingOrCreatePerson(flightStart.getGlider().getPilot());
        Person copilot = personService.getExistingOrCreatePerson(flightStart.getGlider().getCopilot());

        Flight flight = new Flight(Flight.Type.GLIDER, Task.of(flightStart.getTask()), flightStart.getTakeoffTime(), airplane, pilot, copilot, flightStart.getGlider().getNote());
        return flightRepository.save(flight);
    }

    private Airplane getAirplane(AirplaneTo airplaneTo) {
        if (airplaneTo.getId() != null) {
            return Airplane.clubAirplane(clubAirplaneRepository.findById(airplaneTo.getId()).orElseThrow(() -> new IllegalArgumentException("Club airplane does not exists")));
        } else {
            return Airplane.guestAirplane(airplaneTo.getImmatriculation(), airplaneTo.getType());
        }
    }

    /**
     * Marks the landing of a flight with the given ID and landing time.
     *
     * @param flightId     ID of the flight.
     * @param landingTime  Time when the flight landed.
     * @throws NotFoundException      if the flight with the given ID does not exist.
     * @throws ValidationException    if landing time is before takeoff time or the flight has already landed.
     */
    @Override
    public void land(FlightId flightId, LocalDateTime landingTime) {
        Assert.notNull(flightId, "Flight ID cannot be null");
        if (landingTime == null) {
            landingTime = clock.now();
        }
        Flight flight = flightRepository.findById(flightId.getId()).orElseThrow(() -> new NotFoundException("Flight with ID {} does not exists.", flightId));
        if (!landingTime.isAfter(flight.getTakeoffTime())) {
            throw new ValidationException("Given landing time {} cannot be before takeoffTime {}", landingTime, flight.getTakeoffTime());
        }
        if (flight.getLandingTime() != null) {
            throw new ValidationException("Flight with ID {} has already landed", flight.getId());
        }

        flight.setLandingTime(landingTime);
        flightRepository.save(flight);
    }

    /**
     * Retrieves a list of flights currently in the air (Landing time == null).
     *
     * @return List of FlightTo representing flights in the air.
     */
    @Transactional(readOnly = true)
    @Override
    public List<FlightTo> getFlightsInTheAir() {
        return flightRepository.findAllByLandingTimeNullOrderByTakeoffTimeAscIdAsc()
            .stream()
            .map(FlightTo::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Retrieves flights data for generating a report.
     *
     * @return List of FlightTuppleTo representing flights for a report.
     */
    @Override
    public List<FlightTuppleTo> getFlightsForReport() {
        List<FlightTuppleTo> result = new ArrayList<>();
        FlightTuppleTo.FlightTuppleToBuilder builder = FlightTuppleTo.builder();
        List<Flight> allTowPlanes = flightRepository.findAllByFlightTypeOrderByTakeoffTimeAscIdAsc(Flight.Type.TOWPLANE);
        for (Flight singleFlight : allTowPlanes) {
            Flight gliderFlight = singleFlight.getGliderFlight();
            result.add(
                builder.towplane(FlightTo.fromEntity(singleFlight))
                    .glider(FlightTo.fromEntity(gliderFlight))
                    .build()
            );
        }
        return result;
    }
}