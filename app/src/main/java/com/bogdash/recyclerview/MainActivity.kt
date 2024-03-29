package com.bogdash.recyclerview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.bogdash.recyclerview.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: ContactItemAdapter
    private lateinit var binding: ActivityMainBinding
    private var isDeleteModeActive = false
    private val contactItemList = mutableListOf<ContactItem>()
    private val contactDataManager = ContactDataManager()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        contactItemList.addAll(contactDataManager.generateMockContactList())
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete -> {
                isDeleteModeActive = !isDeleteModeActive

                toggleFABVisibility(isDeleteModeActive)

                if (!isDeleteModeActive) {
                    contactItemList.forEach { it.isChecked = false }
                }

                val newList =
                    contactItemList.map { it.copy(isDeleteModeActive = isDeleteModeActive) }
                val diffCallback = DiffUtilCallback(contactItemList, newList)
                val diffResult = DiffUtil.calculateDiff(diffCallback)

                contactItemList.forEach { it.isChecked = !isDeleteModeActive }

                adapter.submitList(newList)
                diffResult.dispatchUpdatesTo(adapter)

                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun resetSelections() {
        val newList = contactItemList.map { it.copy(isChecked = false) }
        val diffCallback = DiffUtilCallback(contactItemList, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        contactItemList.forEach { it.isChecked = false }
        adapter.submitList(newList)
        diffResult.dispatchUpdatesTo(adapter)
    }

    private fun toggleFABVisibility(visible: Boolean) {
        binding.fabAddContactItem.visibility = if (visible) View.GONE else View.VISIBLE
        binding.fabCheck.visibility = if (visible) View.VISIBLE else View.GONE
        binding.fabCancel.visibility = if (visible) View.VISIBLE else View.GONE
    }

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
                val newItem = ContactItem(newId, firstName, lastName, phone)

                val newList = mutableListOf<ContactItem>().apply {
                    addAll(contactItemList)
                    add(newItem)
                }

                val diffCallback = DiffUtilCallback(contactItemList, newList)
                val diffResult = DiffUtil.calculateDiff(diffCallback)
                contactItemList.add(newItem)
                adapter.submitList(newList)
                diffResult.dispatchUpdatesTo(adapter)

                val newPosition = adapter.itemCount - 1

                binding.rvContactItems.smoothScrollToPosition(newPosition)
            }
            setNegativeButton("Cancel") { _, _ -> }
            setView(dialogLayout)
            show()
        }
    }

    private fun deleteCheckedItems() {
        for (i in contactItemList.indices.reversed()) {
            if (contactItemList[i].isChecked) {
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

    fun showEditDialog(position: Int) {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialog, null)
        val tvEditContact = dialogLayout.findViewById<TextView>(R.id.tv_add_contact)
        val editTextFirstName = dialogLayout.findViewById<EditText>(R.id.tied_firstname)
        val editTextLastName = dialogLayout.findViewById<EditText>(R.id.tied_lastname)
        val editTextPhone = dialogLayout.findViewById<EditText>(R.id.ed_phone)

        val originalItem = contactItemList[position].copy()
        tvEditContact.text = getString(R.string.edit_contact)
        editTextFirstName.setText(originalItem.firstName)
        editTextLastName.setText(originalItem.lastName)
        editTextPhone.setText(originalItem.phone)

        with(builder) {
            setPositiveButton("Ok") { _, _ ->
                val firstName = editTextFirstName.text.toString()
                val lastName = editTextLastName.text.toString()
                val phone = editTextPhone.text.toString()

                if (originalItem.firstName != firstName || originalItem.lastName != lastName || originalItem.phone != phone) {
                    contactItemList[position].firstName = firstName
                    contactItemList[position].lastName = lastName
                    contactItemList[position].phone = phone

                    adapter.updateItem(position)
                    val newList = contactItemList.toList()
                    val diffCallback = DiffUtilCallback(contactItemList, newList)
                    val diffResult = DiffUtil.calculateDiff(diffCallback)
                    diffResult.dispatchUpdatesTo(adapter)
                }
            }
            setNegativeButton("Cancel") { _, _ -> }
            setView(dialogLayout)
            show()
        }
    }
}