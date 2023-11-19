package eu.profinit.education.flightlog.rest;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import eu.profinit.education.flightlog.service.AirplaneService;
import eu.profinit.education.flightlog.to.AirplaneTo;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AirplaneController {

    // TODO 3.1: Vystavte REST endpoint vracející seznam klubových letadel
    // Tip: letadla získáte voláním AirplaneService
    // Tip: bude se volat metoda GET na /airplane
    // Tip: struktura odpovědi je dána objektem AirplaneTo

    private final AirplaneService airplaneService;

    @GetMapping("/airplane")
    public List<AirplaneTo> getClubAirplanes() {
        return airplaneService.getClubAirplanes();
    }
}
