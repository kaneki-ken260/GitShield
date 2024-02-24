package com.backend.Aspect;

import com.backend.Controller.AuthController;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Aspect
@Component
public class AuthorizationAspect {

    @Autowired
    private AuthController authController;
    @Before("@annotation(com.backend.Aspect.RequiresAuthorization)")
    public ResponseEntity<?> verifyUserDetails(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();

        if (args.length > 0 && args[0] instanceof String) {
            String accessToken = (String) args[0];

            System.out.println(accessToken);

//            if (idToken == null) {
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
//            }

            try {
                return (ResponseEntity<?>) joinPoint.proceed();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            throw new IllegalArgumentException("Method annotated with @RequiresAuthorization must have a String parameter for the access token");
        }

        // If requestBody is not found or if the method signature changes unexpectedly
    }
}

