package com.example.calculator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    TextView result, calculation;
    Button btn_0, btn_1, btn_2, btn_3, btn_4, btn_5, btn_6, btn_7, btn_8, btn_9;
    Button btn_plus, btn_minus, btn_mult, btn_div, btn_dot, btn_equal;
    Button btn_msave, btn_mread, btn_mclear;
    Button btn_clear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        result = findViewById(R.id.result);
        calculation = findViewById(R.id.calculation);
        assignId(btn_0, R.id.btn_0);
        assignId(btn_1, R.id.btn_1);
        assignId(btn_2, R.id.btn_2);
        assignId(btn_3, R.id.btn_3);
        assignId(btn_4, R.id.btn_4);
        assignId(btn_5, R.id.btn_5);
        assignId(btn_6, R.id.btn_6);
        assignId(btn_7, R.id.btn_7);
        assignId(btn_8, R.id.btn_8);
        assignId(btn_9, R.id.btn_9);
        assignId(btn_plus, R.id.btn_plus);
        assignId(btn_minus, R.id.btn_minus);
        assignId(btn_mult, R.id.btn_mult);
        assignId(btn_div, R.id.btn_div);
        assignId(btn_dot, R.id.btn_dot);
        assignId(btn_equal, R.id.btn_equal);
        assignId(btn_msave, R.id.btn_msave);
        assignId(btn_mread, R.id.btn_mread);
        assignId(btn_mclear, R.id.btn_mclear);
        assignId(btn_clear, R.id.btn_clear);
    }

    void assignId(Button btn, int id){
        btn = findViewById(id);
        btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Button btn = (Button) view;
        String btnText = btn.getText().toString();
        String calculationData = calculation.getText().toString();
        if(btnText.equals("C")){
            calculation.setText("");
            result.setText("0");
            return;
        }
        if (btnText.equals("=")){
            calculation.setText("");
            result.getText();
            return;
        }else{
            calculationData = calculationData + btnText;
        }
        calculation.setText(calculationData);

        String calculationResult = getResult(calculationData);
        if (!calculationResult.equals("Err")) {
            result.setText(calculationResult);
        }
    }

    String getResult(String data){
        try{
            Context context = Context.enter();
            context.setOptimizationLevel(-1);
            Scriptable scriptable = context.initStandardObjects();
            String calculationResult = context.evaluateString(scriptable, data, "Javascript", 1, null).toString();
            if(calculationResult.endsWith(".0")){
                calculationResult = calculationResult.replace(".0", "");
            }
            return calculationResult;
        }catch (Exception e){
            return "Err";
        }
    }
}