package com.bruno.minhasfinancas.service.impl;

import com.bruno.minhasfinancas.exception.ErroAutenticacaoException;
import com.bruno.minhasfinancas.exception.RegraNegocioException;
import com.bruno.minhasfinancas.model.entity.Usuario;
import com.bruno.minhasfinancas.model.repository.UsuarioRepository;
import com.bruno.minhasfinancas.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
        super();
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Usuario autenticar(String email, String senha) {
        Optional<Usuario> usuario = repository.findByEmail(email);
        if (!usuario.isPresent()) {
            throw new ErroAutenticacaoException("Usuário não encontrado!");
        }
        boolean senhasBatem = passwordEncoder.matches(senha, usuario.get().getSenha());
        if (!senhasBatem) {
            throw new ErroAutenticacaoException("Senha inválida!");
        }
        return usuario.get();
    }

    @Override
    @Transactional
    public Usuario salvar(Usuario usuario) {
        validar(usuario);
        validarEmail(usuario.getEmail());
        criprografarSenha(usuario);
        try {
            return repository.save(usuario);
        } catch (Throwable throwable) {
            throw new RegraNegocioException("Erro ao salvar o usuário!");
        }

    }

    private void criprografarSenha(Usuario usuario) {
        String senhaCrypto = passwordEncoder.encode(usuario.getSenha());
        usuario.setSenha(senhaCrypto);
    }

    @Override
    public void validarEmail(String email) {
        boolean existe = repository.existsByEmail(email);
        if (existe) {
            throw new RegraNegocioException("Já existe um usuário cadastrado com este email!");
        }
    }

    @Override
    public void validar(Usuario usuario) {
        if (usuario.getNome() == null || usuario.getNome().trim().equals("")) {
            throw new RegraNegocioException("Informe o nome!");
        }
        if (usuario.getEmail() == null || usuario.getEmail().trim().equals("")) {
            throw new RegraNegocioException("Informe um email válido!");
        }
        if (usuario.getSenha().length() < 6) {
            throw new RegraNegocioException("Mínimo 6 caracteres para a Senha!");
        }
        if (usuario.getSenha() == null || usuario.getSenha().trim().equals("")) {
            throw new RegraNegocioException("Informe uma senha válida!");
        }
    }

    @Override
    public Optional<Usuario> obterPorId(Long id) {
        return repository.findById(id);
    }

}
