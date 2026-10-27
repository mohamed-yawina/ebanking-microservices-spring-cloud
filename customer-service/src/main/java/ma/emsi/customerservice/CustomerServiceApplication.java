
package ma.emsi.customerservice;

import ma.emsi.customerservice.entities.Customer;
import ma.emsi.customerservice.repositories.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CustomerServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(
				CustomerServiceApplication.class,
				args
		);
	}

	@Bean
	CommandLineRunner start(CustomerRepository repository) {
		return args -> {

			repository.save(Customer.builder()
					.name("Mohamed")
					.email("mohamed@gmail.com")
					.build());

			repository.save(Customer.builder()
					.name("Ahmed")
					.email("ahmed@gmail.com")
					.build());

			repository.save(Customer.builder()
					.name("Sara")
					.email("sara@gmail.com")
					.build());

			repository.findAll().forEach(System.out::println);
		};
	}
}
