package com.internship.healthcare;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.internship.healthcare.adapters.PaymentMethodAdapter;
import com.internship.healthcare.models.PaymentMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * PaymentMethodsActivity.java
 * A comprehensive healthcare management Android application
 * Activity managing payment method configuration. Integrates with Firebase Authentication, Realtime Database.
 *
 * <p>Extends: {@link AppCompatActivity}</p>
 *
 * <h3>Firebase Integration:</h3>
 * <ul>
 *   <li>Authentication</li>
 *   <li>Realtime Database</li>
 * </ul>
 * Package: com.internship.healthcare
 * 
 * @author Mustafa Merchant
 * @version 1.0
 * @since 2025
 */


public class PaymentMethodsActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private RecyclerView paymentMethodsRecycler;
    private LinearLayout emptyState;
    private FloatingActionButton fabAddPayment;
    
    private PaymentMethodAdapter adapter;
    private List<PaymentMethod> paymentMethods;

    
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    
    private BottomSheetDialog bottomSheetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_methods);
        
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(auth.getCurrentUser().getUid())
                .child("paymentMethods");
        
        toolbar = findViewById(R.id.toolbar);
        paymentMethodsRecycler = findViewById(R.id.payment_methods_recycler);
        emptyState = findViewById(R.id.empty_state);
        fabAddPayment = findViewById(R.id.fab_add_payment);
        
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        
        paymentMethods = new ArrayList<>();
        adapter = new PaymentMethodAdapter(paymentMethods, new PaymentMethodAdapter.OnPaymentMethodClickListener() {
            @Override
            public void onPaymentMethodSelected(PaymentMethod paymentMethod, int position) {
    
                setDefaultPaymentMethod(paymentMethod);
            }

            @Override
            public void onDeleteClicked(PaymentMethod paymentMethod, int position) {
                deletePaymentMethod(paymentMethod, position);
            }
        });
        
        paymentMethodsRecycler.setLayoutManager(new LinearLayoutManager(this));
        paymentMethodsRecycler.setAdapter(adapter);
        
        fabAddPayment.setOnClickListener(v -> showAddPaymentBottomSheet());
        
        loadPaymentMethods();
    }
    
    private void loadPaymentMethods() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                paymentMethods.clear();
                
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    PaymentMethod paymentMethod = dataSnapshot.getValue(PaymentMethod.class);
    
                    if (paymentMethod != null) {
                        paymentMethods.add(paymentMethod);
                    }
                }
                
                // Sort by timestamp (most recent first)
                paymentMethods.sort((p1, p2) -> Long.compare(p2.getTimestamp(), p1.getTimestamp()));
                
                adapter.updateData(paymentMethods);
                
    
                // Show/hide empty state
                if (paymentMethods.isEmpty()) {
                    emptyState.setVisibility(View.VISIBLE);
                    paymentMethodsRecycler.setVisibility(View.GONE);
                } else {
                    emptyState.setVisibility(View.GONE);
                    paymentMethodsRecycler.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PaymentMethodsActivity.this, 
                        "Failed to load payment methods", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    
    private void showAddPaymentBottomSheet() {
        bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_add_payment, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        
    
        MaterialButtonToggleGroup paymentTypeToggle = bottomSheetView.findViewById(R.id.payment_type_toggle);
        LinearLayout cardForm = bottomSheetView.findViewById(R.id.card_form);
        LinearLayout upiForm = bottomSheetView.findViewById(R.id.upi_form);
        
        TextInputLayout cardNumberLayout = bottomSheetView.findViewById(R.id.card_number_layout);
        TextInputEditText cardNumberInput = bottomSheetView.findViewById(R.id.card_number_input);
        TextInputLayout cardHolderLayout = bottomSheetView.findViewById(R.id.card_holder_layout);
        TextInputEditText cardHolderInput = bottomSheetView.findViewById(R.id.card_holder_input);
        TextInputLayout expiryMonthLayout = bottomSheetView.findViewById(R.id.expiry_month_layout);
        TextInputEditText expiryMonthInput = bottomSheetView.findViewById(R.id.expiry_month_input);
        TextInputLayout expiryYearLayout = bottomSheetView.findViewById(R.id.expiry_year_layout);
        TextInputEditText expiryYearInput = bottomSheetView.findViewById(R.id.expiry_year_input);
        TextInputLayout cvvLayout = bottomSheetView.findViewById(R.id.cvv_layout);
        TextInputEditText cvvInput = bottomSheetView.findViewById(R.id.cvv_input);
        
        TextInputLayout upiIdLayout = bottomSheetView.findViewById(R.id.upi_id_layout);
        TextInputEditText upiIdInput = bottomSheetView.findViewById(R.id.upi_id_input);
        
        MaterialCheckBox setDefaultCheckbox = bottomSheetView.findViewById(R.id.set_default_checkbox);
        MaterialButton btnSavePayment = bottomSheetView.findViewById(R.id.btn_save_payment);
        
        // Default selection is Card
        paymentTypeToggle.check(R.id.btn_card);
        
        // Toggle between Card and UPI forms
        paymentTypeToggle.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btn_card) {
                    cardForm.setVisibility(View.VISIBLE);
                    upiForm.setVisibility(View.GONE);
    
                } else if (checkedId == R.id.btn_upi) {
                    cardForm.setVisibility(View.GONE);
                    upiForm.setVisibility(View.VISIBLE);
                }
            }
        });
        
        cardNumberInput.addTextChangedListener(new TextWatcher() {
            private boolean isFormatting;
            
            @Override
    
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            
            @Override
            public void afterTextChanged(Editable s) {
                if (isFormatting) return;
                
                isFormatting = true;
                String input = s.toString().replaceAll("\\s", "");
                StringBuilder formatted = new StringBuilder();
                
                for (int i = 0; i < input.length(); i++) {
                    if (i > 0 && i % 4 == 0) {
                        formatted.append(" ");
                    }
                    formatted.append(input.charAt(i));
                }
                
                s.replace(0, s.length(), formatted.toString());
                isFormatting = false;
            }
        });
        
        btnSavePayment.setOnClickListener(v -> {
            int checkedId = paymentTypeToggle.getCheckedButtonId();
            
            if (checkedId == R.id.btn_card) {
                if (validateCardDetails(cardNumberLayout, cardHolderLayout, expiryMonthLayout, 
                        expiryYearLayout, cvvLayout)) {
                    saveCardPaymentMethod(
                            cardNumberInput.getText().toString().replaceAll("\\s", ""),
                            cardHolderInput.getText().toString(),
                            expiryMonthInput.getText().toString(),
                            expiryYearInput.getText().toString(),
                            cvvInput.getText().toString(),
                            setDefaultCheckbox.isChecked()
                    );
                    bottomSheetDialog.dismiss();
                }
            } else {
                if (validateUpiId(upiIdLayout)) {
                    saveUpiPaymentMethod(
                            upiIdInput.getText().toString(),
                            setDefaultCheckbox.isChecked()
                    );
                    bottomSheetDialog.dismiss();
    
                }
            }
        });
        
        bottomSheetDialog.show();
    }
    
    private boolean validateCardDetails(TextInputLayout cardNumberLayout, TextInputLayout cardHolderLayout,
                                       TextInputLayout expiryMonthLayout, TextInputLayout expiryYearLayout,
                                       TextInputLayout cvvLayout) {
    
        boolean isValid = true;
        
        String cardNumber = cardNumberLayout.getEditText().getText().toString().replaceAll("\\s", "");
        String cardHolder = cardHolderLayout.getEditText().getText().toString().trim();
        String expiryMonth = expiryMonthLayout.getEditText().getText().toString().trim();
        String expiryYear = expiryYearLayout.getEditText().getText().toString().trim();
        String cvv = cvvLayout.getEditText().getText().toString().trim();
        
        if (cardNumber.isEmpty() || cardNumber.length() < 13 || cardNumber.length() > 19) {
            cardNumberLayout.setError("Enter a valid card number");
    
            isValid = false;
        } else {
            cardNumberLayout.setError(null);
        }
        
        if (cardHolder.isEmpty()) {
            cardHolderLayout.setError("Enter card holder name");
            isValid = false;
        } else {
            cardHolderLayout.setError(null);
        }
        
        if (expiryMonth.isEmpty() || Integer.parseInt(expiryMonth) < 1 || Integer.parseInt(expiryMonth) > 12) {
            expiryMonthLayout.setError("Invalid month");
            isValid = false;
        } else {
            expiryMonthLayout.setError(null);
        }
        
        if (expiryYear.isEmpty() || expiryYear.length() != 2) {
            expiryYearLayout.setError("Invalid year");
            isValid = false;
        } else {
            expiryYearLayout.setError(null);
        }
        
        if (cvv.isEmpty() || cvv.length() < 3) {
            cvvLayout.setError("Invalid CVV");
            isValid = false;
        } else {
            cvvLayout.setError(null);
        }
        
        return isValid;
    }
    
    private boolean validateUpiId(TextInputLayout upiIdLayout) {
        String upiId = upiIdLayout.getEditText().getText().toString();
        
        // UPI ID format: username@bankname
        if (upiId.isEmpty() || !upiId.matches("^[\\w.]+@[\\w]+$")) {
            upiIdLayout.setError("Enter a valid UPI ID (e.g., username@upi)");
            return false;
        } else {
            upiIdLayout.setError(null);
            return true;
        }
    }
    
    private void saveCardPaymentMethod(String cardNumber, String cardHolderName, 
                                      String expiryMonth, String expiryYear, String cvv,
                                      boolean setAsDefault) {
        String paymentId = databaseReference.push().getKey();
    
        
        if (paymentId == null) {
            Toast.makeText(this, "Failed to generate payment ID", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String cardType = PaymentMethod.detectCardType(cardNumber);
        
        PaymentMethod paymentMethod = new PaymentMethod(
                paymentId,
                PaymentMethod.PaymentType.CARD,
                cardNumber,
                cardHolderName,
                expiryMonth,
                expiryYear,
                cardType,
                null,
                setAsDefault || paymentMethods.isEmpty(), // First card is default
                System.currentTimeMillis()
        );
        
        // If setting as default, unset other defaults
        if (setAsDefault || paymentMethods.isEmpty()) {
            unsetAllDefaults(() -> {
                databaseReference.child(paymentId).setValue(paymentMethod)
                        .addOnSuccessListener(aVoid -> 
                                Toast.makeText(this, "Card added successfully", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> 
                                Toast.makeText(this, "Failed to add card", Toast.LENGTH_SHORT).show());
            });
        } else {
            databaseReference.child(paymentId).setValue(paymentMethod)
                    .addOnSuccessListener(aVoid -> 
                            Toast.makeText(this, "Card added successfully", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> 
                            Toast.makeText(this, "Failed to add card", Toast.LENGTH_SHORT).show());
        }
    }
    
    private void saveUpiPaymentMethod(String upiId, boolean setAsDefault) {
        String paymentId = databaseReference.push().getKey();
        
        if (paymentId == null) {
            Toast.makeText(this, "Failed to generate payment ID", Toast.LENGTH_SHORT).show();
            return;
        }
        
        PaymentMethod paymentMethod = new PaymentMethod(
                paymentId,
                PaymentMethod.PaymentType.UPI,
                null,
                null,
                null,
                null,
                null,
                upiId,
                setAsDefault || paymentMethods.isEmpty(), // First UPI is default
                System.currentTimeMillis()
    
        );
        
        // If setting as default, unset other defaults
        if (setAsDefault || paymentMethods.isEmpty()) {
            unsetAllDefaults(() -> {
                databaseReference.child(paymentId).setValue(paymentMethod)
                        .addOnSuccessListener(aVoid -> 
                                Toast.makeText(this, "UPI added successfully", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> 
                                Toast.makeText(this, "Failed to add UPI", Toast.LENGTH_SHORT).show());
            });
        } else {
            databaseReference.child(paymentId).setValue(paymentMethod)
                    .addOnSuccessListener(aVoid -> 
                            Toast.makeText(this, "UPI added successfully", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> 
                            Toast.makeText(this, "Failed to add UPI", Toast.LENGTH_SHORT).show());
        }
    
    }
    
    private void setDefaultPaymentMethod(PaymentMethod paymentMethod) {
        unsetAllDefaults(() -> {
            databaseReference.child(paymentMethod.getId()).child("default").setValue(true)
                    .addOnSuccessListener(aVoid -> 
                            Toast.makeText(this, "Default payment method updated", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> 
                            Toast.makeText(this, "Failed to update default", Toast.LENGTH_SHORT).show());
        });
    }
    
    private void unsetAllDefaults(Runnable onComplete) {
        Map<String, Object> updates = new HashMap<>();
        
        for (PaymentMethod payment : paymentMethods) {
            updates.put(payment.getId() + "/default", false);
        }
        
        if (!updates.isEmpty()) {
            databaseReference.updateChildren(updates)
                    .addOnSuccessListener(aVoid -> {
                        if (onComplete != null) {
                            onComplete.run();
                        }
                    });
        } else {
            if (onComplete != null) {
                onComplete.run();
            }
        }
    }
    
    private void deletePaymentMethod(PaymentMethod paymentMethod, int position) {
        databaseReference.child(paymentMethod.getId()).removeValue()
                .addOnSuccessListener(aVoid -> 
                        Toast.makeText(this, "Payment method deleted", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> 
                        Toast.makeText(this, "Failed to delete payment method", Toast.LENGTH_SHORT).show());
    }
}

    
    
    
    