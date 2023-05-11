package com.bruno.minhasfinancas.api.resource;

import com.bruno.minhasfinancas.api.dto.AtualizaStatusDTO;
import com.bruno.minhasfinancas.api.dto.LancamentoDTO;
import com.bruno.minhasfinancas.exception.RegraNegocioException;
import com.bruno.minhasfinancas.model.entity.Lancamento;
import com.bruno.minhasfinancas.model.entity.Usuario;
import com.bruno.minhasfinancas.model.enums.StatusLancamentoENUM;
import com.bruno.minhasfinancas.model.enums.TipoLancamentoENUM;
import com.bruno.minhasfinancas.service.LancamentoService;
import com.bruno.minhasfinancas.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/lancamentos")
@RequiredArgsConstructor
public class LancamentoResource {

    private final LancamentoService service;
    private final UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity buscar(
            @RequestParam(value = "descricao", required = false) String descricao,
            @RequestParam(value = "mes", required = false) Integer mes,
            @RequestParam(value = "ano", required = false) Integer ano,
            @RequestParam(value = "tipo", required = false) String tipo,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam("usuario") Long idUsuario
    ) {
        Lancamento lancamentoFiltro = new Lancamento();
        lancamentoFiltro.setDescricao(descricao);
        lancamentoFiltro.setMes(mes);
        lancamentoFiltro.setAno(ano);
        if (tipo != null) {
            if (service.tipoValido(tipo)) {
                lancamentoFiltro.setTipo(TipoLancamentoENUM.valueOf(tipo));
            } else {
                return ResponseEntity.badRequest().body("Informe um tipo de lançamento válido!");
            }
        }
        if (status != null) {
            if (service.statusValido(status)) {
                lancamentoFiltro.setStatus(StatusLancamentoENUM.valueOf(status));
            } else {
                return ResponseEntity.badRequest().body("Informe um status de lançamento válido!");
            }
        }

        Optional<Usuario> usuario = usuarioService.obterPorId(idUsuario);
        if (!usuario.isPresent()) {
            return ResponseEntity.badRequest().body("Não foi possível realizar a consulta. Usuário não encontrado para o Id informado!");
        } else {
            lancamentoFiltro.setUsuario(usuario.get());
        }

        List<Lancamento> lancamentos = service.buscar(lancamentoFiltro);

        if (lancamentos.isEmpty()) {
            return ResponseEntity.ok("Nada encontrado para a consulta!");
        } else {
            return ResponseEntity.ok(lancamentos);
        }

    }

    @GetMapping("{id}")
    public ResponseEntity obterLancamento(@PathVariable("id") Long id) {
        return service.obterPorId(id)
                .map(lancamento -> new ResponseEntity(converter(lancamento), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity salvar(@RequestBody LancamentoDTO dto) {
        try {
            Lancamento entidade = converter(dto);
            entidade = service.salvar(entidade);
            return new ResponseEntity(entidade, HttpStatus.CREATED);
        } catch (RegraNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{id}")
    public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody LancamentoDTO dto) {
        return service.obterPorId(id).map(entity -> {
            try {
                Lancamento lancamento = converter(dto);
                lancamento.setId(entity.getId());
                service.atualizar(lancamento);
                return ResponseEntity.ok(lancamento);
            } catch (RegraNegocioException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }).orElseGet(() ->
                new ResponseEntity("Lancamento não encontrado na base de Dados!", HttpStatus.BAD_REQUEST));
    }

    @PutMapping("{id}/atualiza-status")
    public ResponseEntity atualizarStatus(@PathVariable("id") Long id, @RequestBody AtualizaStatusDTO dto) {
        Optional<Lancamento> entity = service.obterPorId(id);
        if (entity.isPresent()) {
            if (service.statusValido(dto.getStatus())) {
                StatusLancamentoENUM statusSelecionado = StatusLancamentoENUM.valueOf(dto.getStatus());
                try {
                    service.atualizarStatus(entity.get(), statusSelecionado);
                    entity.get().setStatus(statusSelecionado);
                    return ResponseEntity.ok(entity);
                } catch (RegraNegocioException e) {
                    return ResponseEntity.badRequest().body(e.getMessage());
                }
            } else {
                return ResponseEntity.badRequest().body("O Status enviado é inválido!");
            }
        } else {
            return new ResponseEntity("Lancamento não encontrado na base de Dados!", HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity deletar(@PathVariable("id") Long id) {
        return service.obterPorId(id).map(entidade -> {
            service.deletar(entidade);
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }).orElseGet(() ->
                new ResponseEntity("Lancamento não encontrado na base de Dados!", HttpStatus.BAD_REQUEST));
    }

    private LancamentoDTO converter(Lancamento lancamento) {
        return LancamentoDTO.builder()
                .id(lancamento.getId())
                .descricao(lancamento.getDescricao())
                .valor(lancamento.getValor())
                .dataCadastro(lancamento.getDataCadastro())
                .mes(lancamento.getMes())
                .ano(lancamento.getAno())
                .status(lancamento.getStatus().name())
                .tipo(lancamento.getTipo().name())
                .usuario(lancamento.getUsuario().getId())
                .build();

    }

    private Lancamento converter(LancamentoDTO dto) {
        Lancamento lancamento = new Lancamento();
        lancamento.setId(dto.getId());
        lancamento.setDescricao(dto.getDescricao());
        lancamento.setAno(dto.getAno());
        lancamento.setMes(dto.getMes());
        lancamento.setValor(dto.getValor());
        lancamento.setDataCadastro(LocalDate.now());

        Usuario usuario = usuarioService
                .obterPorId(dto.getUsuario())
                .orElseThrow(() -> new RegraNegocioException("Usuário não encontrado para o Id informado!"));

        lancamento.setUsuario(usuario);

        if (dto.getTipo() != null) {
            lancamento.setTipo(TipoLancamentoENUM.valueOf(dto.getTipo()));
        }

        if (dto.getStatus() != null) {
            lancamento.setStatus(StatusLancamentoENUM.valueOf(dto.getStatus()));
        }

        return lancamento;
    }

}
