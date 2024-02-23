package com.backend.Controller;

import com.backend.Entity.Organization;
import com.backend.Entity.Role;
import com.backend.Entity.User;
import com.backend.Entity.UserOrganizationRole;
import com.backend.Repository.RoleRepository;
import com.backend.Repository.UserOrganizationRoleRepository;
import com.backend.Repository.UserRepository;
import com.backend.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Objects;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserOrganizationRoleRepository userOrganizationRoleRepository;

    @PostMapping("/auth")
    public String receiveToken(@RequestBody Map<String, String> requestBody){
        //System.out.println("Credential:" + requestBody.get("credential"));
        try {
            String idToken = requestBody.get("credential");
            String googleApiUrl = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken;

            // Make a request to Google's token verification endpoint
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map> responseEntity = restTemplate.getForEntity(googleApiUrl, Map.class);

            Map<String, Object> userInfo = responseEntity.getBody();
            assert userInfo != null;
            String name = userInfo.get("name").toString();
            String email = userInfo.get("email").toString();
            //System.out.println(userInfo.get("name"));
            //System.out.println(userInfo.get("email"));

            //System.out.println(checkUserInfo(name,email));

            return checkUserInfo(name,email);
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Handle error
        }
    }

    public String checkUserInfo(String name, String email){

        // Check if the user exists in the database
        User user = userService.loginUser(email);

        if (user == null) {
            // If the user doesn't exist, create a new user
            User newUser = userService.createUser(email, name);
            // Redirect or perform any other action for new users
            UserOrganizationRole userOrganizationRole = userOrganizationRoleRepository.findByUser(newUser); // Get a new user from that table
            Role role = userOrganizationRole.getRole(); // Accessing that user's role
            Organization organization = userOrganizationRole.getOrganization();

            System.out.println("Role of new user: " + getRoleOfCurrentUser(role));

            //return "New User Created: " + newUser.getUserName();
            return getRoleOfCurrentUser(role);
        } else {

            UserOrganizationRole userOrganizationRole = userOrganizationRoleRepository.findByUser(user); // Get already existing user from that table
            Role role = userOrganizationRole.getRole();
            Organization organization = userOrganizationRole.getOrganization();
            
            System.out.println("Existing user logging in: " + getRoleOfCurrentUser(role));

            return getRoleOfCurrentUser(role);
        }
    }

    public String getRoleOfCurrentUser(Role role){
        return role.getRoleName();
    }
}
