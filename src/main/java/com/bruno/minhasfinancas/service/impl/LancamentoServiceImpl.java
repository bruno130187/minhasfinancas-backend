package com.bruno.minhasfinancas.service.impl;

import com.bruno.minhasfinancas.exception.RegraNegocioException;
import com.bruno.minhasfinancas.model.entity.Lancamento;
import com.bruno.minhasfinancas.model.enums.StatusLancamentoENUM;
import com.bruno.minhasfinancas.model.enums.TipoLancamentoENUM;
import com.bruno.minhasfinancas.model.repository.LancamentoRepository;
import com.bruno.minhasfinancas.service.LancamentoService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class LancamentoServiceImpl implements LancamentoService {

    private LancamentoRepository repository;

    public LancamentoServiceImpl(LancamentoRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public Lancamento salvar(Lancamento lancamento) {
        lancamento.setStatus(StatusLancamentoENUM.PENDENTE);
        validar(lancamento);
        return repository.save(lancamento);
    }

    @Override
    @Transactional
    public Lancamento atualizar(Lancamento lancamento) {
        Objects.requireNonNull(lancamento.getId());
        validar(lancamento);
        return repository.save(lancamento);
    }

    @Override
    @Transactional
    public void deletar(Lancamento lancamento) {
        Objects.requireNonNull(lancamento.getId());
        repository.delete(lancamento);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Lancamento> buscar(Lancamento lancamentoFiltro) {
        Example example = Example.of(lancamentoFiltro, ExampleMatcher
                .matching()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));
        validarBusca(lancamentoFiltro);
        return repository.findAll(example, Sort.by("ano").descending().and(Sort.by("mes").descending()));
    }

    @Override
    @Transactional
    public void atualizarStatus(Lancamento lancamento, StatusLancamentoENUM status) {
        repository.atualizarStatus(lancamento.getId(), status);
    }

    @Override
    public void validar(Lancamento lancamento) {
        if (lancamento.getDescricao() == null || lancamento.getDescricao().trim().equals("")) {
            throw new RegraNegocioException("Informe uma Descrição válida!");
        }
        if (lancamento.getMes() == null || lancamento.getMes() < 1 || lancamento.getMes() > 12) {
            throw new RegraNegocioException("Informe um Mês válido!");
        }
        if (lancamento.getAno() == null || lancamento.getAno().toString().length() != 4 ) {
            throw new RegraNegocioException("Informe um Ano válido!");
        }
        if (lancamento.getUsuario() == null || lancamento.getUsuario().getId() == null) {
            throw new RegraNegocioException("Informe um Usuário!");
        }
        if (lancamento.getValor() == null || lancamento.getValor().compareTo(BigDecimal.ZERO) < 1 ) {
            throw new RegraNegocioException("Informe um Valor válido!");
        }
        if (lancamento.getTipo() == null ||
                (lancamento.getTipo() != TipoLancamentoENUM.RECEITA && lancamento.getTipo() != TipoLancamentoENUM.DESPESA)) {
            throw new RegraNegocioException("Informe um tipo de lançamento válido!");
        }
        if (lancamento.getStatus() == null ||
                (lancamento.getStatus() != StatusLancamentoENUM.PENDENTE &&
                        lancamento.getStatus() != StatusLancamentoENUM.CANCELADO &&
                        lancamento.getStatus() != StatusLancamentoENUM.EFETIVADO)) {
            throw new RegraNegocioException("Informe um status de lançamento válido!");
        }
    }

    @Override
    public void validarBusca(Lancamento lancamento) {
        if (lancamento.getAno() == null || lancamento.getAno().toString().length() != 4) {
            throw new RegraNegocioException("Informe um Ano válido!");
        }
    }

    @Override
    public boolean tipoValido(String tipo) {
        if (tipo == null ||
                (!tipo.equals(TipoLancamentoENUM.RECEITA.toString()) &&
                        !tipo.equals(TipoLancamentoENUM.DESPESA.toString()))) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean statusValido(String status) {
        if (status == null ||
                (!status.equals(StatusLancamentoENUM.PENDENTE.toString()) &&
                        !status.equals(StatusLancamentoENUM.CANCELADO.toString()) &&
                        !status.equals(StatusLancamentoENUM.EFETIVADO.toString()))) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public Optional<Lancamento> obterPorId(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal obterSaldoPorUsuario(Long id) {
        BigDecimal receitas = repository.obterSaldoPorTipoLancamentoEUsuarioEStatus(id, TipoLancamentoENUM.RECEITA, StatusLancamentoENUM.EFETIVADO);
        BigDecimal despesas = repository.obterSaldoPorTipoLancamentoEUsuarioEStatus(id, TipoLancamentoENUM.DESPESA, StatusLancamentoENUM.EFETIVADO);
        if (receitas == null) {
            receitas = BigDecimal.ZERO;
        }
        if (despesas == null) {
            despesas = BigDecimal.ZERO;
        }
        return receitas.subtract(despesas);
    }

}
