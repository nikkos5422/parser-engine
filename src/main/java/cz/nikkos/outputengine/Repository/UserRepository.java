package cz.nikkos.outputengine.Repository;

import cz.nikkos.outputengine.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

}