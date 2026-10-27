
package ma.emsi.ebankservice.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.emsi.ebankservice.enums.AccountType;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankAccountResponse {

    private String id;
    private double balance;
    private LocalDate createdAt;
    private AccountType type;
    private Long customerId;
    private Customer customer;
}
