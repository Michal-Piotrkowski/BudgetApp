package pk.ni.pasir_piotrkowski_michal.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pk.ni.pasir_piotrkowski_michal.dto.BalanceDto;
import pk.ni.pasir_piotrkowski_michal.dto.TransactionDTO;
import pk.ni.pasir_piotrkowski_michal.exception.TransactionNotFoundException;
import pk.ni.pasir_piotrkowski_michal.model.Transaction;
import pk.ni.pasir_piotrkowski_michal.model.TransactionType;
import pk.ni.pasir_piotrkowski_michal.model.User;
import pk.ni.pasir_piotrkowski_michal.repository.TransactionRepository;
import pk.ni.pasir_piotrkowski_michal.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    public BalanceDto getUserBalance(User user, Integer days)  {
        List<Transaction> userTransactions;
        if (days != null) {
            LocalDateTime startDate = LocalDateTime.now().minusDays(days);
            userTransactions = transactionRepository.findAllByUserAndTimestampGreaterThanEqual(user, startDate);
        } else {
            userTransactions = transactionRepository.findAllByUser(user);
        }

        double income = userTransactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .mapToDouble(Transaction::getAmount)
                .sum();

        double expense = userTransactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .mapToDouble(Transaction::getAmount)
                .sum();

        return new BalanceDto(income, expense, income - expense);
    }

    public List<Transaction> getAllTransactions() {
        User user = getCurrentUser();
        return transactionRepository.findAllByUser(user);
    }

    public User getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new AccessDeniedException("Użytkownik nie jest uwierzytelniony");
        }

        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono zalogowanego użytkownika: " + email));
    }

    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException(id));
    }

    public Transaction createTransaction(TransactionDTO dto) {
        User user = getCurrentUser();
        return createTransactionForUser(dto, user);
    }

    public Transaction createTransactionForUser(TransactionDTO dto, User user){
        Transaction transaction = toEntity(dto);
        transaction.setUser(user);
        if (transaction.getTimestamp() == null) {
            transaction.setTimestamp(LocalDateTime.now());
        }
        return transactionRepository.save(transaction);
    }

    public Transaction updateTransaction(Long id, TransactionDTO dto) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException(id));

        if(!transaction.getUser().getEmail().equals(getCurrentUser().getEmail())){
            throw new AccessDeniedException("Nie masz dotepu do tej transakcji");
        }

        applyDto(transaction, dto);
        return transactionRepository.save(transaction);
    }

    public void deleteTransaction(Long id) {
        if (!transactionRepository.existsById(id)) {
            throw new TransactionNotFoundException(id);
        }
        transactionRepository.deleteById(id);
    }

    private Transaction toEntity(TransactionDTO dto) {
        Transaction t = new Transaction();
        applyDto(t, dto);
        return t;
    }

    private void applyDto(Transaction target, TransactionDTO dto) {
        target.setAmount(dto.getAmount());
        target.setType(dto.getType());
        target.setTags(dto.getTags());
        target.setNotes(dto.getNotes());
        target.setTimestamp(dto.getTimestamp());
    }
}
