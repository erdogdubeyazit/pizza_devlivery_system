package com.graphaware.pizzeria;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.graphaware.pizzeria.model.Pizza;
import com.graphaware.pizzeria.model.PizzeriaUser;
import com.graphaware.pizzeria.model.Purchase;
import com.graphaware.pizzeria.repository.PizzeriaUserRepository;
import com.graphaware.pizzeria.repository.PurchaseRepository;
import com.graphaware.pizzeria.security.PizzeriaUserPrincipal;
import com.graphaware.pizzeria.service.PurchaseService;

@ExtendWith(MockitoExtension.class)
public class CampaignTest {

	@Mock
	private PizzeriaUserRepository userRepository;

	@Mock
	private PurchaseRepository purchaseRepository;

	private PizzeriaUser currentUser;

	private PurchaseService purchaseService;

	@BeforeEach
	void setUp() {
		purchaseService = new PurchaseService(purchaseRepository, userRepository);
		currentUser = new PizzeriaUser();
		currentUser.setName("Papa");
		currentUser.setId(666L);
		currentUser.setRoles(Collections.emptyList());
		currentUser.setEmail("abc@def.com");

		Authentication authentication = Mockito.mock(Authentication.class);
		SecurityContext securityContext = Mockito.mock(SecurityContext.class);
		Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);
		PizzeriaUserPrincipal userPrincipal = new PizzeriaUserPrincipal(currentUser);
		Mockito.when(authentication.getPrincipal()).thenReturn(userPrincipal);
	}

	@Test
	void testNoCampaign() {
		Pizza one = new Pizza();
		one.setId(666L);
		one.setPrice(10.0);
		one.setToppings(Arrays.asList("onioun"));
		Pizza two = new Pizza();
		two.setId(43L);
		two.setPrice(10.0);
		two.setToppings(Arrays.asList("beef"));

		Purchase p = new Purchase();
		p.setPizzas(Arrays.asList(one, two));

		when(purchaseRepository.findFirstByStateEquals(any())).thenReturn(p);
		when(purchaseRepository.findById(any())).thenReturn(Optional.of(p));
		purchaseService.pickPurchase();

		purchaseService.completePurchase(p.getId());

		ArgumentCaptor<Purchase> purchaseCaptor = ArgumentCaptor.forClass(Purchase.class);
		verify(purchaseRepository, times(2)).save(purchaseCaptor.capture());
		Purchase saved = purchaseCaptor.getValue();

		assertThat(saved.getAmount()).isEqualTo(20);
	}

	@Test
	void testOnlyBuyPineApplePizzaAnd10PercentOffTheOthers() {
		Pizza one = new Pizza();
		one.setId(666L);
		one.setPrice(10.0);
		one.setToppings(Arrays.asList("pineapple"));
		Pizza two = new Pizza();
		two.setId(43L);
		two.setPrice(10.0);
		two.setToppings(Arrays.asList("beef"));

		Purchase p = new Purchase();
		p.setPizzas(Arrays.asList(one, two));

		when(purchaseRepository.findFirstByStateEquals(any())).thenReturn(p);
		when(purchaseRepository.findById(any())).thenReturn(Optional.of(p));
		purchaseService.pickPurchase();

		purchaseService.completePurchase(p.getId());

		ArgumentCaptor<Purchase> purchaseCaptor = ArgumentCaptor.forClass(Purchase.class);
		verify(purchaseRepository, times(2)).save(purchaseCaptor.capture());
		Purchase saved = purchaseCaptor.getValue();

		assertThat(saved.getAmount()).isEqualTo(19);
	}

	@Test
	void testOnlyBuy3PizzasTheCheapestOfThe3IsFree() {
		Pizza one = new Pizza();
		one.setId(666L);
		one.setPrice(15.0);
		one.setToppings(Arrays.asList("pastrami"));
		Pizza two = new Pizza();
		two.setId(43L);
		two.setPrice(10.0);
		two.setToppings(Arrays.asList("beef"));
		Pizza three = new Pizza();
		three.setId(433L);
		three.setPrice(5.0);
		three.setToppings(Arrays.asList("onion"));

		Purchase p = new Purchase();
		p.setPizzas(Arrays.asList(one, two));

		when(purchaseRepository.findFirstByStateEquals(any())).thenReturn(p);
		when(purchaseRepository.findById(any())).thenReturn(Optional.of(p));
		purchaseService.pickPurchase();

		purchaseService.completePurchase(p.getId());

		ArgumentCaptor<Purchase> purchaseCaptor = ArgumentCaptor.forClass(Purchase.class);
		verify(purchaseRepository, times(2)).save(purchaseCaptor.capture());
		Purchase saved = purchaseCaptor.getValue();

		assertThat(saved.getAmount()).isEqualTo(25);
	}
	
	@Test
	void testBuy3PizzasTheCheapestOfThe3IsFreeAndBuy3PizzasTheCheapestOfThe3IsFreeTogether() {
		Pizza one = new Pizza();
		one.setId(666L);
		one.setPrice(15.0);
		one.setToppings(Arrays.asList("pastrami"));
		Pizza two = new Pizza();
		two.setId(43L);
		two.setPrice(10.0);
		two.setToppings(Arrays.asList("beef"));
		Pizza three = new Pizza();
		three.setId(433L);
		three.setPrice(5.0);
		three.setToppings(Arrays.asList("pineapple"));

		Purchase p = new Purchase();
		p.setPizzas(Arrays.asList(one, two, three));

		when(purchaseRepository.findFirstByStateEquals(any())).thenReturn(p);
		when(purchaseRepository.findById(any())).thenReturn(Optional.of(p));
		purchaseService.pickPurchase();

		purchaseService.completePurchase(p.getId());

		ArgumentCaptor<Purchase> purchaseCaptor = ArgumentCaptor.forClass(Purchase.class);
		verify(purchaseRepository, times(2)).save(purchaseCaptor.capture());
		Purchase saved = purchaseCaptor.getValue();

		assertThat(saved.getAmount()).isEqualTo(25);
	}
}
