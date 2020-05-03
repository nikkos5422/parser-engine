package cz.nikkos.JsonParserengine.Repository;

import cz.nikkos.JsonParserengine.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

}