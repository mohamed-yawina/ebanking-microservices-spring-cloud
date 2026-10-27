
package ma.emsi.ebankservice.entities;

import jakarta.persistence.*;
import lombok.*;
import ma.emsi.ebankservice.enums.AccountType;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankAccount {

    @Id
    private String id;

    private double balance;

    private LocalDate createdAt;

    @Enumerated(EnumType.STRING)
    private AccountType type;

    private Long customerId;
}
