package com.backend.Controller;

//import com.backend.Aspect.RequiresAuthorization;
import com.backend.Aspect.RequiresAuthorization;
import com.backend.Entity.Organization;
import com.backend.Entity.Role;
import com.backend.Entity.User;
import com.backend.Entity.UserOrganizationRole;
import com.backend.Repository.RoleRepository;
import com.backend.Repository.UserOrganizationRoleRepository;
import com.backend.Repository.UserRepository;
import com.backend.Service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

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

    public boolean validateUser(String accessToken, String organizationId){
        if(accessToken==null || accessToken.isEmpty()) return false;

        else{
            String googleApiUrl = "https://oauth2.googleapis.com/tokeninfo?id_token=" + accessToken;

            // Make a request to Google's token verification endpoint
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map> responseEntity = restTemplate.getForEntity(googleApiUrl, Map.class);

            Map<String, Object> userInfo = responseEntity.getBody();
            assert userInfo != null;
            String name = userInfo.get("name").toString();
            String email = userInfo.get("email").toString();

            User newUser = userRepository.findByUserEmail(email);

            if(newUser==null) return false;

            else{
                UserOrganizationRole userOrganizationRole = userOrganizationRoleRepository.findByUser(newUser);
                Organization organization = userOrganizationRole.getOrganization();

                return organization.getOrganizationId().equals(organizationId);
            }
        }
    }
    @PostMapping("/auth")
    public JsonNode receiveToken(HttpServletRequest request){
        //System.out.println("Credential:" + requestBody.get("credential"));
        try {
            String idToken = request.getHeader("credential");
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

    public JsonNode checkUserInfo(String name, String email){

        // Check if the user exists in the database
        User user = userService.loginUser(email);

        if (user == null) {
            // If the user doesn't exist, create a new user
            User newUser = userService.createUser(email, name);
            // Redirect or perform any other action for new users
            UserOrganizationRole userOrganizationRole = userOrganizationRoleRepository.findByUser(newUser); // Get a new user from that table
            Role role = userOrganizationRole.getRole(); // Accessing that user's role
            Organization organization = userOrganizationRole.getOrganization();

            //Setting the org-id to access the findings table
            JsonNode userData;

            // Create an ObjectMapper
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode objectNode = objectMapper.createObjectNode();

            objectNode.put("role", getRoleOfCurrentUser(role));
            objectNode.put("orgId", getOrganizationIdOfCurrentUser(organization));
            objectNode.put("name", name);

            userData = objectNode;

            System.out.println("Role of new user: " + getRoleOfCurrentUser(role));

            //return "New User Created: " + newUser.getUserName();
            return userData;
        } else {

            UserOrganizationRole userOrganizationRole = userOrganizationRoleRepository.findByUser(user); // Get already existing user from that table
            Role role = userOrganizationRole.getRole();
            Organization organization = userOrganizationRole.getOrganization();

            //Setting the org-id to access the findings table
            JsonNode userData;

            // Create an ObjectMapper
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode objectNode = objectMapper.createObjectNode();

            objectNode.put("role", getRoleOfCurrentUser(role));
            objectNode.put("orgId", getOrganizationIdOfCurrentUser(organization));
            objectNode.put("name", name);

            userData = objectNode;

            System.out.println("Existing user logging in: " + getRoleOfCurrentUser(role));

            return userData;
        }
    }

    public String getRoleOfCurrentUser(Role role){
        return role.getRoleName();
    }

    public String getOrganizationIdOfCurrentUser(Organization organization){
        return organization.getOrganizationId();
    }
}
