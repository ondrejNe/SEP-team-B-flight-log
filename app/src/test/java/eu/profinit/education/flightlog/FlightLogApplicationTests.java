package eu.profinit.education.flightlog;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = IntegrationTestConfig.class)
@Transactional
@ActiveProfiles({"stub", "inttest"})
class FlightLogApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void flightsInAirAndLanding() throws Exception {
        ResponseEntity<List<Map<?, ?>>> flightsResponse = restTemplate.exchange("/flight/inAir", HttpMethod.GET, null,
            new ParameterizedTypeReference<>() {
            });
        List<Map<?, ?>> flightsBefore = flightsResponse.getBody();

        int initialFlightsCount = flightsBefore.size();
        assertTrue(initialFlightsCount >= 1, "There should be at least one flight");
        assertEquals(5, flightsBefore.get(0).get("id"));
        assertEquals("2018-10-25T12:30:00", flightsBefore.get(0).get("takeoffTime"));


        String inputJson = readFileToString("landInput.json");
        HttpEntity<String> request = createRequestEntityWithHeaders(inputJson);
        ResponseEntity<Object> response = restTemplate.exchange("/flight/land", HttpMethod.POST, request, Object.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        ResponseEntity<List<Map<?, ?>>> flightsResponse2 = restTemplate.exchange("/flight/inAir", HttpMethod.GET, null,
            new ParameterizedTypeReference<>() {
            });
        List<Map<?, ?>> flightsAfter = flightsResponse2.getBody();

        assertEquals(initialFlightsCount - 1,
            flightsAfter.size(), "There should one flight less than at the beginning");
    }

    @Test
    void takeoff() throws Exception {
        String inputJson = readFileToString("takeoffInput.json");

        HttpEntity<String> request = createRequestEntityWithHeaders(inputJson);
        ResponseEntity<Object> response = restTemplate.exchange("/flight/takeoff", HttpMethod.POST, request,
            Object.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    private HttpEntity<String> createRequestEntityWithHeaders(String inputJson) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(inputJson, headers);
    }

    private String readFileToString(String fileName) throws IOException, URISyntaxException {
        return new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource(fileName)).toURI())));
    }

}