package com.bogdash.recyclerview

class ContactDataManager {
    fun generateMockContactList(): List<ContactItem> {
        val contactItemList = mutableListOf<ContactItem>()
        for (item in 1..30) {
            val firstName = "Ivan $item"
            val lastName = "Ivanovich $item"
            val phone = (100 * item).toUInt()
            val contactItem =
                ContactItem(id = item, firstName = firstName, lastName = lastName, phone = phone)
            contactItemList.add(contactItem)
        }
        return contactItemList
    }
}