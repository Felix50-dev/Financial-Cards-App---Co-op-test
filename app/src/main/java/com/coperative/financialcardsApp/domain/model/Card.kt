package com.coperative.financialcardsApp.domain.model

sealed class Card(
    open val id: String,
    open val number: String,
    open val holderName: String,
    open val isBlocked: Boolean
) {
    abstract fun getDisplayBalance(): String
    fun maskedNumber(): String = "**** ${number.takeLast(4)}"

    data class Prepaid(
        override val id: String,
        override val number: String,
        override val holderName: String,
        override val isBlocked: Boolean,
        val loadBalance: Double
    ) : Card(id, number, holderName, isBlocked) {
        override fun getDisplayBalance() = "KES ${loadBalance.formatAsCurrency()}"
    }

    data class Credit(
        override val id: String,
        override val number: String,
        override val holderName: String,
        override val isBlocked: Boolean,
        val creditLimit: Double,
        val dueDate: String
    ) : Card(id, number, holderName, isBlocked) {
        override fun getDisplayBalance() = "Limit: KES ${creditLimit.formatAsCurrency()}"
    }

    data class MultiCurrency(
        override val id: String,
        override val number: String,
        override val holderName: String,
        override val isBlocked: Boolean,
        val balances: Map<String, Double>
    ) : Card(id, number, holderName, isBlocked) {
        override fun getDisplayBalance() =
            balances.entries.joinToString(separator = " • ") { "${it.key}: ${it.value.formatAsCurrency()}" }
    }

    data class Debit(
        override val id: String,
        override val number: String,
        override val holderName: String,
        override val isBlocked: Boolean,
        val linkedAccountName: String,
        val balance: Double // In KES
    ) : Card(id, number, holderName, isBlocked) {
        override fun getDisplayBalance() = "KES ${balance.formatAsCurrency()}"
    }
}

fun Double.formatAsCurrency(): String = String.format("%,.2f", this)

data class Transaction(
    val id: String,
    val amount: Double,
    val date: String,
    val description: String,
    val currency: String? = "KES"
)

data class User(
    val name: String,
    val avatarUrl: String?,
    val email: String,
    val phone: String?,
    val address: String?
)
