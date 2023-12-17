package eu.profinit.education.flightlog.rest;

import java.util.List;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import eu.profinit.education.flightlog.service.AirplaneService;
import eu.profinit.education.flightlog.to.AirplaneTo;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AirplaneController {

    private final AirplaneService airplaneService;

    @GetMapping("/airplane")
    public List<AirplaneTo> getClubAirplanes() {
        return airplaneService.getClubAirplanes();
    }

}
