package com.example.smack.Services

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.smack.Controller.App
import com.example.smack.Model.Channel
import com.example.smack.Model.Message
import com.example.smack.Utilities.URL_GET_CHANNEL
import com.example.smack.Utilities.URL_GET_MESSAGES
import org.json.JSONException

object MessageService {

    val channels = ArrayList<Channel>()
    val messages = ArrayList<Message>()

    fun getChannels(complete: (Boolean) -> Unit) {

        val channelRequest = object : JsonArrayRequest(Method.GET, URL_GET_CHANNEL, null, Response.Listener { response ->
            try {

                for (x in 0 until response.length()) {
                    val channel = response.getJSONObject(x)
                    val channelName = channel.getString("name")
                    val channelDescription = channel.getString("description")
                    val channelId = channel.getString("_id")

                    val newChannel = Channel(channelName, channelDescription, channelId)
                    this.channels.add(newChannel)
                }
                complete(true)

            } catch (e: JSONException) {
                Log.d("JSON", "EXC: " + e.localizedMessage)
                complete(false)
            }

        }, Response.ErrorListener { error ->
            Log.d("ERROR", "Could not retrieve channels")
            complete(false)
        }) {

            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer ${App.prefs.authToken}"
                return headers
            }
        }
        App.prefs.requestQueue.add(channelRequest)
    }

    fun getMessages(channelId: String, complete: (Boolean) -> Unit) {

        val url = "$URL_GET_MESSAGES$channelId"

        val messageRequest = object : JsonArrayRequest(Method.GET, url, null, Response.Listener { response ->
            clearMessages()
            try {
                for (x in 0 until response.length()) {
                    val message = response.getJSONObject(x)
                    val msgBody = message.getString("messageBody")
                    val channelId = message.getString("channelId")
                    val id = message.getString("_id")
                    val userName = message.getString("userName")
                    val userAvatar = message.getString("userAvatar")
                    val userAvatarColor = message.getString("userAvatarColor")
                    val timeStamp = message.getString("timeStamp")

                    val newMessage = Message(msgBody, userName, channelId, userAvatar, userAvatarColor, id, timeStamp)
                    this.messages.add(newMessage)
                }
                complete(true)

            } catch (e: JSONException) {
                Log.d("JSON", "EXC: " + e.localizedMessage)
                complete(false)
            }

        }, Response.ErrorListener {
            Log.d("ERROR", "Could not retrieve messages")
            complete(false)
        }) {

            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer ${App.prefs.authToken}"
                return headers
            }
        }
        App.prefs.requestQueue.add(messageRequest)
    }

    fun clearMessages() {
        messages.clear()
    }

    fun clearChannels() {
        channels.clear()
    }
}