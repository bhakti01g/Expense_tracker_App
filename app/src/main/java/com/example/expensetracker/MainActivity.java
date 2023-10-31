package com.example.expensetracker;

import androidx.annotation.ColorRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnItemClick {

    FirebaseAuth auth;
    Button button;
    FirebaseUser user;
    TextView addIncome;
    TextView addExpense;
    RecyclerView recyclerView;
    PieChart pieChart;
    private long income=0,expense=0;

    private ExpensesAdapter expensesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addIncome=findViewById(R.id.addIncome);
        addExpense=findViewById(R.id.addExpense);
        recyclerView=findViewById(R.id.recycler);
        pieChart=findViewById(R.id.pieChart);

        expensesAdapter=new ExpensesAdapter(this,this);
        recyclerView.setAdapter(expensesAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        auth=FirebaseAuth.getInstance();
        button=findViewById(R.id.logout);

        user=auth.getCurrentUser();
        if(user==null){
            Intent intent=new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }
        addIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,AddExpenseActivity.class);
                intent.putExtra("type","Income");
                startActivity(intent);
            }
        });

        addExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,AddExpenseActivity.class);
                intent.putExtra("type","Expense");
                startActivity(intent);
            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent=new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        income=0;expense=0;
        getData();
    }

    private void getData() {

        FirebaseFirestore.getInstance().collection("expenses")
                .whereEqualTo("uid",FirebaseAuth.getInstance().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {;
                        expensesAdapter.clear();
                        List<DocumentSnapshot>dsList=queryDocumentSnapshots.getDocuments();
                        for(DocumentSnapshot ds:dsList){
                            ExpenseModel expenseModel=ds.toObject(ExpenseModel.class);

                            if(expenseModel.getType().equals("Income")){
                                income+=expenseModel.getAmount();
                            }else {
                                expense+=expenseModel.getAmount();
                            }
                            expensesAdapter.add(expenseModel);
                        }
                        setUpGraph();
                    }
                });
    }
    private void setUpGraph(){
        List<PieEntry>pieEntryList=new ArrayList<>();
        List<Integer> colorList = new ArrayList<>();
        List<Integer>colorList1=new ArrayList<>();
        colorList.add(R.color.purple);
        colorList.add(R.color.gray);


        if(income!=0){
            pieEntryList.add(new PieEntry(income,"Income"));
            for(int colorId:colorList) {
                int color = ContextCompat.getColor(getApplicationContext(), colorId);
                colorList1.add(color);
            }
        }
        if(expense!=0){
            pieEntryList.add(new PieEntry(expense,"Expense"));
            for(int colorId:colorList) {
                int color = ContextCompat.getColor(getApplicationContext(), colorId);
                colorList1.add(color);
            }
            PieDataSet pieDataSet=new PieDataSet(pieEntryList,String.valueOf(income-expense));
            pieDataSet.setColors(colorList1);
            PieData pieData=new PieData(pieDataSet);
            pieChart.setData(pieData);
            pieChart.invalidate();

        }

    }
    @Override
    public void onClick(ExpenseModel expenseModel) {
        Intent intent=new Intent(MainActivity.this,AddExpenseActivity.class);
        intent.putExtra("model",expenseModel);
        startActivity(intent);
    }
}
