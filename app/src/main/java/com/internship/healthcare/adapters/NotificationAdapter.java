package com.internship.healthcare.adapters;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.internship.healthcare.R;
import com.internship.healthcare.models.Notification;

import java.util.ArrayList;
import java.util.List;
/**
 * NotificationAdapter.java
 * A comprehensive healthcare management Android application
 * 
 * Package: com.internship.healthcare.adapters
 * 
 * @author Mustafa Merchant
 * @version 1.0
 * @since 2025
 */


public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private List<Notification> notifications = new ArrayList<>();
    private OnNotificationClickListener listener;

    public interface OnNotificationClickListener {
        void onNotificationClick(Notification notification);
        void onDeleteClick(Notification notification);
    }

    public NotificationAdapter(OnNotificationClickListener listener) {
        this.listener = listener;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
        notifyDataSetChanged();
    }

    public void removeNotification(Notification notification) {
        int position = notifications.indexOf(notification);
        if (position != -1) {
            notifications.remove(position);
            notifyItemRemoved(position);
        }
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = notifications.get(position);
        holder.bind(notification, listener);
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private ImageView notificationIcon;
        private ImageView doctorImageView;
        private TextView notificationTitle;
        private TextView notificationMessage;
        private TextView notificationTime;
        private View unreadIndicator;
        private ImageView deleteButton;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.notification_card);
            notificationIcon = itemView.findViewById(R.id.notification_icon);
            doctorImageView = itemView.findViewById(R.id.doctor_image);
            notificationTitle = itemView.findViewById(R.id.notification_title);
            notificationMessage = itemView.findViewById(R.id.notification_message);
            notificationTime = itemView.findViewById(R.id.notification_time);
            unreadIndicator = itemView.findViewById(R.id.unread_indicator);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }

        public void bind(Notification notification, OnNotificationClickListener listener) {
            notificationTitle.setText(notification.getTitle());
            notificationMessage.setText(notification.getMessage());

            // Format timestamp to relative time
            if (notification.getTimestamp() > 0) {
                CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
                        notification.getTimestamp(),
                        System.currentTimeMillis(),
                        DateUtils.MINUTE_IN_MILLIS,
                        DateUtils.FORMAT_ABBREV_RELATIVE
                );
                notificationTime.setText(timeAgo);
            } else if (notification.getTime() != null) {
                notificationTime.setText(notification.getTime());
            }

            // Show/hide unread indicator
            unreadIndicator.setVisibility(notification.isRead() ? View.GONE : View.VISIBLE);

            if (notification.isRead()) {
                cardView.setCardBackgroundColor(itemView.getContext().getColor(R.color.white));
            } else {
                cardView.setCardBackgroundColor(itemView.getContext().getColor(R.color.notification_unread_bg));
            }

            int iconRes = getIconForType(notification.getType());
            notificationIcon.setImageResource(iconRes);
            notificationIcon.setColorFilter(getColorForType(notification.getType()));

            if (notification.getDoctorImage() != null && !notification.getDoctorImage().isEmpty()) {
                doctorImageView.setVisibility(View.VISIBLE);
                Glide.with(itemView.getContext())
                        .load(notification.getDoctorImage())
                        .placeholder(R.drawable.ic_doctor_placeholder)
                        .circleCrop()
                        .into(doctorImageView);
            } else {
                doctorImageView.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onNotificationClick(notification);
                }
            });

            deleteButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(notification);
                }
            });
        }

        private int getIconForType(String type) {
            if (type == null) return R.drawable.ic_notification;

            switch (type) {
                case "appointment_approved":
                    return R.drawable.ic_check;
                case "appointment_rejected":
                    return R.drawable.ic_close;
                case "appointment_reminder":
                    return R.drawable.ic_calendar;
                case "message":
                    return R.drawable.ic_message;
                default:
                    return R.drawable.ic_notification;
            }
        }

        private int getColorForType(String type) {
            return itemView.getContext().getColor(R.color.colorPrimary);
        }
    }
}