package cz.nikkos.JsonParserengine.Services;

import cz.nikkos.JsonParserengine.ExceptionHandling.CustomException;
import cz.nikkos.JsonParserengine.Model.User;
import cz.nikkos.JsonParserengine.Repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final TrippleDesService trippleDesService;

    public UserService(UserRepository userRepository, TrippleDesService trippleDesService) {
        this.userRepository = userRepository;
        this.trippleDesService = trippleDesService;
    }

    public void save(User user) throws CustomException {
        user.setPassword(trippleDesService.encrypt(user.getPassword()));
        userRepository.save(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean validatePassword(String inputPassword, String username) throws CustomException {
        return getDecryptedPassword(username).equalsIgnoreCase(inputPassword);
    }

    public String getDecryptedPassword(String username) throws CustomException {
        return trippleDesService.decrypt(findByUsername(username).getPassword());

    }

    public boolean userAlreadyExists(String username) {
        return findByUsername(username) != null;
    }
}
