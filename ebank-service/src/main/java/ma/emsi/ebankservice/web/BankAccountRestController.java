
package ma.emsi.ebankservice.web;

import lombok.RequiredArgsConstructor;
import ma.emsi.ebankservice.clients.CustomerRestClient;
import ma.emsi.ebankservice.entities.BankAccount;
import ma.emsi.ebankservice.models.BankAccountResponse;
import ma.emsi.ebankservice.models.Customer;
import ma.emsi.ebankservice.repositories.BankAccountRepository;
import ma.emsi.ebankservice.services.BankAccountService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class BankAccountRestController {

    private final BankAccountRepository repository;
    private final BankAccountService bankAccountService;
    // 1. Récupérer tous les comptes
    @GetMapping
    public List<BankAccount> getAllAccounts() {
        return repository.findAll();
    }

    // 2. Récupérer un compte par son ID
    @GetMapping("/{id}")
    public BankAccount getAccountById(@PathVariable String id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Bank account not found"
                        )
                );
    }

    // 3. Récupérer les comptes d'un client
    @GetMapping("/customer/{customerId}")
    public List<BankAccount> getAccountsByCustomer(
            @PathVariable Long customerId) {

        return repository.findByCustomerId(customerId);
    }

    // 4. Créer un compte bancaire
    @PostMapping
    public ResponseEntity<BankAccount> createAccount(
            @RequestBody BankAccount account) {

        account.setId(UUID.randomUUID().toString());
        account.setCreatedAt(LocalDate.now());

        BankAccount savedAccount = repository.save(account);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedAccount);
    }

    // 5. Modifier un compte bancaire
    @PutMapping("/{id}")
    public BankAccount updateAccount(
            @PathVariable String id,
            @RequestBody BankAccount account) {

        BankAccount existingAccount = getAccountById(id);

        existingAccount.setBalance(account.getBalance());
        existingAccount.setType(account.getType());
        existingAccount.setCustomerId(account.getCustomerId());

        return repository.save(existingAccount);
    }

    // 6. Supprimer un compte bancaire
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(
            @PathVariable String id) {

        if (!repository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Bank account not found"
            );
        }

        repository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    // 7. Récupérer un compte avec les informations du client
    // Communication entre EBank Service et Customer Service via OpenFeign

    @GetMapping("/{id}/details")
    public BankAccountResponse getAccountDetails(
            @PathVariable String id) {

        return bankAccountService.getAccountDetails(id);
    }

}
