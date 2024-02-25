package hh.ont.shiftbooking.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import hh.ont.shiftbooking.model.User;

public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByUsername(String username);
}
