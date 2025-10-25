package com.internship.healthcare;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.internship.healthcare.adapters.ChatListAdapter;
import com.internship.healthcare.models.Chat;
import com.internship.healthcare.utils.MessagingUtils;
import com.internship.healthcare.utils.SessionManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
/**
 * MessagesActivity.java
 * A comprehensive healthcare management Android application
 * Activity displaying list of all chat conversations. Integrates with Firebase Realtime Database.
 *
 * <p>Extends: {@link AppCompatActivity}</p>
 *
 * <h3>Firebase Integration:</h3>
 * <ul>
 *   <li>Realtime Database</li>
 * </ul>
 * Package: com.internship.healthcare
 * 
 * @author Mustafa Merchant
 * @version 1.0
 * @since 2025
 */


public class MessagesActivity extends AppCompatActivity {

    private EditText searchInput;
    private ImageButton backButton;
    private RecyclerView messagesRecyclerView;
    private ProgressBar loadingProgress;
    private TextView emptyStateText;
    private TextView unreadCountBadge;


    private ChatListAdapter chatListAdapter;
    private List<Chat> allChats;
    private List<Chat> filteredChats;

    private String currentUserId;
    private DatabaseReference chatsRef;
    private ValueEventListener chatsListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        SessionManager sessionManager = new SessionManager(this);
        currentUserId = sessionManager.getUserId();

        initializeViews();

        backButton.setOnClickListener(v -> finish());

        setupSearch();

        setupMessagesRecyclerView();

        loadChats();
    }

    private void initializeViews() {
        backButton = findViewById(R.id.back_button);
        searchInput = findViewById(R.id.search_doctor);
    
        messagesRecyclerView = findViewById(R.id.messages_recycler_view);
        loadingProgress = findViewById(R.id.loading_progress);
        emptyStateText = findViewById(R.id.empty_state_text);
        unreadCountBadge = findViewById(R.id.unread_count_badge);

        allChats = new ArrayList<>();
        filteredChats = new ArrayList<>();

        chatsRef = FirebaseDatabase.getInstance()
                .getReference("chats")
                .child(currentUserId);
    }

    private void setupSearch() {
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterChats(s.toString());
            }
    

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupMessagesRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        messagesRecyclerView.setLayoutManager(layoutManager);

        chatListAdapter = new ChatListAdapter(chat -> {
            Intent intent = new Intent(MessagesActivity.this, ChatActivity.class);
            intent.putExtra("chatId", chat.getChatId());
            intent.putExtra("otherUserId", chat.getOtherUserId());
            intent.putExtra("otherUserName", chat.getOtherUserName());
            intent.putExtra("otherUserImage", chat.getOtherUserImage());
            intent.putExtra("otherUserRole", chat.getOtherUserRole());
            startActivity(intent);
    
        });
        messagesRecyclerView.setAdapter(chatListAdapter);
    }

    private void loadChats() {
    
        showLoading();

        chatsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                allChats.clear();
                int totalUnread = 0;

                for (DataSnapshot chatSnapshot : snapshot.getChildren()) {
                    Chat chat = chatSnapshot.getValue(Chat.class);
    
                    if (chat != null) {
                        allChats.add(chat);
                        totalUnread += chat.getUnreadCount();
                    }
                }

                // Sort by last message time (most recent first)
                Collections.sort(allChats, new Comparator<Chat>() {
                    @Override
                    public int compare(Chat c1, Chat c2) {
                        return Long.compare(c2.getLastMessageTime(), c1.getLastMessageTime());
                    }
    
                });

                filteredChats = new ArrayList<>(allChats);
                chatListAdapter.updateChats(filteredChats);
                updateUnreadBadge(totalUnread);
                hideLoading();
            }

    
            @Override
            public void onCancelled(DatabaseError error) {
                hideLoading();
            }
        };

        chatsRef.addValueEventListener(chatsListener);
    }

    private void filterChats(String query) {
        if (query == null || query.trim().isEmpty()) {
            filteredChats = new ArrayList<>(allChats);
        } else {
            filteredChats = new ArrayList<>();
            String lowerQuery = query.toLowerCase().trim();

            for (Chat chat : allChats) {
                if (chat.getOtherUserName() != null && 
    
                    chat.getOtherUserName().toLowerCase().contains(lowerQuery)) {
                    filteredChats.add(chat);
                } else if (chat.getLastMessage() != null && 
                           chat.getLastMessage().toLowerCase().contains(lowerQuery)) {
                    filteredChats.add(chat);
                }
            }
    
        }

        chatListAdapter.updateChats(filteredChats);
        
        if (filteredChats.isEmpty() && !allChats.isEmpty()) {
            emptyStateText.setText("No chats found");
            emptyStateText.setVisibility(View.VISIBLE);
            messagesRecyclerView.setVisibility(View.GONE);
        } else if (!filteredChats.isEmpty()) {
            emptyStateText.setVisibility(View.GONE);
            messagesRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void updateUnreadBadge(int count) {
        if (unreadCountBadge != null) {
            if (count > 0) {
                unreadCountBadge.setVisibility(View.VISIBLE);
                unreadCountBadge.setText(count > 99 ? "99+" : String.valueOf(count));
    
            } else {
                unreadCountBadge.setVisibility(View.GONE);
            }
        }
    }

    private void showLoading() {
        loadingProgress.setVisibility(View.VISIBLE);
        emptyStateText.setVisibility(View.GONE);
        messagesRecyclerView.setVisibility(View.GONE);
    }

    private void hideLoading() {
        loadingProgress.setVisibility(View.GONE);

        if (allChats.isEmpty()) {
            emptyStateText.setText("No messages yet.\nStart a conversation with a doctor!");
            emptyStateText.setVisibility(View.VISIBLE);
    
            messagesRecyclerView.setVisibility(View.GONE);
        } else {
            emptyStateText.setVisibility(View.GONE);
            messagesRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MessagingUtils.updateOnlineStatus(currentUserId, true);
    }
    

    @Override
    protected void onPause() {
        super.onPause();
        MessagingUtils.updateOnlineStatus(currentUserId, false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (chatsListener != null && chatsRef != null) {
            chatsRef.removeEventListener(chatsListener);
        }
    }
}

    
    
    
    
    
    