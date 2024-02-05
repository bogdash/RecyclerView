package com.bogdash.recyclerview

data class ContactItem(
    val id: Int,
    var firstName: String,
    var lastName: String,
    var phone: UInt,
    var isChecked: Boolean = false,
    var wasUserSelected: Boolean = false
)
