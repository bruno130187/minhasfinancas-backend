package com.bruno.minhasfinancas.service;

import com.bruno.minhasfinancas.model.entity.Lancamento;
import com.bruno.minhasfinancas.model.enums.StatusLancamentoENUM;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface LancamentoService {

    Lancamento salvar(Lancamento lancamento);

    Lancamento atualizar(Lancamento lancamento);

    void deletar(Lancamento lancamento);

    List<Lancamento> buscar(Lancamento lancamentoFiltro);

    void atualizarStatus(Lancamento lancamento, StatusLancamentoENUM status);

    void validar(Lancamento lancamento);

    void validarBusca(Lancamento lancamento);

    boolean tipoValido(String tipo);

    boolean statusValido(String status);

    Optional<Lancamento> obterPorId(Long id);

    BigDecimal obterSaldoPorUsuario(Long id);

}
