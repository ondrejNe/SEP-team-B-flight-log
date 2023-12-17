package eu.profinit.education.flightlog.dao;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import eu.profinit.education.flightlog.exceptions.ExternalSystemException;

@Component
@Profile("!stub")
public class ClubDatabaseDaoImpl implements ClubDatabaseDao {

    private final RestTemplate restTemplate;
    private final String clubDbBaseUrl;

    public ClubDatabaseDaoImpl(
        @Value("${integration.clubDb.baseUrl}") String clubDbBaseUrl) {
        this.restTemplate = new RestTemplate();
        this.clubDbBaseUrl = clubDbBaseUrl;
    }

    @Override
    public List<User> getUsers() {
        User[] userList;
        try {
            userList = restTemplate.getForObject(clubDbBaseUrl + "/club/user", User[].class);
            return Arrays.asList(userList);

        } catch (RuntimeException e) {
            throw new ExternalSystemException("Cannot get users from Club database. URL: {}. Call resulted in exception.", e, clubDbBaseUrl);
        }
    }
}
