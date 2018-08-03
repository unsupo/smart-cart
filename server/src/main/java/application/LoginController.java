package application;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@EnableAutoConfiguration
@RequestMapping("/")
@CrossOrigin
public class LoginController {
    /**
     * @param username
     * @param password
     * @return token containing secret
     */
    @RequestMapping(value = "/login", method = POST)
    public String login(@RequestHeader(value = "username") String username,
                        @RequestHeader(value = "password") String password) throws Exception {
        if(username.equals("a") && password.equals("a")) {
            return "aaaa";
        }else
            throw new Exception("Illegal login exception");
    }
    /**
     * @return token containing secret
     */
    @RequestMapping(value = "/isValidToken", method = POST)
    public boolean isValidToken(@RequestHeader(value = "token") String token) throws Exception {
        if("aaaa".equals(token)) {
            return true;
        }else
            throw new Exception("Illegal login exception");
    }
}
