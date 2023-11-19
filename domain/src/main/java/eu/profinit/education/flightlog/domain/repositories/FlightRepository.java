package eu.profinit.education.flightlog.domain.repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import eu.profinit.education.flightlog.domain.entities.Flight;

public interface FlightRepository extends JpaRepository<Flight, Long> {

    // TODO 2.1: Upravte metodu findAll tak, aby podle jejího názvu SpringData správně načtla všechny lety kluzáků (Fligh.Type.GLIDER)
    List<Flight> findAll();

    List<Flight> findAllByFlightType(Flight.Type flightType);

    // TODO 2.3: Vytvořte metodu podle jejíhož názvu SpringData správně načte lety, které jsou právě ve vzduchu
    // Tip: Lety by se měly řadit od nejstarších a v případě shody podle ID tak, aby vlečná byla před kluzákem, který táhne
    // Tip: Výsledek si můžete ověřit v testu k této tříde v modulu services

    List<Flight> findAllByLandingTimeNullOrderByTakeoffTimeAscIdAsc();

    // TODO 8.1: Vytvorte metodu pro nacteni vlecnych letu pro vytvoreni dvojice letu na obrazovce Report
}

