package com.backend.Repository;

import com.backend.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    User findByUserEmail(String userEmail);

    User findByUserId(String userId);

}
