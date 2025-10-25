package com.internship.healthcare.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.internship.healthcare.R;

import java.util.List;
/**
 * DocumentUrlAdapter.java
 * A comprehensive healthcare management Android application
 * 
 * Package: com.internship.healthcare.adapters
 * 
 * @author Mustafa Merchant
 * @version 1.0
 * @since 2025
 */


public class DocumentUrlAdapter extends RecyclerView.Adapter<DocumentUrlAdapter.ViewHolder> {

    private List<String> documentUrls;
    private OnDocumentClickListener listener;

    public interface OnDocumentClickListener {
        void onDocumentClick(String documentUrl);
    }
    public DocumentUrlAdapter(List<String> documentUrls, OnDocumentClickListener listener) {
        this.documentUrls = documentUrls;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_document_url, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String documentUrl = documentUrls.get(position);
        
        holder.documentName.setText("Certificate " + (position + 1));
        holder.documentSubtext.setText("Tap to view");
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDocumentClick(documentUrl);
            }
        });
    }

    @Override
    public int getItemCount() {
        return documentUrls.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView documentName, documentSubtext;
        ImageView documentIcon;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            documentName = itemView.findViewById(R.id.document_name);
            documentSubtext = itemView.findViewById(R.id.document_subtext);
            documentIcon = itemView.findViewById(R.id.document_icon);
        }
    }
}