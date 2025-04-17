package model

data class Usuario(
    val id: String,
    val nome: String,
    val telefone: String,
    val email: String,
    val senha: String,
    val preferencia: String,
    val experiencia: String
)