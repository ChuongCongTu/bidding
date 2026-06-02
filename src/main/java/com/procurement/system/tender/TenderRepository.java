package com.procurement.system.tender;

import com.procurement.system.plan.Plan;
import com.procurement.system.tender.enums.TenderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TenderRepository extends JpaRepository<Tender, UUID> {
    List<Tender> findAllByPlan(Plan plan);

    @Query("SELECT t FROM Tender t WHERE t.id = :id AND t.plan.investor.id = :investorId")
    Optional<Tender> findByIdAndInvestorId(@Param("id") UUID id, @Param("investorId") UUID investorId);

    List<Tender> findAllByStatusIn(List<TenderStatus> statuses);

    @Query("SELECT count(*) FROM Tender t WHERE t.plan.fiscalYear = :fiscalYear")
    Long countByFiscalYear(@Param("fiscalYear") Integer fiscalYear);

    Plan plan(Plan plan);
}
