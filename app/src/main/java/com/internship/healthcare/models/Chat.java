

package com.internship.healthcare.models;
/**
 * Chat.java
 * A comprehensive healthcare management Android application
 *
 * Package: com.internship.healthcare.models
 *
 * @author Mustafa Merchant
 * @version 1.0
 * @since 2025
 */
public class Chat {
    private String chatId;
    private String lastMessage;
    private long lastMessageTime;
    private int unreadCount;
    private String otherUserId;
    private String otherUserName;
    private String otherUserImage;
    private String otherUserRole; // "patient" or "doctor"

    private boolean isOnline;
    
    public Chat() {
        // Required empty constructor for Firebase
    }
    
    public Chat(String chatId, String lastMessage, long lastMessageTime, int unreadCount,
                String otherUserId, String otherUserName, String otherUserImage, String otherUserRole) {
        this.chatId = chatId;
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
        this.unreadCount = unreadCount;
        this.otherUserId = otherUserId;
        this.otherUserName = otherUserName;
        this.otherUserImage = otherUserImage;
        this.otherUserRole = otherUserRole;
        this.isOnline = false;
    }
    
    public String getChatId() {
        return chatId;
    }
    
    public String getLastMessage() {
        return lastMessage;
    }
    
    public long getLastMessageTime() {
        return lastMessageTime;
    }
    
    public int getUnreadCount() {
        return unreadCount;
    }

    
    public String getOtherUserId() {
        return otherUserId;
    }
    
    public String getOtherUserName() {
        return otherUserName;

    }
    
    public String getOtherUserImage() {
        return otherUserImage;
    }
    
    public String getOtherUserRole() {

        return otherUserRole;
    }
    
    public boolean isOnline() {
        return isOnline;
    }
    

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }
    
    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    
    public void setLastMessageTime(long lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }
    
    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;

    }
    
    public void setOtherUserId(String otherUserId) {
        this.otherUserId = otherUserId;
    }
    
    public void setOtherUserName(String otherUserName) {

        this.otherUserName = otherUserName;
    }
    
    public void setOtherUserImage(String otherUserImage) {
        this.otherUserImage = otherUserImage;
    }
    

    public void setOtherUserRole(String otherUserRole) {
        this.otherUserRole = otherUserRole;
    }
    
    public void setOnline(boolean online) {
        isOnline = online;
    }

}









