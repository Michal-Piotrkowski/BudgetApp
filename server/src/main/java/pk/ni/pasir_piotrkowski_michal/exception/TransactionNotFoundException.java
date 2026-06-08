package pk.ni.pasir_piotrkowski_michal.exception;

import lombok.Getter;

@Getter
public class TransactionNotFoundException extends RuntimeException {
    private final Long id;

    public TransactionNotFoundException(Long id) {
        super("Transaction NOT FOUND with id: " + id);
        this.id = id;
    }

}