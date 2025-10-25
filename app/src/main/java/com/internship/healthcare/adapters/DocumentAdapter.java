package com.internship.healthcare.adapters;

import android.net.Uri;
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
 * DocumentAdapter.java
 * A comprehensive healthcare management Android application
 * 
 * Package: com.internship.healthcare.adapters
 * 
 * @author Mustafa Merchant
 * @version 1.0
 * @since 2025
 */


public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.DocumentViewHolder> {

    private List<Uri> documentUris;
    private OnDocumentRemoveListener removeListener;

    public interface OnDocumentRemoveListener {
        void onDocumentRemove(int position);
    }
    public DocumentAdapter(List<Uri> documentUris, OnDocumentRemoveListener removeListener) {
        this.documentUris = documentUris;
        this.removeListener = removeListener;
    }

    @NonNull
    @Override
    public DocumentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_document, parent, false);
        return new DocumentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DocumentViewHolder holder, int position) {
        Uri documentUri = documentUris.get(position);
        
        // Extract filename from URI
        String fileName = getFileName(documentUri);
        holder.documentName.setText(fileName);
        
        holder.documentSize.setText("Document " + (position + 1));
        holder.removeButton.setOnClickListener(v -> {
            if (removeListener != null) {
                removeListener.onDocumentRemove(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return documentUris.size();
    }

    private String getFileName(Uri uri) {
        String path = uri.getPath();
        if (path != null) {
            int cut = path.lastIndexOf('/');
            if (cut != -1) {
                return path.substring(cut + 1);
            }
        }
        return "Document";
    }

    static class DocumentViewHolder extends RecyclerView.ViewHolder {
        TextView documentName;
        TextView documentSize;
        ImageView removeButton;

        DocumentViewHolder(@NonNull View itemView) {
            super(itemView);
            documentName = itemView.findViewById(R.id.document_name);
            documentSize = itemView.findViewById(R.id.document_size);
            removeButton = itemView.findViewById(R.id.remove_document_button);
        }
    }
}