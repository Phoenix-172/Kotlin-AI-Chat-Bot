package com.example.kotlinchatbot

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// Adapter class for handling the chat messages
class ChatAdapter(private val messages: List<Message>) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    // ViewHolder to represent each chat message item
    class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val messageTextView: TextView = view.findViewById(android.R.id.text1)
    }

    // Inflate the item view for each message
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return ChatViewHolder(view)
    }

    // Bind the message data to the TextView with a prefix
    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message = messages[position]
        // Add a prefix to the message content
        val prefixedMessage = if (message.type == MessageType.USER) {
            "User: ${message.content}"
        } else {
            "Bot: ${message.content}"
        }
        holder.messageTextView.text = prefixedMessage
    }

    // Return the total number of messages
    override fun getItemCount(): Int = messages.size
}
