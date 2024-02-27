package com.backend.Aspect;

import com.backend.Controller.AuthController;
import com.backend.Entity.Organization;
import com.backend.Entity.User;
import com.backend.Entity.UserOrganizationRole;
import com.backend.Repository.UserOrganizationRoleRepository;
import com.backend.Repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.naming.AuthenticationException;
import java.util.Map;

@Aspect
@Component
public class AuthorizationAspect {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserOrganizationRoleRepository userOrganizationRoleRepository;

    @Before("@annotation(RequiresAuthorization)")
    public void verifyUserDetails() throws AuthenticationException {

        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();

        String organizationId = request.getHeader("organizationId");
        String accessToken = request.getHeader("accessToken");

        System.out.println("Aspect chal raha h: " + accessToken);

        if (accessToken == null || accessToken.isEmpty()) {
            throw new AuthenticationException("You are not authorized to access this endpoint");
        } else {
            String googleApiUrl = "https://oauth2.googleapis.com/tokeninfo?id_token=" + accessToken;

            // Make a request to Google's token verification endpoint
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map> responseEntity = restTemplate.getForEntity(googleApiUrl, Map.class);

            Map<String, Object> userInfo = responseEntity.getBody();
            assert userInfo != null;
            String name = userInfo.get("name").toString();
            String email = userInfo.get("email").toString();

            User newUser = userRepository.findByUserEmail(email);
            System.out.println("Aspect: " + newUser);
            if (newUser == null) {
                throw new AuthenticationException("You need to login first");
            } else {
                UserOrganizationRole userOrganizationRole = userOrganizationRoleRepository.findByUser(newUser);
                Organization organization = userOrganizationRole.getOrganization();

                if (!organization.getOrganizationId().equals(organizationId))
                    throw new AuthenticationException("You are not authorized to access this data");
            }
        }

        // If requestBody is not found or if the method signature changes unexpectedly
    }
}

