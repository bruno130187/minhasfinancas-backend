package com.bruno.minhasfinancas.com.bruno.minhasfinancas.service;

import com.bruno.minhasfinancas.exception.ErroAutenticacaoException;
import com.bruno.minhasfinancas.exception.RegraNegocioException;
import com.bruno.minhasfinancas.model.entity.Usuario;
import com.bruno.minhasfinancas.model.repository.UsuarioRepository;

import com.bruno.minhasfinancas.service.impl.UsuarioServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UsuarioServiceTest {

    @SpyBean
    UsuarioServiceImpl service;

    @MockBean
    UsuarioRepository repository;

    @Test()
    @Order(1)
    public void deveValidarEmailNaoRetornandoErro() {
        Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);
        Assertions.assertDoesNotThrow(
                () -> service.validarEmail("usuario@email.com")
        );
    }

    @Test()
    @Order(2)
    public void deveValidarEmailRetornandoErro() {
        UsuarioRepository usuarioRepository = Mockito.mock(UsuarioRepository.class);
        Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);
        Throwable thrown = Assertions.assertThrows(
                RegraNegocioException.class,
                () -> service.validarEmail("usuario@email.com")
        );
        Assertions.assertTrue(thrown.getMessage().contentEquals("Já existe um usuário cadastrado com este email!"));
    }

    @Test
    @Order(3)
    public void deveAutenticarUmUsuarioComSucesso() {
        String email = "email.email.com";
        String senha = "senha";
        Usuario usuario = Usuario.builder().id(1L).email(email).senha(senha).build();
        Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));
        Usuario result = service.autenticar(email, senha);
        org.assertj.core.api.Assertions.assertThat(result).isNotNull();
    }

    @Test
    @Order(4)
    public void deveAutenticarLancarErroQUandoNaoEncontrarUsuarioCadastradoComEmailInformado() {
        Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());
        Throwable thrown = Assertions.assertThrows(
                ErroAutenticacaoException.class,
                () -> service.autenticar("usuario@email.com", "senha")
        );
        Assertions.assertTrue(thrown.getMessage().contentEquals("Usuário não encontrado!"));
    }

    @Test
    @Order(5)
    public void deveAutenticarLancarErroQUandoSenhaNaoForCorreta() {
        String email = "email.email.com";
        String senha = "senha";
        Usuario usuario = Usuario.builder().email(email).senha(senha).build();
        Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));
        Throwable thrown = Assertions.assertThrows(
                ErroAutenticacaoException.class,
                () -> service.autenticar("usuario@email.com", "senhaErrada")
        );
        Assertions.assertTrue(thrown.getMessage().contentEquals("Senha inválida!"));
    }

    @Test
    @Order(6)
    public void deveSalvarUmUsuarioSemErros() {
        Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
        Usuario usuario = Usuario.builder()
                .id(1L)
                .nome("usuario")
                .email("email@email.com")
                .senha("senha")
                .build();
        Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);
        Assertions.assertDoesNotThrow(
                () -> service.salvar(new Usuario())
        );
    }

    @Test
    @Order(6)
    public void naoDeveSalvarUmUsuarioComEmailJaCadastrado() {
        String email = "email@email.com";
        Usuario usuario = Usuario.builder()
                .email(email)
                .build();
        Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail(email);
        service.salvar(usuario);
        Mockito.verify(repository, Mockito.never()).save(usuario);
    }

}
