//package com.backend.Entity;
//
//import jakarta.persistence.*;
//
//@Entity
//public class Scan {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(name = "product_name")
//    private String productName;
//
//    @Column(name = "sub_product")
//    private String subProduct;
//
//    @Column(name = "scan_tool")
//    private String scanTool;
//
//    @Column(name = "scan_type")
//    private Integer scanType;
//
//    @Column(name = "scan_date")
//    private String scanDate;
//
//    @Column(name = "total_findings")
//    private Integer totalFindings;
//
//    @Column(name = "duplicate_findings")
//    private Integer duplicateFindings;
//
//    @Column(name = "resolved_findings")
//    private Integer resolvedFindings;
//
//    @Column(name = "new_findings")
//    private Integer newFindings;
//
//    // Constructors, getters, setters...
//
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public String getProductName() {
//        return productName;
//    }
//
//    public void setProductName(String productName) {
//        this.productName = productName;
//    }
//
//    public String getSubProduct() {
//        return subProduct;
//    }
//
//    public void setSubProduct(String subProduct) {
//        this.subProduct = subProduct;
//    }
//
//    public String getScanTool() {
//        return scanTool;
//    }
//
//    public void setScanTool(String scanTool) {
//        this.scanTool = scanTool;
//    }
//
//    public Integer getScanType() {
//        return scanType;
//    }
//
//    public void setScanType(Integer scanType) {
//        this.scanType = scanType;
//    }
//
//    public String getScanDate() {
//        return scanDate;
//    }
//
//    public void setScanDate(String scanDate) {
//        this.scanDate = scanDate;
//    }
//
//    public Integer getTotalFindings() {
//        return totalFindings;
//    }
//
//    public void setTotalFindings(Integer totalFindings) {
//        this.totalFindings = totalFindings;
//    }
//
//    public Integer getDuplicateFindings() {
//        return duplicateFindings;
//    }
//
//    public void setDuplicateFindings(Integer duplicateFindings) {
//        this.duplicateFindings = duplicateFindings;
//    }
//
//    public Integer getResolvedFindings() {
//        return resolvedFindings;
//    }
//
//    public void setResolvedFindings(Integer resolvedFindings) {
//        this.resolvedFindings = resolvedFindings;
//    }
//
//    public Integer getNewFindings() {
//        return newFindings;
//    }
//
//    public void setNewFindings(Integer newFindings) {
//        this.newFindings = newFindings;
//    }
//
//
//    // Add any additional fields and methods as needed
//}

// User Service to handle new or existing user

//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//@Service
//public class UserService {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private UserOrganizationRoleRepository userOrganizationRoleRepository;
//
//    public User getUserByEmail(String email) {
//        return userRepository.findByEmail(email);
//    }
//
//    public User createUser(String email, String name) {
//        // Create a new user with default role and organization
//        User user = new User();
//        user.setEmail(email);
//        user.setName(name);
//
//        // Set default role (admin for new users)
//        Role defaultRole = new Role();
//        defaultRole.setName("admin"); // Set default role as admin
//        user.setRole(defaultRole);
//
//        // Create a new organization for the user
//        Organization organization = new Organization();
//        organization.setName(name + "'s Organization");
//        // Set other organization properties as needed
//        user.setOrganization(organization);
//
//        // Save the user to the database
//        user = userRepository.save(user);
//
//        // Update UserOrganizationRole table
//        UserOrganizationRole userOrganizationRole = new UserOrganizationRole();
//        userOrganizationRole.setUser(user);
//        userOrganizationRole.setOrganization(user.getOrganization());
//        userOrganizationRole.setRole(user.getRole());
//        userOrganizationRoleRepository.save(userOrganizationRole);
//
//        return user;
//    }
//
//    public User loginUser(String email) {
//        // Retrieve the user by email
//        User user = getUserByEmail(email);
//
//        // Return the user if found
//        if (user != null) {
//            return user;
//        } else {
//            return null;
//        }
//    }
//}

//Updated AuthController

//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//public class AuthController {
//
//    @Autowired
//    private UserService userService;
//
//    @GetMapping("/user")
//    public String getUserInfo(@AuthenticationPrincipal OAuth2User principal) {
//        String email = principal.getAttribute("email");
//        String name = principal.getAttribute("name");
//
//        // Check if the user exists in the database
//        User user = userService.loginUser(email);
//
//        if (user == null) {
//            // If the user doesn't exist, create a new user
//            User newUser = userService.createUser(email, name);
//            // Redirect or perform any other action for new users
//            return "New User Created: " + newUser.getName();
//        } else {
//            // If the user exists, log them in according to their role
//            // You can implement your login logic here
//            // For example, redirect to appropriate page based on role
//            if ("admin".equals(user.getRole().getName())) {
//                // Redirect to admin dashboard
//                return "Logged In as Admin: " + user.getName();
//            } else {
//                // Redirect to employee dashboard
//                return "Logged In as Employee: " + user.getName();
//            }
//        }
//    }
//}


