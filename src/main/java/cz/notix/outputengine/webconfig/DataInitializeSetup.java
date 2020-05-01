package cz.notix.outputengine.webconfig;

import cz.notix.outputengine.ExceptionHandling.CustomException;
import cz.notix.outputengine.Model.User;
import cz.notix.outputengine.Services.UserService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.security.SecureRandom;
import java.util.Random;

@Component
public class DataInitializeSetup {

    private final UserService userService;

    private final String username = "admin";
    private final int PASSWORD_LENGTH = 20;
    private String password;

    public DataInitializeSetup(UserService userService) {
        this.userService = userService;
    }

    @PostConstruct
    @Transactional
    void initialize() throws CustomException {
        final String passwordToPrint;

        if (!userService.userAlreadyExists(username)) {//if user was not created we fetch password from db otherwise will be generate new password

            generatePassword(PASSWORD_LENGTH);
            passwordToPrint = password;

            User user = new User(username, password);
            userService.save(user);
        } else {
            passwordToPrint = userService.getDecryptedPassword(username);
        }
        System.out.println("\n\n >>>>>>>>> New PASSWORD for authorization was generated :  " + passwordToPrint + "\n\n");
    }

    private void generatePassword(int length) {

        final Random RANDOM = new SecureRandom();
        final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ!#$^%@:abcdefghijklmnopqrstuvwxyz";

        StringBuilder generatedValue = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            generatedValue.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        password = new String(generatedValue);
    }
}
