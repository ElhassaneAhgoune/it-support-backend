package com.tc.userauth.repository;

import static com.tc.userauth.testdata.TestUserBuilder.createUser;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class UserRepositoryIT extends JpaTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldFindUserByUsername() {
        final var testUser = userRepository.save(createUser());
        final var foundUser = userRepository.findByUsername(testUser.getUsername());

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo(testUser.getUsername());
    }

    @Test
    void shouldCheckIfUserExistsByUsername() {
        final var testUser = userRepository.save(createUser());
        final var exists = userRepository.existsByUsername(testUser.getUsername());

        assertThat(exists).isTrue();
    }

    @Test
    void shouldCheckIfUserExistsByEmail() {
        final var testUser = userRepository.save(createUser());
        final var exists = userRepository.existsByEmail(testUser.getEmail());

        assertThat(exists).isTrue();
    }

    @Test
    void shouldSaveAndRetrieveUser() {
        final var createUser = createUser();
        final var savedUser = userRepository.save(createUser);

        final var retrievedUser = userRepository.findById(savedUser.getId());

        assertThat(retrievedUser).isPresent();
        assertThat(retrievedUser.get().getUsername()).isEqualTo(createUser.getUsername());
        assertThat(retrievedUser.get().getEmail()).isEqualTo(createUser.getEmail());
    }

    @Test
    void shouldDeleteUser() {
        final var testUser = userRepository.save(createUser());
        userRepository.delete(testUser);

        final var deletedUser = userRepository.findById(testUser.getId());

        assertThat(deletedUser).isNotPresent();
    }

}