package eu.profinit.education.flightlog.domain.repositories;

import eu.profinit.education.flightlog.AbstractIntegrationTest;
import eu.profinit.education.flightlog.domain.entities.Flight;
import eu.profinit.education.flightlog.domain.entities.Person;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PersonRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private PersonRepository testSubject;

    private final Long testClubMemberId = 1L;

    private final Long testClubMember2Id = 2L;

    @Test
    void shouldLoadAllClubMembers() {
        List<Person> clubMembers = testSubject
            .findAllByPersonTypeOrderByLastNameAscFirstNameAsc(Person.Type.CLUB_MEMBER);

        assertEquals(2, clubMembers.size(), "There should be 2 club members");
        assertEquals(testClubMember2Id, clubMembers.get(0).getMemberId(), "First Member ID should be 2");
        assertEquals(testClubMemberId, clubMembers.get(1).getMemberId(), "First Member ID should be 1");

    }

    @Test
    void shouldFindClubMemberByMemberId() {
        Optional<Person> maybeClubMember = testSubject.findByMemberId(testClubMemberId);

        assertTrue(maybeClubMember.isPresent(), "Club member should be found");
        assertEquals(testClubMemberId, maybeClubMember.get().getMemberId(), "Member ID should be 1");

    }

}