package com.example.appdocbao.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.appdocbao.Adapter.ChatAdapter;
import com.example.appdocbao.Model.Message;
import com.example.appdocbao.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatbotActivity extends AppCompatActivity {

    private RecyclerView recyclerViewChat;
    private EditText editTextMessage;
    private Button buttonSend;
    private ChatAdapter chatAdapter;
    private List<Message> messageList;
    private RequestQueue requestQueue;

    // THAY THẾ BẰNG URL API CỦA BẠN
    private static final String API_URL = "http://10.0.2.2:5000/get_response"; // Ví dụ: "https://api.example.com/get_response"
    private static final String TAG = "ChatbotApp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        recyclerViewChat = findViewById(R.id.recyclerViewChat);
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);

        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewChat.setLayoutManager(layoutManager);
        recyclerViewChat.setAdapter(chatAdapter);

        requestQueue = Volley.newRequestQueue(this);

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userMessageText = editTextMessage.getText().toString().trim();
                if (!userMessageText.isEmpty()) {
                    sendMessage(userMessageText);
                }
            }
        });
    }

    private void sendMessage(String messageText) {
        // Hiển thị tin nhắn người dùng
        Message userMessage = new Message(messageText, true);
        messageList.add(userMessage);
        chatAdapter.notifyItemInserted(messageList.size() - 1);
        recyclerViewChat.scrollToPosition(messageList.size() - 1);
        editTextMessage.setText(""); // Xóa nội dung trong EditText

        // Gọi API để lấy phản hồi từ chatbot
        getChatbotResponse(messageText);
    }

    private void getChatbotResponse(String userQuery) {
        // Hiển thị tin nhắn "Bot đang gõ..." (tùy chọn)
        // Message typingMessage = new Message("Bot đang gõ...", false);
        // messageList.add(typingMessage);
        // chatAdapter.notifyItemInserted(messageList.size() - 1);
        // recyclerViewChat.scrollToPosition(messageList.size() - 1);

        // Ví dụ: API của bạn nhận một JSON object với key "query"
        // và trả về một JSON object với key "response"
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("query", userQuery); // Key này phụ thuộc vào API của bạn
        } catch (JSONException e) {
            Log.e(TAG, "JSONException khi tạo request body: " + e.getMessage());
            addBotMessage("Lỗi tạo yêu cầu: " + e.getMessage());
            return;
        }

        // Nếu API của bạn là GET và truyền query qua URL parameter:
        // String urlWithQuery = API_URL + "?query=" + Uri.encode(userQuery);
        // JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlWithQuery, null,

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, API_URL, requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Xóa tin nhắn "Bot đang gõ..." nếu có
                        // if (!messageList.isEmpty() && messageList.get(messageList.size() -1 ).getText().equals("Bot đang gõ...")) {
                        //     messageList.remove(messageList.size() - 1);
                        //     chatAdapter.notifyItemRemoved(messageList.size());
                        // }
                        try {
                            // Key "response_text" này phụ thuộc vào cấu trúc JSON trả về từ API của bạn
                            String botReply = response.getString("response_text"); // THAY ĐỔI KEY NÀY
                            addBotMessage(botReply);
                        } catch (JSONException e) {
                            Log.e(TAG, "JSONException khi parse response: " + e.getMessage());
                            addBotMessage("Lỗi xử lý phản hồi từ bot.");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Volley error: " + error.toString());
                        String errorMessage = "Lỗi kết nối tới bot.";
                        if (error.networkResponse != null) {
                            errorMessage += " Mã lỗi: " + error.networkResponse.statusCode;
                        }
                        addBotMessage(errorMessage);
                    }
                }) {
            // Tùy chọn: Thêm headers nếu API yêu cầu (ví dụ: Authorization token)
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                // headers.put("Authorization", "Bearer YOUR_API_TOKEN");
                // headers.put("Content-Type", "application/json"); // Mặc định Volley sẽ đặt nếu requestBody là JSONObject
                return headers;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }

    private void addBotMessage(String messageText) {
        Message botMessage = new Message(messageText, false);
        messageList.add(botMessage);
        chatAdapter.notifyItemInserted(messageList.size() - 1);
        recyclerViewChat.scrollToPosition(messageList.size() - 1);
    }
}