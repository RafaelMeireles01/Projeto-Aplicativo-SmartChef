package model

import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

data class Ingredientes(
    val id: String,
    val nome: String,
    val quantidade: BigDecimal,
    val dataValidade: LocalDate
)