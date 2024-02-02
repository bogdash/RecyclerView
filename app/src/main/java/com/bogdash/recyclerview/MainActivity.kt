package com.bogdash.recyclerview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bogdash.recyclerview.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val contactItemList: MutableList<ContactItem> = mutableListOf()
    private lateinit var adapter: ContactItemAdapter
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        populateList()
        setUpAdapter()
    }

    private fun populateList() {
        for (item in 1..30) {
            val firstName = "Ivan $item"
            val lastName = "Ivanovich $item"
            val phone = (100*item)

            val contactItem = ContactItem(id = item, firstName = firstName, lastName = lastName, phone = phone)
            contactItemList.add(contactItem)
        }
    }

    private fun setUpAdapter() {
        adapter = ContactItemAdapter(this, contactItemList)
        binding.rvContactItems.adapter = adapter
        binding.rvContactItems.layoutManager = LinearLayoutManager(this)
        binding.rvContactItems.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
    }
}