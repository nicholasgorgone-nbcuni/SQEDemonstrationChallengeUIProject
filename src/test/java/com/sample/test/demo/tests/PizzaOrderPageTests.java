package com.sample.test.demo.tests;

import java.util.List;

import org.testng.annotations.Test;
import org.testng.annotations.DataProvider;

import com.sample.test.demo.constants.PizzaTypes;
import com.sample.test.demo.constants.PizzaToppings;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertEquals;

public class PizzaOrderPageTests extends TestBase {

    /* Page Content Validation Samples. */

    @Test
    public void validate_pizza_options() {
        List<String> pizzaOptions = this.pizzaOrderPage.getPizzaOptions();
        assertEquals(PizzaTypes.values().length + 1, pizzaOptions.size());

        assertTrue(pizzaOptions.contains("Choose Pizza"));
        for (PizzaTypes pizzaType : PizzaTypes.values()) {
            assertTrue(
                    pizzaOptions.contains(String.format("%s $%.2f", pizzaType.getDisplayName(), pizzaType.getCost())));
        }
    }

    @Test
    public void validate_pizza_firsttopping_options() {
        List<String> pizzaTopping1Options = this.pizzaOrderPage.getPizzaTopping1Options();
        assertEquals(PizzaToppings.values().length + 1, pizzaTopping1Options.size());

        assertTrue(pizzaTopping1Options.contains("Choose a Toppings 1"));
        for (PizzaToppings pizzaTopping : PizzaToppings.values()) {
            assertTrue(pizzaTopping1Options.contains(pizzaTopping.getDisplayName()));
        }
    }

    @Test
    public void validate_pizza_secondtopping_options() {
        List<String> pizzaTopping1Options = this.pizzaOrderPage.getPizzaTopping2Options();
        assertEquals(PizzaToppings.values().length + 1, pizzaTopping1Options.size());

        assertTrue(pizzaTopping1Options.contains("Choose a Toppings 2"));
        for (PizzaToppings pizzaTopping : PizzaToppings.values()) {
            assertTrue(pizzaTopping1Options.contains(pizzaTopping.getDisplayName()));
        }
    }

    @Test
    public void validate_payment_instruments() {
        assertTrue(this.pizzaOrderPage.isCreditCardOptionEnabled());
        assertTrue(this.pizzaOrderPage.isCashPaymentOptionEnabled());
    }

    /* Sample Happy Path Test Case. */

    @Test
    public void order_successful_when_all_required_inputs_specified() {

        int quantity = 2;
        this.pizzaOrderPage.selectPizza(PizzaTypes.MEDIUM_TWOTOPPINGS.getDisplayName());
        this.pizzaOrderPage.selectPizzaTopping1(PizzaToppings.MOZZARELLA.getDisplayName());
        this.pizzaOrderPage.selectPizzaTopping2(PizzaToppings.ITALIANHAM.getDisplayName());
        this.pizzaOrderPage.setPizzaQuantity("2");
        this.pizzaOrderPage.setName("Test User");
        this.pizzaOrderPage.setPhone("111-111-1111");
        this.pizzaOrderPage.setEmail("someone@gmail.com");
        this.pizzaOrderPage.chooseCreditCardPayment();

        assertEquals("19.5", this.pizzaOrderPage.getCost());
        // Ensure Cost Text Box is readonly [Computed field]
        assertTrue(this.pizzaOrderPage.isCostReadOnly());

        this.pizzaOrderPage.placeOrder();
        String alertDialogText = this.pizzaOrderPage.getDialogText();
        System.out.println((alertDialogText));
        System.out.println(String.format("Thank you for your order! TOTAL: %.2f %s",
                quantity * PizzaTypes.MEDIUM_TWOTOPPINGS.getCost(), PizzaTypes.MEDIUM_TWOTOPPINGS.getDisplayName()));
        assertTrue(alertDialogText.contains(
                String.format("Thank you for your order! TOTAL: %.1f %s",
                        quantity * PizzaTypes.MEDIUM_TWOTOPPINGS.getCost(),
                        PizzaTypes.MEDIUM_TWOTOPPINGS.getDisplayName())));

        this.pizzaOrderPage.closeDialog();
    }

    /* Sample Negative test case */

