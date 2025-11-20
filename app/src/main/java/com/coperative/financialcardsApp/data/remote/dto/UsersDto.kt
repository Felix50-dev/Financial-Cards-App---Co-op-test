package com.coperative.financialcardsApp.data.remote.dto

import com.coperative.financialcardsApp.domain.model.User
import com.squareup.moshi.Json

data class UserResponseDto(
    @Json(name = "user") val user: UserDto
)

data class UserDto(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val avatarUrl: String,
    val address: AddressDto
)

data class AddressDto(
    val street: String,
    val city: String,
    val country: String,
    val postalCode: String
)

fun UserDto.toDomain(): User {
    return User(
        name = "$firstName $lastName",
        avatarUrl = avatarUrl,
        email = email,
        phone = phone,
        address = "${address.street}, ${address.city}, ${address.country}"
    )
}

