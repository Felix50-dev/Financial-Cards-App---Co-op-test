package com.coperative.financialcardsApp.domain.model

sealed class Card(
    open val id: String,
    open val number: String,
    open val holderName: String,
    open val isBlocked: Boolean,
    open val name: String,
    open val expiryDate: String?,
    open val currency: String?
) {
    abstract fun getDisplayBalance(): String
    fun maskedNumber(): String = "**** ${number.takeLast(4)}"

    data class Prepaid(
        override val id: String,
        override val number: String,
        override val holderName: String,
        override val isBlocked: Boolean,
        override val name: String,
        override val expiryDate: String?,
        override val currency: String?,
        val loadBalance: Double
    ) : Card(id, number, holderName, isBlocked, name, expiryDate, currency) {
        override fun getDisplayBalance() = "$currency ${loadBalance.formatAsCurrency()}"
    }

    data class Credit(
        override val id: String,
        override val number: String,
        override val holderName: String,
        override val isBlocked: Boolean,
        override val name: String,
        override val expiryDate: String?,
        override val currency: String?,
        val creditLimit: Double,
        val dueDate: String
    ) : Card(id, number, holderName, isBlocked, name, expiryDate, currency) {
        override fun getDisplayBalance() = "Limit: $currency ${creditLimit.formatAsCurrency()}"
    }

    data class MultiCurrency(
        override val id: String,
        override val number: String,
        override val holderName: String,
        override val isBlocked: Boolean,
        override val name: String,
        override val expiryDate: String?,
        override val currency: String?,
        val balances: Map<String, Double>
    ) : Card(id, number, holderName, isBlocked, name, expiryDate, currency) {
        override fun getDisplayBalance() =
            balances.entries.joinToString(" • ") { "${it.key}: ${it.value.formatAsCurrency()}" }
    }

    data class Debit(
        override val id: String,
        override val number: String,
        override val holderName: String,
        override val isBlocked: Boolean,
        override val name: String,
        override val expiryDate: String?,
        override val currency: String?,
        val linkedAccountName: String,
        val balance: Double
    ) : Card(id, number, holderName, isBlocked, name, expiryDate, currency) {
        override fun getDisplayBalance() = "$currency ${balance.formatAsCurrency()}"
    }
}
fun Double.formatAsCurrency(): String = String.format("%,.2f", this)
