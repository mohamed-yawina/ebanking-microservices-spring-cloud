
package ma.emsi.ebankservice.services;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import ma.emsi.ebankservice.clients.CustomerRestClient;
import ma.emsi.ebankservice.entities.BankAccount;
import ma.emsi.ebankservice.models.BankAccountResponse;
import ma.emsi.ebankservice.models.Customer;
import ma.emsi.ebankservice.repositories.BankAccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class BankAccountService {

    private final BankAccountRepository repository;
    private final CustomerRestClient customerRestClient;

    @CircuitBreaker(
            name = "customerService",
            fallbackMethod = "getAccountDetailsFallback"
    )
    public BankAccountResponse getAccountDetails(String id) {

        BankAccount account = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Bank account not found"
                ));

        Customer customer = customerRestClient
                .findCustomerById(account.getCustomerId());

        return buildResponse(account, customer);
    }

    public BankAccountResponse getAccountDetailsFallback(
            String id,
            Throwable exception) {

        BankAccount account = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Bank account not found"
                ));

        Customer unavailableCustomer = new Customer(
                account.getCustomerId(),
                "Customer unavailable",
                null
        );

        return buildResponse(account, unavailableCustomer);
    }

    private BankAccountResponse buildResponse(
            BankAccount account,
            Customer customer) {

        return BankAccountResponse.builder()
                .id(account.getId())
                .balance(account.getBalance())
                .createdAt(account.getCreatedAt())
                .type(account.getType())
                .customerId(account.getCustomerId())
                .customer(customer)
                .build();
    }
}
