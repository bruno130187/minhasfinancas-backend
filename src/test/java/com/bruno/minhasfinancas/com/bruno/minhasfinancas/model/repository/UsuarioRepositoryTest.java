package com.bruno.minhasfinancas.com.bruno.minhasfinancas.model.repository;

import com.bruno.minhasfinancas.model.entity.Usuario;
import com.bruno.minhasfinancas.model.repository.UsuarioRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;


@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UsuarioRepositoryTest {

    @Autowired
    UsuarioRepository repository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    @Order(1)
    public void deveVerificarAExistenciaDeUmEmail() {
        Usuario usuario = criarUsuario();
        entityManager.persist(usuario);
        boolean result = repository.existsByEmail("usuario@email.com");
        Assertions.assertThat(result).isTrue();
    }

    @Test
    @Order(2)
    public void deveRetornarFalsoQuandoNaoHouverUsuarioCadastradoComOEmail() {
        boolean result = repository.existsByEmail("usuario@email.com");
        Assertions.assertThat(result).isFalse();
    }

    @Test
    @Order(3)
    public void devePersistirUmUsuarioNaBaseDeDados() {
        Usuario usuario = criarUsuario();
        Usuario usuarioSalvo = entityManager.persist(usuario);
        Assertions.assertThat(usuarioSalvo.getId()).isNotNull();
    }

    @Test
    @Order(4)
    public void deveBuscarUmUsuarioPorEmail() {
        Usuario usuario = criarUsuario();
        entityManager.persist(usuario);
        Optional<Usuario> result = repository.findByEmail("usuario@email.com");
        Assertions.assertThat(result.isPresent()).isTrue();
    }

    @Test
    @Order(5)
    public void deveRetornarVazioAoBuscarUmUsuarioPorEmail() {
        Optional<Usuario> result = repository.findByEmail("usuario@email.com");
        Assertions.assertThat(result.isPresent()).isFalse();
    }

    public static Usuario criarUsuario() {
        return Usuario.builder()
                .nome("usuario")
                .email("usuario@email.com")
                .senha("senha").build();
    }

}
