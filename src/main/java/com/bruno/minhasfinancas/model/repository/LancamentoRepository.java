package com.bruno.minhasfinancas.model.repository;

import com.bruno.minhasfinancas.model.entity.Lancamento;
import com.bruno.minhasfinancas.model.enums.StatusLancamentoENUM;
import com.bruno.minhasfinancas.model.enums.TipoLancamentoENUM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

    @Query( value =
            " SELECT SUM(l.valor) FROM Lancamento l JOIN l.usuario u "
                    + " WHERE u.id = :idUsuario AND l.tipo =:tipo AND l.status = :status GROUP BY u " )
    BigDecimal obterSaldoPorTipoLancamentoEUsuarioEStatus(
            @Param("idUsuario") Long idUsuario,
            @Param("tipo") TipoLancamentoENUM tipo,
            @Param("status") StatusLancamentoENUM status);

    @Modifying
    @Query(value = "UPDATE Lancamento l SET l.status = :status WHERE l.id = :idLancamento ")
    void atualizarStatus(@Param("idLancamento") Long idLancamento,
                               @Param("status") StatusLancamentoENUM status);

}
