package com.bogdash.recyclerview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
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

        binding.rvContactItems.addOnScrollListener(object : OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    binding.fabAddContactItem.hide()
                } else {
                    binding.fabAddContactItem.show()
                }
            }
        })

        binding.fabAddContactItem.setOnClickListener {
            showAddDialog()
        }

        binding.fabCheck.setOnClickListener {
            deleteCheckedItems()
        }

        binding.fabCancel.setOnClickListener {
            resetSelections()
        }
    }

    private fun resetSelections() {
        for (contactItem in contactItemList) {
            contactItem.wasUserSelected = false
        }
        adapter.notifyDataSetChanged()
    }

    private fun clearChecks() {
        val newList = contactItemList.map { it.copy(isChecked = false) }
        val diffCallback = DiffUtilCallback(contactItemList, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        contactItemList.forEach { it.isChecked = false }
        adapter.submitList(newList)
        diffResult.dispatchUpdatesTo(adapter)
    }

    // menu
    private var isDeleteModeActive = false
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete -> {
                isDeleteModeActive = !isDeleteModeActive
                if (isDeleteModeActive) {
                    toggleFABVisibility(isDeleteModeActive)

                    val newList = contactItemList.map { it.copy(isChecked = true) }
                    val diffCallback = DiffUtilCallback(contactItemList, newList)
                    val diffResult = DiffUtil.calculateDiff(diffCallback)

                    contactItemList.forEach { it.isChecked = true }
                    adapter.submitList(newList)
                    diffResult.dispatchUpdatesTo(adapter)
                } else {
                    toggleFABVisibility(isDeleteModeActive)
                    clearChecks()
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun toggleFABVisibility(visible: Boolean) {
        binding.fabAddContactItem.visibility = if (visible) View.GONE else View.VISIBLE
        binding.fabCheck.visibility = if (visible) View.VISIBLE else View.GONE
        binding.fabCancel.visibility = if (visible) View.VISIBLE else View.GONE
    }

    // dialog
    private fun showAddDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialog, null)
        val editTextFirstName = dialogLayout.findViewById<EditText>(R.id.tied_firstname)
        val editTextLastName = dialogLayout.findViewById<EditText>(R.id.tied_lastname)
        val editTextPhone = dialogLayout.findViewById<EditText>(R.id.ed_phone)

        var firstName: String
        var lastName: String
        var phone: String

        with(builder) {
            setPositiveButton("OK") { _, _ ->
                firstName = editTextFirstName.text.toString()
                lastName = editTextLastName.text.toString()
                phone = editTextPhone.text.toString()

                val newId = contactItemList.size + 1
                val newItem = ContactItem(newId, firstName, lastName, phone.toUInt())

                val newList = mutableListOf<ContactItem>().apply {
                    addAll(contactItemList)
                    add(newItem)
                }

                val diffCallback = DiffUtilCallback(contactItemList, newList)
                val diffResult = DiffUtil.calculateDiff(diffCallback)
                contactItemList.add(newItem)
                adapter.submitList(newList)
                diffResult.dispatchUpdatesTo(adapter)
            }
            setNegativeButton("Cancel") { _, _ -> }
            setView(dialogLayout)
            show()
        }
    }

    fun showEditDialog(position: Int) {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialog, null)
        val editTextFirstName = dialogLayout.findViewById<EditText>(R.id.tied_firstname)
        val editTextLastName = dialogLayout.findViewById<EditText>(R.id.tied_lastname)
        val editTextPhone = dialogLayout.findViewById<EditText>(R.id.ed_phone)

        val originalItem = contactItemList[position].copy()

        editTextFirstName.setText(originalItem.firstName)
        editTextLastName.setText(originalItem.lastName)
        editTextPhone.setText(originalItem.phone.toString())

        with(builder) {
            setPositiveButton("Ok") { _, _ ->
                val firstName = editTextFirstName.text.toString()
                val lastName = editTextLastName.text.toString()
                val phone = editTextPhone.text.toString()

                if (originalItem.firstName != firstName || originalItem.lastName != lastName || originalItem.phone.toString() != phone) {
                    contactItemList[position].firstName = firstName
                    contactItemList[position].lastName = lastName
                    contactItemList[position].phone = phone.toUInt()

                    val newList = contactItemList.toList()
                    val diffCallback = DiffUtilCallback(contactItemList, newList)
                    val diffResult = DiffUtil.calculateDiff(diffCallback)
                    adapter.submitList(newList)
                    diffResult.dispatchUpdatesTo(adapter)
                }
            }
            setNegativeButton("Cancel") { _, _ -> }
            setView(dialogLayout)
            show()
        }
    }

    private fun populateList() {
        for (item in 1..30) {
            val firstName = "Ivan $item"
            val lastName = "Ivanovich $item"
            val phone = (100 * item).toUInt()
            val contactItem =
                ContactItem(id = item, firstName = firstName, lastName = lastName, phone = phone)
            contactItemList.add(contactItem)
        }
    }

    private fun deleteCheckedItems() {
        for (i in contactItemList.indices.reversed()) {
            if (contactItemList[i].isChecked && contactItemList[i].wasUserSelected) {
                contactItemList.removeAt(i)
                adapter.notifyItemRemoved(i)
            }
        }
    }

    private fun setUpAdapter() {
        adapter = ContactItemAdapter(this, this, contactItemList)
        binding.rvContactItems.adapter = adapter
        binding.rvContactItems.layoutManager = LinearLayoutManager(this)
        binding.rvContactItems.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
    }
}