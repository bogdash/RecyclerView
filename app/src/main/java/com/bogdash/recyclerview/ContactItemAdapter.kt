package com.bogdash.recyclerview

import android.content.Context
import android.provider.ContactsContract.DisplayPhoto
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bogdash.recyclerview.databinding.ContactItemLayoutBinding

class ContactItemAdapter(
    private val mainActivity: MainActivity,
    private val context: Context, private val contactItemList: MutableList<ContactItem>
) : RecyclerView.Adapter<ContactItemAdapter.ContactItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactItemViewHolder {
        val binding = ContactItemLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        return ContactItemViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return contactItemList.size
    }

    override fun onBindViewHolder(holder: ContactItemViewHolder, position: Int) {
        val contactItem = contactItemList[position]
        holder.bind(contactItem)
        holder.itemView.setOnClickListener {
            mainActivity.editDialog(position)
        }
        holder.itemView.findViewById<CheckBox>(R.id.check_box).apply{
            setOnCheckedChangeListener { _, isChecked ->
                contactItem.isChecked = isChecked
                contactItem.wasUserSelected = true
            }
        }
    }

    class ContactItemViewHolder(contactItemLayoutBinding: ContactItemLayoutBinding) :
        RecyclerView.ViewHolder(contactItemLayoutBinding.root) {

        private val binding = contactItemLayoutBinding

        fun bind(contactItem: ContactItem) {
            binding.tvFirstname.text = contactItem.firstName
            binding.tvLastname.text = contactItem.lastName
            binding.tvPhone.text = "${contactItem.phone}"
            binding.checkBox.visibility = if (contactItem.isChecked) View.VISIBLE else View.GONE
        }
    }
}