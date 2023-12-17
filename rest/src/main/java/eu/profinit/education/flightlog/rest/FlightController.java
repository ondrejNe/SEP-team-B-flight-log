package eu.profinit.education.flightlog.rest;

import eu.profinit.education.flightlog.domain.entities.FlightId;
import eu.profinit.education.flightlog.exceptions.ExternalSystemException;
import eu.profinit.education.flightlog.exceptions.FlightLogException;
import eu.profinit.education.flightlog.exceptions.NotFoundException;
import eu.profinit.education.flightlog.exceptions.ValidationException;
import eu.profinit.education.flightlog.service.CsvExportService;
import eu.profinit.education.flightlog.service.FlightService;
import eu.profinit.education.flightlog.to.FileExportTo;
import eu.profinit.education.flightlog.to.FlightLandingTo;
import eu.profinit.education.flightlog.to.FlightTakeoffTo;
import eu.profinit.education.flightlog.to.FlightTo;
import eu.profinit.education.flightlog.to.FlightTuppleTo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.ResponseEntity.status;

@RestController
@RequiredArgsConstructor
@Slf4j
public class FlightController {

    private final FlightService flightService;
    private final CsvExportService csvExportService;

    @RequestMapping("flight/inAir")
    public List<FlightTo> getFlightInTheAir(){
        List<FlightTo> flightsInTheAir = flightService.getFlightsInTheAir();
        log.debug("Flights in the air:\n{}", flightsInTheAir);
        return flightsInTheAir;
    }

    @GetMapping("flight/report")
    public List<FlightTuppleTo> getFlightReport(){
        return flightService.getFlightsForReport();
    }

    @PostMapping("/flight/takeoff")
    @ResponseStatus(HttpStatus.CREATED)
    public void takeoff(@RequestBody FlightTakeoffTo start) {
        log.debug("Start\n{}", start);

        flightService.takeoff(start);
    }

    @PostMapping("/flight/land")
    public void land(@RequestBody FlightLandingTo landing) {
        log.debug("Land\n{}", landing);

        flightService.land(FlightId.of(landing.getFlightId()), landing.getLandingTime());
    }

    @RequestMapping("flight/export")
    public ResponseEntity<byte[]> getCsvExport(){
        FileExportTo flightsCsv = csvExportService.getAllFlightsAsCsv();
        return ResponseEntity.ok()
            .contentType(flightsCsv.getContentType())
            .header("Content-Disposition", "attachment; filename=" + flightsCsv.getFileName())
            .body(flightsCsv.getContent());
    }

    @ExceptionHandler({ NotFoundException.class, ValidationException.class})
    public ResponseEntity<String> handleClientException(FlightLogException e) {
        return status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler({ ExternalSystemException.class})
    public ResponseEntity<String> handleExternalSystemException(FlightLogException e) {
        return status(HttpStatus.SERVICE_UNAVAILABLE).body(e.getMessage());
    }


}