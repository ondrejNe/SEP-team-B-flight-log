package eu.profinit.education.flightlog.service;


import eu.profinit.education.flightlog.dao.ClubDatabaseDao;
import eu.profinit.education.flightlog.dao.User;
import eu.profinit.education.flightlog.domain.entities.Address;
import eu.profinit.education.flightlog.domain.entities.Person;
import eu.profinit.education.flightlog.domain.repositories.PersonRepository;
import eu.profinit.education.flightlog.exceptions.NotFoundException;
import eu.profinit.education.flightlog.to.PersonTo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;
    private final ClubDatabaseDao clubDatabaseDao;

    /**
     * Retrieves a list of club members.
     *
     * @return List of PersonTo representing club members.
     */
    @Override
    public List<PersonTo> getClubMembers() {
        return clubDatabaseDao.getUsers().stream().map(this::convert2To)
            .toList();
    }

    private User getClubMemberById(long memberId) {
        return clubDatabaseDao.getUsers().stream()
            .filter(user -> user.getMemberId() == memberId)
            .findFirst()
            .orElseThrow(() -> new NotFoundException("Club member with ID {} does not exist.", memberId));
    }

    /**
     * Retrieves an existing club member or creates a new person entity based on the provided PersonTo.
     *
     * @param personTo PersonTo object representing the person's details.
     * @return Person object representing an existing club member or a newly created person entity.
     */
    @Override
    public Person getExistingOrCreatePerson(PersonTo personTo) {
        if (personTo == null) {
            return null;
        }
        if (personTo.getMemberId() != null) {
            User userFromBackend = getClubMemberById(personTo.getMemberId());
            Optional<Person> maybeClubMember = personRepository.findByMemberId(userFromBackend.getMemberId());
            if (maybeClubMember.isPresent()) {
                return maybeClubMember.get();
            } else {
                Person clubMember = new Person(Person.Type.CLUB_MEMBER, personTo.getMemberId(), userFromBackend.getFirstName(), userFromBackend.getLastName());
                return personRepository.save(clubMember);
            }
        } else {
            Person guest = createGuestEntity(personTo);
            return personRepository.save(guest);
        }
    }

    private Person createGuestEntity(PersonTo personTo) {
        Address address = null;
        if (personTo.getAddress() != null) {
            address = new Address(personTo.getAddress().getStreet(), personTo.getAddress().getCity(), personTo.getAddress().getPostalCode(), personTo.getAddress().getCountry());
        }
        return new Person(Person.Type.GUEST, personTo.getFirstName(), personTo.getLastName(), address);
    }

    private PersonTo convert2To(User user) {
        return PersonTo.ofClubMember(user.getMemberId(), user.getFirstName(), user.getLastName());
    }

}