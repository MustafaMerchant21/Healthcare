package com.internship.healthcare;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.internship.healthcare.adapters.ChatMessageAdapter;
import com.internship.healthcare.models.ChatMessage;
import com.internship.healthcare.utils.MessagingUtils;
import com.internship.healthcare.utils.SessionManager;
import com.internship.healthcare.utils.SupabaseImageUploader;

import java.util.ArrayList;
import java.util.List;
/**
 * ChatActivity.java
 * A comprehensive healthcare management Android application
 * Activity managing real-time chat communication between doctor and patient. Integrates with Firebase Realtime Database.
 *
 * <p>Extends: {@link AppCompatActivity}</p>
 * <p>Implements: {@link ImagePreviewAdapter.OnImageDeleteListener}</p>
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


public class ChatActivity extends AppCompatActivity implements ImagePreviewAdapter.OnImageDeleteListener {

    private RecyclerView messagesRecyclerView;
    private RecyclerView imagePreviewRecycler;
    private EditText messageInput;
    private ImageButton sendButton;
    private ImageButton attachImageButton;
    private ImageButton backButton;
    private ImageButton callButton;

    private ImageView doctorAvatar;
    private TextView doctorNameText;
    private TextView onlineStatus;
    private LinearLayout imagePreviewContainer;
    private ProgressBar loadingProgress;
    private TextView emptyStateText;

    private ChatMessageAdapter chatMessageAdapter;
    private ImagePreviewAdapter imagePreviewAdapter;
    private List<ChatMessage> chatMessages;
    private List<Uri> selectedImages;

    private String chatId;
    private String otherUserId;
    private String otherUserName;
    private String otherUserImage;
    private String otherUserRole;
    private String currentUserId;
    private String currentUserName;
    
    private DatabaseReference messagesRef;
    private ChildEventListener messagesListener;
    private boolean isFirstLoad = true;

    private ActivityResultLauncher<String> imagePickerLauncher;
    private ProgressDialog uploadDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        SessionManager sessionManager = new SessionManager(this);
        currentUserId = sessionManager.getUserId();
        currentUserName = sessionManager.getUserName();

        Intent intent = getIntent();
        chatId = intent.getStringExtra("chatId");
        otherUserId = intent.getStringExtra("otherUserId");
        otherUserName = intent.getStringExtra("otherUserName");
        otherUserImage = intent.getStringExtra("otherUserImage");
        otherUserRole = intent.getStringExtra("otherUserRole");

        if (chatId == null || otherUserId == null) {
            Toast.makeText(this, "Error: Missing chat information", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViews();
    

        setupMessagesRecyclerView();
        setupImagePreviewRecyclerView();

        setupListeners();

        setupImagePicker();

        MessagingUtils.initializeChatMetadata(currentUserId, otherUserId, otherUserName, 
                otherUserImage, otherUserRole, chatId);

        loadMessages();

        // Mark messages as read
        MessagingUtils.markMessagesAsRead(chatId, currentUserId);

        listenForOnlineStatus();
    }

    private void initializeViews() {
        messagesRecyclerView = findViewById(R.id.messages_recycler_view);
        imagePreviewRecycler = findViewById(R.id.image_preview_recycler);
        messageInput = findViewById(R.id.message_input);
        sendButton = findViewById(R.id.send_button);
        attachImageButton = findViewById(R.id.attach_image_button);
        backButton = findViewById(R.id.back_button);
        callButton = findViewById(R.id.call_button);
        doctorAvatar = findViewById(R.id.doctor_avatar);
        doctorNameText = findViewById(R.id.doctor_name);
        onlineStatus = findViewById(R.id.online_status);
        imagePreviewContainer = findViewById(R.id.image_preview_container);
        loadingProgress = findViewById(R.id.loading_progress);
        emptyStateText = findViewById(R.id.empty_state_text);

        if (otherUserName != null) {
            doctorNameText.setText(otherUserName);
        }
        
        if (otherUserImage != null && !otherUserImage.isEmpty()) {
            Glide.with(this)
                    .load(otherUserImage)
                    .circleCrop()
                    .placeholder(R.drawable.ic_profile)
                    .error(R.drawable.ic_profile)
                    .into(doctorAvatar);
    
        } else {
            doctorAvatar.setImageResource(R.drawable.ic_profile);
        }

        chatMessages = new ArrayList<>();
        selectedImages = new ArrayList<>();

        messagesRef = FirebaseDatabase.getInstance().getReference("messages").child(chatId);
    }

    private void setupMessagesRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true); // Start from bottom
        messagesRecyclerView.setLayoutManager(layoutManager);
        
        chatMessageAdapter = new ChatMessageAdapter(currentUserId, imageUrl -> {
            Intent intent = new Intent(ChatActivity.this, ImageViewerActivity.class);
            intent.putExtra("imageUrl", imageUrl);
            startActivity(intent);
        });
        messagesRecyclerView.setAdapter(chatMessageAdapter);
    }

    private void setupImagePreviewRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        imagePreviewRecycler.setLayoutManager(layoutManager);
        
        imagePreviewAdapter = new ImagePreviewAdapter(selectedImages, this);
        imagePreviewRecycler.setAdapter(imagePreviewAdapter);
    }

    private void setupListeners() {
        // Back button
        backButton.setOnClickListener(v -> finish());

        callButton.setOnClickListener(v -> makeCall());

        // Text input listener
    
        messageInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Enable/disable send button based on input
                boolean hasText = s.toString().trim().length() > 0;
                boolean hasImages = !selectedImages.isEmpty();
                sendButton.setEnabled(hasText || hasImages);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    

        sendButton.setOnClickListener(v -> sendMessage());

        // Attach image button
        attachImageButton.setOnClickListener(v -> pickImage());
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
    
                uri -> {
                    if (uri != null) {
                        selectedImages.add(uri);
                        imagePreviewAdapter.notifyItemInserted(selectedImages.size() - 1);
                        imagePreviewContainer.setVisibility(View.VISIBLE);
                        sendButton.setEnabled(true);
                    }
                }
        );
    }

    
    private void pickImage() {
        imagePickerLauncher.launch("image/*");
    }

    @Override
    public void onImageDelete(int position) {
        selectedImages.remove(position);
        imagePreviewAdapter.notifyItemRemoved(position);
        imagePreviewAdapter.notifyItemRangeChanged(position, selectedImages.size());
        
    
        if (selectedImages.isEmpty()) {
            imagePreviewContainer.setVisibility(View.GONE);
            boolean hasText = messageInput.getText().toString().trim().length() > 0;
            sendButton.setEnabled(hasText);
        }
    }

    private void sendMessage() {
        String messageText = messageInput.getText().toString().trim();
        
        if (messageText.isEmpty() && selectedImages.isEmpty()) {
            return;
        }

        // Disable send button to prevent double-sending
    
        sendButton.setEnabled(false);

        if (!selectedImages.isEmpty()) {
            sendImageMessages();
        } else if (!messageText.isEmpty()) {
            sendTextMessage(messageText);
        }
    }

    private void sendTextMessage(String messageText) {
        MessagingUtils.sendMessage(chatId, currentUserId, otherUserId, messageText, 
                currentUserName, new MessagingUtils.OnMessageSentListener() {
            @Override
    
            public void onSuccess(String messageId) {
                runOnUiThread(() -> {
                    messageInput.setText("");
                    sendButton.setEnabled(true);
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(ChatActivity.this, "Failed to send message: " + error, 
                            Toast.LENGTH_SHORT).show();
                    sendButton.setEnabled(true);
                });
            }
        });
    
    }

    private void sendImageMessages() {
        uploadDialog = new ProgressDialog(this);
        uploadDialog.setMessage("Uploading image...");
        uploadDialog.setCancelable(false);
        uploadDialog.show();
    

        // Upload first image (can be extended for multiple images)
        Uri imageUri = selectedImages.get(0);
        String fileName = "chat_images/" + chatId + "/" + System.currentTimeMillis() + ".jpg";

        SupabaseImageUploader imageUploader = new SupabaseImageUploader(this);

        imageUploader.uploadImage(
                imageUri,
                "chat-images",  // bucket name
                fileName,
                new SupabaseImageUploader.UploadCallback() {
                    @Override
                    public void onSuccess(String publicUrl) {
                        runOnUiThread(() -> {
                            if (uploadDialog != null && uploadDialog.isShowing()) {
    
                                uploadDialog.dismiss();
                            }

                            MessagingUtils.sendImageMessage(chatId, currentUserId, otherUserId,
                                    publicUrl, currentUserName, new MessagingUtils.OnMessageSentListener() {
                                        @Override
                                        public void onSuccess(String messageId) {
                                            runOnUiThread(() -> {
                                                messageInput.setText("");
                                                selectedImages.clear();
                                                imagePreviewAdapter.notifyDataSetChanged();
                                                imagePreviewContainer.setVisibility(View.GONE);
                                                sendButton.setEnabled(true);
                                            });
                                        }

                                        @Override
                                        public void onFailure(String error) {
                                            runOnUiThread(() -> {
    
                                                Toast.makeText(ChatActivity.this,
                                                        "Failed to send image: " + error,
                                                        Toast.LENGTH_SHORT).show();
                                                sendButton.setEnabled(true);
                                            });
                                        }
                                    });
                        });
    
                    }

                    @Override
                    public void onFailure(String error) {
                        runOnUiThread(() -> {
                            if (uploadDialog != null && uploadDialog.isShowing()) {
                                uploadDialog.dismiss();
                            }
                            Toast.makeText(ChatActivity.this, "Upload failed: " + error,
                                    Toast.LENGTH_SHORT).show();
                            sendButton.setEnabled(true);
                        });
    
                    }
                }
        );
    }

    private void makeCall() {
        Toast.makeText(this, "Call functionality coming soon", Toast.LENGTH_SHORT).show();
    }

    private void loadMessages() {
        showLoading();

        messagesListener = new ChildEventListener() {
            @Override
    
            public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                ChatMessage message = snapshot.getValue(ChatMessage.class);
                if (message != null) {
                    chatMessages.add(message);
                    chatMessageAdapter.updateMessages(chatMessages);
                    
                    if (isFirstLoad) {
                        messagesRecyclerView.scrollToPosition(chatMessages.size() - 1);
                    } else {
                        messagesRecyclerView.smoothScrollToPosition(chatMessages.size() - 1);
                    }

                    // Mark as read if from other user
                    if (message.getReceiverId().equals(currentUserId) && !message.isRead()) {
                        messagesRef.child(message.getId()).child("isRead").setValue(true);
                    }
                }
                
                if (isFirstLoad) {
                    hideLoading();
    
                    isFirstLoad = false;
                }
            }

            @Override
            public void onChildChanged(DataSnapshot snapshot, String previousChildName) {
                ChatMessage updatedMessage = snapshot.getValue(ChatMessage.class);
                if (updatedMessage != null) {
                    for (int i = 0; i < chatMessages.size(); i++) {
                        if (chatMessages.get(i).getId().equals(updatedMessage.getId())) {
                            chatMessages.set(i, updatedMessage);
                            chatMessageAdapter.notifyItemChanged(i);
                            break;
    
                        }
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot snapshot) {
                ChatMessage removedMessage = snapshot.getValue(ChatMessage.class);
                if (removedMessage != null) {
                    for (int i = 0; i < chatMessages.size(); i++) {
                        if (chatMessages.get(i).getId().equals(removedMessage.getId())) {
                            chatMessages.remove(i);
                            chatMessageAdapter.notifyItemRemoved(i);
                            break;
                        }
    
                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot snapshot, String previousChildName) {}

            @Override
            public void onCancelled(DatabaseError error) {
                hideLoading();
                Toast.makeText(ChatActivity.this, "Failed to load messages", Toast.LENGTH_SHORT).show();
            }
        };

        messagesRef.addChildEventListener(messagesListener);
    }

    
    private void listenForOnlineStatus() {
        DatabaseReference presenceRef = FirebaseDatabase.getInstance()
                .getReference("presence").child(otherUserId);
        
        presenceRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Boolean isOnline = snapshot.child("online").getValue(Boolean.class);
                    if (isOnline != null && isOnline) {
                        onlineStatus.setText("Online");
                        onlineStatus.setVisibility(View.VISIBLE);
                    } else {
                        Long lastSeen = snapshot.child("lastSeen").getValue(Long.class);
                        if (lastSeen != null) {
                            onlineStatus.setText(formatLastSeen(lastSeen));
                            onlineStatus.setVisibility(View.VISIBLE);
                        } else {
    
                            onlineStatus.setVisibility(View.GONE);
                        }
                    }
                } else {
                    onlineStatus.setVisibility(View.GONE);
                }
    
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }

    
    private String formatLastSeen(long timestamp) {
        long diff = System.currentTimeMillis() - timestamp;
        long minutes = diff / (60 * 1000);
        long hours = diff / (60 * 60 * 1000);
        long days = diff / (24 * 60 * 60 * 1000);

        if (minutes < 1) {
            return "Just now";
        } else if (minutes < 60) {
            return minutes + " min ago";
        } else if (hours < 24) {
            return hours + " hour" + (hours > 1 ? "s" : "") + " ago";
        } else {
            return days + " day" + (days > 1 ? "s" : "") + " ago";
        }
    }

    private void showLoading() {
        loadingProgress.setVisibility(View.VISIBLE);
        emptyStateText.setVisibility(View.GONE);
        messagesRecyclerView.setVisibility(View.GONE);
    }

    private void hideLoading() {
        loadingProgress.setVisibility(View.GONE);
        
        if (chatMessages.isEmpty()) {
            emptyStateText.setVisibility(View.VISIBLE);
            emptyStateText.setText("No messages yet. Start the conversation!");
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
        if (messagesListener != null && messagesRef != null) {
            messagesRef.removeEventListener(messagesListener);
        }
        if (uploadDialog != null && uploadDialog.isShowing()) {
            uploadDialog.dismiss();
        }
    }
}

    
    
    
    
    
    
    
    
    
    
    