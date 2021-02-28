package com.sample.test.demo.tests;

import com.sample.test.demo.TestBase;
import com.sample.test.demo.constants.PizzaToppings;
import com.sample.test.demo.constants.PizzaTypes;
import components.PizzaOrderForm;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Properties;

import static com.sample.test.demo.Configuration.readDataFile;

public class PizzaOrder extends TestBase {
    @Test
    public void demoTest() throws IOException {
        Properties propData = readDataFile("src/test/resources/files/data.txt");

        //select pizza type
        PizzaOrderForm.pizza1(PizzaTypes.SMALL_NOTOPPINGS.getDisplayName());
        //select topping1
        PizzaOrderForm.toppings1(PizzaToppings.OLIVES.getDisplayName());
        //select topping2
        PizzaOrderForm.toppings2(PizzaToppings.PARMASAN.getDisplayName());
        //enter quantity
        PizzaOrderForm.quantity(propData.getProperty("quantity"));

        //enter name
        PizzaOrderForm.name(propData.getProperty("name"));

        //enter email
        PizzaOrderForm.email(propData.getProperty("email"));

        //enter phone number
        PizzaOrderForm.phone(propData.getProperty("phone"));

        //click cash on pickup radio button
        PizzaOrderForm.cashOnPickup();

        //click place order button
        PizzaOrderForm.clickPlaceOrder();

        //Verify thank you message
        String SuccessMessage = PizzaOrderForm.verifyThankyouMessage();
        Assert.assertEquals(SuccessMessage, propData.getProperty("expectedMessage"));
    }
}
