package com.coperative.financialcardsApp.data.remote.dto

import com.coperative.financialcardsApp.domain.model.User
import org.json.JSONObject

data class UserDto(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val avatarUrl: String,
    val address: AddressDto
){
    companion object {
    }
}

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
        address = "${address.street}, ${address.city}, ${address.country}",
        city = address.city,
        country = address.country,
        postalCode = address.postalCode
    )
}


fun UserDto.Companion.fromJson(json: JSONObject): UserDto {
    val addrJson = json.getJSONObject("address")
    return UserDto(
        id = json.getString("id"),
        firstName = json.getString("firstName"),
        lastName = json.getString("lastName"),
        email = json.getString("email"),
        phone = json.getString("phone"),
        avatarUrl = json.getString("avatarUrl"),
        address = AddressDto(
            street = addrJson.getString("street"),
            city = addrJson.getString("city"),
            country = addrJson.getString("country"),
            postalCode = addrJson.getString("postalCode")
        )
    )
}
