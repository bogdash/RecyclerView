package com.bogdash.recyclerview

class ContactDataManager {
    fun generateMockContactList(): List<ContactItem> {
        val contactItemList = mutableListOf<ContactItem>()
        for (item in 1..100) {
            val firstName = "Ivan $item"
            val lastName = "Ivanovich $item"
            val phone = generateRandomPhoneNumber()
            val contactItem =
                ContactItem(id = item, firstName = firstName, lastName = lastName, phone = phone)
            contactItemList.add(contactItem)
        }
        return contactItemList
    }

    private fun generateRandomPhoneNumber(): String {
        val random = (100000000L..999999999L).random()
        return String.format(
            "8 %03d%03d%02d%02d",
            random / 1_000_000_00,
            (random / 1_000_000) % 1000,
            (random / 1_000) % 100,
            random % 100
        )
    }
}