    @Test
    public void order_fails_when_mandatory_fields_missing() {

        this.pizzaOrderPage.selectPizza(PizzaTypes.MEDIUM_TWOTOPPINGS.getDisplayName());
        this.pizzaOrderPage.selectPizzaTopping1(PizzaToppings.MOZZARELLA.getDisplayName());
        this.pizzaOrderPage.selectPizzaTopping2(PizzaToppings.ITALIANHAM.getDisplayName());
        this.pizzaOrderPage.setPizzaQuantity("2");
        this.pizzaOrderPage.setEmail("someone@gmail.com");
        this.pizzaOrderPage.chooseCreditCardPayment();

        assertEquals("19.5", this.pizzaOrderPage.getCost());
        assertTrue(this.pizzaOrderPage.isCostReadOnly());

        this.pizzaOrderPage.placeOrder();

        String alertDialogText = this.pizzaOrderPage.getDialogText();
        assertTrue(alertDialogText.contains("Missing name"));
        assertTrue(alertDialogText.contains("Missing phone number"));
        assertFalse(alertDialogText.contains("Thank you for your order"));

        this.pizzaOrderPage.closeDialog();
    }

    /* Sample Data Driven Tests */

    @DataProvider(name = "NamePhoneAndEmailDataProvider")
    public Object[][] NamePhoneAndEmailDataProvider() {
        return new Object[][] {
                { "Test", "111-111-1111", "test@gmail.com", true },
                { "Test", "111-111-1111", "", true },
                { "Test", "", "test@gmail.com", false },
                { "", "111-111-111", "test@gmail.com", false },
                { "     ", "111-111-1111", "test@gmail.com", false },
                { "Test", "111-111-1111", "    ", false },
                { "Test", "111-111-1111", "invalidemail", false },
                { "Test", "invalidphone", "test@gmail.com", false },
                { "Test", "       ", "test@gmail.com", false },

                // Add other combinations based on the requirements of these fields.
        };
    }

    // Note some of the Combinations fail because of bugs.
    @Test(dataProvider = "NamePhoneAndEmailDataProvider")
    public void validate_order_status(
            String name,
            String phone,
            String email,
            boolean shoudOrderSucceed) {
        this.pizzaOrderPage.selectPizza(PizzaTypes.MEDIUM_TWOTOPPINGS.getDisplayName());
        this.pizzaOrderPage.selectPizzaTopping1(PizzaToppings.MOZZARELLA.getDisplayName());
        this.pizzaOrderPage.selectPizzaTopping2(PizzaToppings.ITALIANHAM.getDisplayName());
        this.pizzaOrderPage.setPizzaQuantity("2");
        this.pizzaOrderPage.setName(name);
        this.pizzaOrderPage.setPhone(phone);
        this.pizzaOrderPage.setEmail(email);
        this.pizzaOrderPage.chooseCreditCardPayment();

        this.pizzaOrderPage.placeOrder();

        String alertDialogText = this.pizzaOrderPage.getDialogText();
        assertEquals(shoudOrderSucceed, alertDialogText.contains("Thank you for your order"));
    }

    @DataProvider(name = "QuantityTestDataProvider")
    public Object[][] QuantityTestDataProvider() {
        return new Object[][] {
                { "-1" , false }, // Fail
                { "0" , false}, // Fail
                { "1" , true }, // Success
                { "3" , true }, // Success
                { "100" , true }, // Success
                { "1000" , true }, // Success
                { "99999", true }, // Based on Cost Input max length attribute.
                
                // UX does not allow us to set this. But putting it here.                
                // { "100000", false } 
        };
    }

    // Sample tests that hit the boundary values.
    // Some tests fail because of bugs.    
    @Test(dataProvider = "QuantityTestDataProvider")
    public void validate_order_status_by_quantity(
            String quantity,
            boolean shoudOrderSucceed) {
        this.pizzaOrderPage.selectPizza(PizzaTypes.MEDIUM_TWOTOPPINGS.getDisplayName());
        this.pizzaOrderPage.selectPizzaTopping1(PizzaToppings.MOZZARELLA.getDisplayName());
        this.pizzaOrderPage.selectPizzaTopping2(PizzaToppings.ITALIANHAM.getDisplayName());
        this.pizzaOrderPage.setPizzaQuantity(quantity);
        this.pizzaOrderPage.setName("test");
        this.pizzaOrderPage.setPhone("111-111-1111");
        this.pizzaOrderPage.setEmail("test@gmail.com");
        this.pizzaOrderPage.chooseCreditCardPayment();

        this.pizzaOrderPage.placeOrder();

        String alertDialogText = this.pizzaOrderPage.getDialogText();
        assertEquals(shoudOrderSucceed, alertDialogText.contains("Thank you for your order"));
    }
}
