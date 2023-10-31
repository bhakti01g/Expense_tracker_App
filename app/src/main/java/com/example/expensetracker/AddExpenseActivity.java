package com.example.expensetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class AddExpenseActivity extends AppCompatActivity {

    EditText editTextAmount,editTextNote,editTextCategory;

    RadioButton incomeRadio;
    RadioButton expenseRadio;
    ExpenseModel expenseModel=new ExpenseModel();

//    private ExpenseModel expenseModel;

    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);
        editTextAmount=findViewById(R.id.amount);
        editTextNote=findViewById(R.id.note);
        editTextCategory=findViewById(R.id.category);
        incomeRadio=findViewById(R.id.incomeRadio);
        expenseRadio=findViewById(R.id.expenseRadio);



        type=getIntent().getStringExtra("type");
        expenseModel=(ExpenseModel) getIntent().getSerializableExtra("model");

        if(type==null) {
            type = expenseModel.getType();
            editTextAmount.setText(String.valueOf(expenseModel.getAmount()));
            editTextCategory.setText(expenseModel.getCategory());
            editTextNote.setText(expenseModel.getNote());
        }

        if (type != null && type.equals("Income")) {
            incomeRadio.setChecked(true);
        }else{
            expenseRadio.setChecked(true);
        }

        incomeRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type="Income";
            }
        });

        expenseRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type="Expense";
            }
        });

    }

    SimpleDateFormat sdf=new SimpleDateFormat("dd MM yyyy_HH:mm", Locale.getDefault());
    String currentDateAndTime=sdf.format(new Date());

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        if(expenseModel==null) {
            menuInflater.inflate(R.menu.add_menu, menu);
        }else {
            menuInflater.inflate(R.menu.update_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.saveExpense){
            if(type!=null) {
                createExpense();
            }else {
                updateExpense();
            }
            return true;
        }
        if(id==R.id.deleteExpense){
            deleteExpense();
        }
        return false;
    }
    private void createExpense(){
        String expenseId= UUID.randomUUID().toString();
        String amount=editTextAmount.getText().toString();
        String note=editTextNote.getText().toString();
        String category=editTextCategory.getText().toString();


        boolean incomeChecked=incomeRadio.isChecked();
        if(incomeChecked){
            type="Income";
        }else{
            type="Expense";
        }

        if(amount.trim().length()==0){
            editTextAmount.setError("Empty");
            return;
        }

        ExpenseModel expenseModel=new ExpenseModel(expenseId,note,category,type,Long.parseLong(amount),FirebaseAuth.getInstance().getUid());

        FirebaseFirestore.getInstance().collection("expenses")
                .document(expenseId).set(expenseModel);
        finish();
    }

    private void deleteExpense(){
        FirebaseFirestore.getInstance().collection("expenses").document(expenseModel.getExpenseId()).delete();
        finish();
    }

    private void updateExpense(){
        String expenseId= expenseModel.getExpenseId();
        String amount=editTextAmount.getText().toString();
        String note=editTextNote.getText().toString();
        String category=editTextCategory.getText().toString();

        boolean incomeChecked=incomeRadio.isChecked();
        if(incomeChecked){
            type="Income";
        }else{
            type="Expense";
        }

        if(amount.trim().length()==0){
            editTextAmount.setError("Empty");
            return;
        }

        ExpenseModel Model=new ExpenseModel(expenseId,note,category,type,Long.parseLong(amount), FirebaseAuth.getInstance().getUid());

        FirebaseFirestore.getInstance().collection("expenses")
                .document(expenseId).set(Model);
        finish();
    }
}