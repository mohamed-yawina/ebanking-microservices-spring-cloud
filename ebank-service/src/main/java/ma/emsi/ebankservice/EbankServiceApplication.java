
package ma.emsi.ebankservice;

import ma.emsi.ebankservice.entities.BankAccount;
import ma.emsi.ebankservice.enums.AccountType;
import ma.emsi.ebankservice.repositories.BankAccountRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.util.Random;
import java.util.UUID;

@SpringBootApplication
@EnableFeignClients
public class EbankServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(
				EbankServiceApplication.class,
				args
		);
	}

	@Bean
	CommandLineRunner start(BankAccountRepository repository) {
		return args -> {

			Random random = new Random();

			for (int i = 0; i < 15; i++) {

				BankAccount account = BankAccount.builder()
						.id(UUID.randomUUID().toString())
						.balance(1000 + random.nextDouble() * 90000)
						.createdAt(LocalDate.now())
						.type(i % 2 == 0
								? AccountType.CURRENT_ACCOUNT
								: AccountType.SAVING_ACCOUNT)
						.customerId((long) (i % 3 + 1))
						.build();

				repository.save(account);
			}

			repository.findAll()
					.forEach(System.out::println);
		};
	}
}
