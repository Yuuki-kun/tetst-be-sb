package com.amigos.authenautho.demo;

import com.amigos.authenautho.demo.services.CheckoutService;
import com.amigos.authenautho.demo.services.TrackingOrderServices;
import com.stripe.Stripe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@SpringBootApplication
@RequestMapping
public class BookShopV1Demo {

	public static void main(String[] args) {
		SpringApplication.run(BookShopV1Demo.class, args);
	}

	@Autowired
	private CheckoutService checkoutService;
	@Autowired
	private  TrackingOrderServices getAllOrdersByEmail;
	@Bean
	public CommandLineRunner commandLineRunner(){
		return args -> {
//			startStripeListen();
//			getAllOrdersByEmail.getAllOrdersByEmail("tcm@gmail.com");
//			Stripe.apiKey = "sk_test_51O25J9CB4m1aFTFZ8kZt5EoYrTpwl0LeApVLSDqSsA7GWihlJ0xxxI03XfMlwpYtoNyNI6JWZV30fseZ56A33fON0005Rab2Q3";

//			checkoutService.addProductTest();
		};
	}
	public void startStripeListen(){
		Stripe.apiKey = "sk_test_51O25J9CB4m1aFTFZ8kZt5EoYrTpwl0LeApVLSDqSsA7GWihlJ0xxxI03XfMlwpYtoNyNI6JWZV30fseZ56A33fON0005Rab2Q3";

		try {
			String command = "stripe listen --forward-to localhost:8080/stripe/api/webhook";
			Process process = Runtime.getRuntime().exec(command);
			System.out.println("Stripe listening success to start.".toUpperCase());
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

			String line;
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}

			int exitCode = process.waitFor();
			System.out.println("Stripe Listen exited with code: " + exitCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
