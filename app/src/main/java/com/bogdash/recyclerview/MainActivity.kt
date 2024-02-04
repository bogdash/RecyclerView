package com.bogdash.recyclerview

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.bogdash.recyclerview.databinding.ActivityMainBinding
import java.text.FieldPosition

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
            showDialog()
        }
    }

    // menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete -> {
                binding.fabAddContactItem.visibility = View.GONE
                binding.fabCheck.visibility = View.VISIBLE
                binding.fabCancel.visibility = View.VISIBLE

                Toast.makeText(this, "Menu Item is Pressed", Toast.LENGTH_SHORT).show()
                for (contactItem in contactItemList) {
                    contactItem.isChecked = !contactItem.isChecked
                }
                adapter.notifyDataSetChanged()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun showDialog() {
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
            setPositiveButton("OK"){ dialog, which ->
                firstName = editTextFirstName.text.toString()
                lastName = editTextLastName.text.toString()
                phone = editTextPhone.text.toString()
                Log.d("MyLog", firstName)
                Log.d("MyLog", lastName)
                Log.d("MyLog", phone)

                val newId = contactItemList.size + 1
                contactItemList.add(ContactItem(newId, firstName, lastName, phone.toInt()))
                adapter.notifyDataSetChanged() //diff util
                Log.d("MyLog", newId.toString())
            }
            setNegativeButton("Cancel"){ _, _ ->
                Log.d("MyLog", "Negative button clicked")
            }

            setView(dialogLayout)
            show()
        }
    }

    fun editDialog(position: Int) {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialog, null)
        val editTextFirstName = dialogLayout.findViewById<EditText>(R.id.tied_firstname)
        val editTextLastName = dialogLayout.findViewById<EditText>(R.id.tied_lastname)
        val editTextPhone = dialogLayout.findViewById<EditText>(R.id.ed_phone)

        var firstName: String
        var lastName: String
        var phone: String

        editTextFirstName.setText(contactItemList[position].firstName)
        editTextLastName.setText(contactItemList[position].lastName)
        editTextPhone.setText(contactItemList[position].phone.toString())

        with(builder) {
            setPositiveButton("Ok") { dialog, which ->
                firstName = editTextFirstName.text.toString()
                lastName = editTextLastName.text.toString()
                phone = editTextPhone.text.toString()

                contactItemList[position].firstName = firstName
                contactItemList[position].lastName = lastName
                contactItemList[position].phone = phone.toInt()
                adapter.notifyDataSetChanged()
            }
            setNegativeButton("Cancel"){ _, _ ->
                Log.d("MyLog", "Negative button clicked")
            }

            setView(dialogLayout)
            show()
        }
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
        adapter = ContactItemAdapter(this,this, contactItemList)
        binding.rvContactItems.adapter = adapter
        binding.rvContactItems.layoutManager = LinearLayoutManager(this)
        binding.rvContactItems.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
    }
}