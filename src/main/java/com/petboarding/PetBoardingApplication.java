package com.petboarding;

import com.stripe.Stripe;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class PetBoardingApplication {

	@Value("${stripe.api.key}")
	private String stripeApiKey;

	@PostConstruct
	public void setup() {
		Stripe.apiKey = stripeApiKey;
	}

	public static void main(String[] args) {
		SpringApplication.run(PetBoardingApplication.class, args);
	}

}
