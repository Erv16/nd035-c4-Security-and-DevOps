package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserController userController;
    private final CartRepository cartRepository = mock(CartRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setup() {
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);

        User user = new User();
        Cart cart = new Cart();

        user.setId(0L);
        user.setUsername("batman");
        user.setPassword("alfredPennyworth");
        user.setCart(cart);

        when(userRepository.findByUsername("batman")).thenReturn(user);
        when(userRepository.findById(0L)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername("bruce")).thenReturn(null);
        when(encoder.encode("alfredPennyworth")).thenReturn("alfredPennyworthHashed");
    }

    @Test
    public void create_user_test() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("batman");
        request.setPassword("alfredPennyworth");
        request.setConfirmPassword("alfredPennyworth");
        final ResponseEntity<User> response = userController.createUser(request);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User user = response.getBody();
        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals("batman", user.getUsername());
        assertEquals("alfredPennyworthHashed", user.getPassword());
    }

    @Test
    public void create_user_password_which_is_short_test() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("batman");
        request.setPassword("alfred");
        request.setConfirmPassword("alfred");
        final ResponseEntity<User> response = userController.createUser(request);
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void find_user_by_username_test() {
        final ResponseEntity<User> response = userController.findByUserName("batman");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User user = response.getBody();
        assertNotNull(user);
        assertEquals("batman", user.getUsername());
    }

    @Test
    public void find_user_by_a_username_that_does_not_exist_test() {
        final ResponseEntity<User> response = userController.findByUserName("bruce");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void find_user_by_id_test() {
        final ResponseEntity<User> response = userController.findById(0L);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User user = response.getBody();
        assertNotNull(user);
        assertEquals(0, user.getId());
    }

    @Test
    public void find_user_by_an_id_that_does_not_exist_test() {
        final ResponseEntity<User> response = userController.findById(1L);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }
}
