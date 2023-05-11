package com.bruno.minhasfinancas.service;

import com.bruno.minhasfinancas.model.entity.Usuario;

import java.util.Optional;

public interface UsuarioService {

    Usuario autenticar(String email, String senha);

    Usuario salvar(Usuario usuario);

    void validarEmail(String email);

    void validar(Usuario usuario);

    Optional<Usuario> obterPorId(Long id);

}
