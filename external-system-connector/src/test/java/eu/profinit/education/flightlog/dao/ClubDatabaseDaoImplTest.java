package eu.profinit.education.flightlog.dao;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestPropertySource(properties = {"integration.clubDb.baseUrl = http://vyuka.profinit.eu:8080"})
@ContextConfiguration
class ClubDatabaseDaoImplTest {

    @Autowired
    private ClubDatabaseDao testSubject;

    @Test
    void getUsers() {
        List<User> users = testSubject.getUsers();

        assertNotNull(users);
        assertTrue(users.size() > 5, "Should contains at least 5 items.");
        assertNotNull(users.get(0).getFirstName());
        assertNotNull(users.get(0).getLastName());
        assertNotNull(users.get(0).getMemberId());
        assertNotNull(users.get(0).getRoles());
    }

    @Configuration
    @ComponentScan
    static class IntegrationTestConfig {

    }
}