package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    private OrderController orderController;
    private final OrderRepository orderRepository = mock(OrderRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);

    @Before
    public void setup() {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);
        TestUtils.injectObjects(orderController, "userRepository", userRepository);

        Item item = new Item();
        item.setId(1L);
        item.setName("Batmobile");
        BigDecimal price = BigDecimal.valueOf(100000);
        item.setPrice(price);
        item.setDescription("Heavily armored tactical response vehicle");
        List<Item> items = new ArrayList<>();
        items.add(item);

        User user = new User();
        Cart cart = new Cart();

        user.setId(0L);
        user.setUsername("batman");
        user.setPassword("alfredPennyworth");
        cart.setId(0L);
        cart.setUser(user);
        cart.setItems(items);
        BigDecimal total = BigDecimal.valueOf(100000);
        cart.setTotal(total);
        user.setCart(cart);
        when(userRepository.findByUsername("batman")).thenReturn(user);
        when(userRepository.findByUsername("bruce")).thenReturn(null);
    }

    @Test
    public void submit_order_successfully_test() {
        ResponseEntity<UserOrder> response = orderController.submit("batman");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        UserOrder order = response.getBody();
        assertNotNull(order);
        assertEquals(1, order.getItems().size());
    }

    @Test
    public void submit_order_for_a_user_that_does_not_exist_test() {
        ResponseEntity<UserOrder> response = orderController.submit("bruce");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void get_orders_for_a_user_test() {
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("batman");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        List<UserOrder> userOrders = response.getBody();
        assertNotNull(userOrders);
    }

    @Test
    public void get_orders_for_a_user_that_does_not_exist() {
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("bruce");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }
}
