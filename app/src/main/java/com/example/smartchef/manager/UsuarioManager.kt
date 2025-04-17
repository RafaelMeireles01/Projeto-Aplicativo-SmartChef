package manager

import model.Usuario

object UsuarioManager {
    private val usuarios = mutableListOf<Usuario>()
    private var currentUser: Usuario? = null

    fun cadastrarUsuario(usuario: Usuario): Boolean {
        if (usuarios.any { it.email == usuario.email }) {
            return false // Email já cadastrado
        }
        usuarios.add(usuario)
        return true
    }

    fun autenticar(email: String, senha: String): Usuario? {
        return usuarios.find { it.email == email && it.senha == senha }?.also {
            currentUser = it
        }
    }

    fun getCurrentUser(): Usuario? = currentUser

    fun logout() {
        currentUser = null
    }

    // Para persistência simples (opcional)
    fun getUsuarios(): List<Usuario> = usuarios.toList()
    fun setUsuarios(list: List<Usuario>) {
        usuarios.clear()
        usuarios.addAll(list)
    }
}