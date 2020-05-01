package cz.notix.outputengine.Controllers;

import cz.notix.outputengine.ExceptionHandling.CustomException;
import cz.notix.outputengine.Model.User;
import cz.notix.outputengine.Services.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*", allowCredentials = "true", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.PUT})
public class UserController {


    private final UserService userService;

    private final String username = "admin";

    public UserController( UserService userService) {
        this.userService = userService;
    }


    @RequestMapping("/login")
    public boolean login(@RequestBody User inputUser) throws CustomException {
        User existingUser = userService.findByUsername(username);

        return inputUser.getUsername().equalsIgnoreCase(existingUser.getUsername()) && userService.validatePassword(inputUser.getPassword(), inputUser.getUsername());
    }
}
