package pk.ni.pasir_piotrkowski_michal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import pk.ni.pasir_piotrkowski_michal.model.Debt;

public interface DebtRepository extends JpaRepository<Debt, Long> {
    List<Debt> findByGroupId(Long groupId);
    void deleteByGroupId(Long groupId);
}
