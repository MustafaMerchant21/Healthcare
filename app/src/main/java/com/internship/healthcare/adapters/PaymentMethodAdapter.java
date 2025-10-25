package com.internship.healthcare.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.internship.healthcare.R;
import com.internship.healthcare.models.PaymentMethod;

import java.util.List;
/**
 * PaymentMethodAdapter.java
 * A comprehensive healthcare management Android application
 * 
 * Package: com.internship.healthcare.adapters
 * 
 * @author Mustafa Merchant
 * @version 1.0
 * @since 2025
 */


public class PaymentMethodAdapter extends RecyclerView.Adapter<PaymentMethodAdapter.PaymentViewHolder> {

    private List<PaymentMethod> paymentMethods;
    private int selectedPosition = -1;
    private OnPaymentMethodClickListener listener;

    public interface OnPaymentMethodClickListener {
        void onPaymentMethodSelected(PaymentMethod paymentMethod, int position);
        void onDeleteClicked(PaymentMethod paymentMethod, int position);
    }

    public PaymentMethodAdapter(List<PaymentMethod> paymentMethods, OnPaymentMethodClickListener listener) {
        this.paymentMethods = paymentMethods;
        this.listener = listener;
        
        // Find default payment method
        for (int i = 0; i < paymentMethods.size(); i++) {
            if (paymentMethods.get(i).isDefault()) {
                selectedPosition = i;
                break;
            }
        }
    }

    @NonNull
    @Override
    public PaymentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_payment_method, parent, false);
        return new PaymentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentViewHolder holder, int position) {
        PaymentMethod payment = paymentMethods.get(position);
        
        holder.radioButton.setChecked(position == selectedPosition);
        
        if (payment.getType() == PaymentMethod.PaymentType.CARD) {
            holder.paymentIcon.setImageResource(R.drawable.ic_credit_card);
            holder.paymentTitle.setText(payment.getMaskedCardNumber());
            String subtitle = payment.getCardType();
            if (payment.getExpiryDate() != null && !payment.getExpiryDate().isEmpty()) {
                subtitle += " • Expires " + payment.getExpiryDate();
            }
            holder.paymentSubtitle.setText(subtitle);
        } else {
            holder.paymentIcon.setImageResource(R.drawable.ic_upi);
            holder.paymentTitle.setText(payment.getUpiId());
            holder.paymentSubtitle.setText("UPI Payment");
        }
        
        // Show/hide default badge
        if (payment.isDefault()) {
            holder.defaultBadge.setVisibility(View.VISIBLE);
        } else {
            holder.defaultBadge.setVisibility(View.GONE);
        }
        
        holder.paymentCard.setOnClickListener(v -> {
            int oldPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            
            notifyItemChanged(oldPosition);
            notifyItemChanged(selectedPosition);
            
            if (listener != null) {
                listener.onPaymentMethodSelected(payment, selectedPosition);
            }
        });
        
        holder.deleteButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClicked(payment, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return paymentMethods.size();
    }

    public void updateData(List<PaymentMethod> newPaymentMethods) {
        this.paymentMethods = newPaymentMethods;
        
        // Find default payment method
        selectedPosition = -1;
        for (int i = 0; i < paymentMethods.size(); i++) {
            if (paymentMethods.get(i).isDefault()) {
                selectedPosition = i;
                break;
            }
        }
        
        notifyDataSetChanged();
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    static class PaymentViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView paymentCard;
        RadioButton radioButton;
        ImageView paymentIcon;
        TextView paymentTitle;
        TextView paymentSubtitle;
        TextView defaultBadge;
        ImageView deleteButton;

        public PaymentViewHolder(@NonNull View itemView) {
            super(itemView);
            paymentCard = itemView.findViewById(R.id.payment_card);
            radioButton = itemView.findViewById(R.id.radio_button);
            paymentIcon = itemView.findViewById(R.id.payment_icon);
            paymentTitle = itemView.findViewById(R.id.payment_title);
            paymentSubtitle = itemView.findViewById(R.id.payment_subtitle);
            defaultBadge = itemView.findViewById(R.id.default_badge);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }
}