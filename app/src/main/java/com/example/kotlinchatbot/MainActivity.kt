package com.example.kotlinchatbot

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.IOException

// Enum to represent message types
enum class MessageType {
    USER,
    BOT
}

// Model class to represent a message
data class Message(val content: String, val type: MessageType)

class MainActivity : AppCompatActivity() {

    // List to store messages
    private val messagesList = mutableListOf<Message>()

    // Adapter for the RecyclerView
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var inputMessage: EditText
    private lateinit var sendButton: Button

    // Function to handle network call
    private suspend fun getAIResponse(myUrl: String): String {
        return withContext(Dispatchers.IO) {
            val result = StringBuilder()
            try {
                val url = URL(myUrl)
                val httpURLConnection = url.openConnection() as HttpURLConnection
                httpURLConnection.requestMethod = "GET"

                BufferedReader(InputStreamReader(httpURLConnection.inputStream)).use { reader ->
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        result.append(line)
                    }
                }
                httpURLConnection.disconnect()
            } catch (e: IOException) {
                return@withContext "Error: ${e.message}"
            }
            return@withContext result.toString()
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Initialize views
        recyclerView = findViewById(R.id.recyclerView)
        inputMessage = findViewById(R.id.inputMessage)
        sendButton = findViewById(R.id.sendButton)

        // Handle window insets for edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize RecyclerView with the adapter
        setupRecyclerView()

        // Handle Send button click
        sendButton.setOnClickListener {
            val userMessage = inputMessage.text.toString()
            if (userMessage.isNotEmpty()) {
                // Add the user's message to the list
                addMessageToChat(userMessage, MessageType.USER)

                // Clear input field after sending
                inputMessage.text.clear()

                // Fetch bot response in a coroutine
                CoroutineScope(Dispatchers.Main).launch {
                    val botResponse = getAIResponse("http://10.0.2.2:5000/getData?prompt=$userMessage")
                    addMessageToChat(botResponse, MessageType.BOT)
                }
            } else {
                // Show a Toast if the input is empty
                Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Function to add message and update RecyclerView
    private fun addMessageToChat(message: String, type: MessageType) {
        messagesList.add(Message(message, type))
        chatAdapter.notifyDataSetChanged() // Notify adapter to refresh the UI
        recyclerView.scrollToPosition(messagesList.size - 1) // Scroll to the latest message
    }



    // Set up RecyclerView and its adapter
    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter(messagesList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = chatAdapter
    }
}