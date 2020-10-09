package com.graphaware.pizzeria.service.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.graphaware.pizzeria.model.Pizza;

/**
 * 
 * @author bebeyazit
 *
 *         Depending on the customer requirements, campaigns can not be merged
 *         and the the cheapest rule will be chosen.
 */
public class SimpleCampaignHandler implements CampaignHandler {

	private List<Pizza> pizzas = new ArrayList<Pizza>();

	/**
	 * 
	 * @return the cheapest amount depending on the campaigns
	 */
	public double handle(List<Pizza> pizzas) {
		this.pizzas = pizzas;
		return Math.min(buyPineApplePizzaAnd10PercentOffTheOthers(), buy3PizzasTheCheapestOfThe3IsFree());
	}

	/**
	 * If there is at least one pineapple pizza then the others 10% cheaper
	 * 
	 * @return
	 */
	private double buyPineApplePizzaAnd10PercentOffTheOthers() {
		
		String specialTopping = "pineapple"; 
		
		double totalPrice = 0;
		if (pizzas == null) {
			return 0.0;
		}
		boolean applyPineappleDiscount = false;
		for (Pizza pizza : pizzas) {
			if (pizza.getToppings()!=null & pizza.getToppings().contains(specialTopping)) {
				applyPineappleDiscount = true;
				// no need to continue
				break;
			}
		}
		for (Pizza pizza : pizzas) {
			if (pizza.getToppings()!=null && pizza.getToppings().contains(specialTopping)) {
				totalPrice += pizza.getPrice();
			} else {
				if (applyPineappleDiscount) {
					totalPrice += pizza.getPrice() * 0.9;
				} else {
					totalPrice += pizza.getPrice();
				}
			}
		}
		return totalPrice;
	}

	/**
	 * Buy three pizza and cheapes of the each 3 is free TODO complexity
	 * optimization.
	 * 
	 * @return
	 * 
	 */
	private double buy3PizzasTheCheapestOfThe3IsFree() {
		if (pizzas == null || pizzas.size() == 0)
			return 0;
		double sum = pizzas.stream().mapToDouble(x -> x.getPrice()).sum();

		double discount = 0;
		if (pizzas.size() > 2) {
			int freePizzasCount = pizzas.size() / 3;
			double[] prices = new double[pizzas.size()];
			for (int i = 0; i < pizzas.size(); i++) {
				prices[i] = pizzas.get(i).getPrice();
			}
			Arrays.sort(prices);

			for (int i = 0; i < freePizzasCount; i++) {
				discount += prices[i];
			}
		}

		return sum - discount;

	}
}
