package com.example.calculator;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.datastore.preferences.core.MutablePreferences;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import java.math.BigDecimal;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView result, calculation;
    Button btn_0, btn_1, btn_2, btn_3, btn_4, btn_5, btn_6, btn_7, btn_8, btn_9;
    Button btn_plus, btn_minus, btn_mult, btn_div, btn_dot, btn_equal;
    Button btn_msave, btn_mread, btn_mclear;
    Button btn_clear;
    boolean isNumberPressed = false;

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

        initButtons();

        disableButtons(btn_0, btn_plus, btn_minus, btn_mult, btn_div);
    }

    private void initButtons() {
        btn_0 = findViewById(R.id.btn_0);
        btn_1 = findViewById(R.id.btn_1);
        btn_2 = findViewById(R.id.btn_2);
        btn_3 = findViewById(R.id.btn_3);
        btn_4 = findViewById(R.id.btn_4);
        btn_5 = findViewById(R.id.btn_5);
        btn_6 = findViewById(R.id.btn_6);
        btn_7 = findViewById(R.id.btn_7);
        btn_8 = findViewById(R.id.btn_8);
        btn_9 = findViewById(R.id.btn_9);
        btn_plus = findViewById(R.id.btn_plus);
        btn_minus = findViewById(R.id.btn_minus);
        btn_mult = findViewById(R.id.btn_mult);
        btn_div = findViewById(R.id.btn_div);
        btn_dot = findViewById(R.id.btn_dot);
        btn_equal = findViewById(R.id.btn_equal);
        btn_clear = findViewById(R.id.btn_clear);
        btn_msave = findViewById(R.id.btn_msave);
        btn_mread = findViewById(R.id.btn_mread);
        btn_mclear = findViewById(R.id.btn_mclear);

        btn_0.setOnClickListener(this);
        btn_1.setOnClickListener(this);
        btn_2.setOnClickListener(this);
        btn_3.setOnClickListener(this);
        btn_4.setOnClickListener(this);
        btn_5.setOnClickListener(this);
        btn_6.setOnClickListener(this);
        btn_7.setOnClickListener(this);
        btn_8.setOnClickListener(this);
        btn_9.setOnClickListener(this);
        btn_plus.setOnClickListener(this);
        btn_minus.setOnClickListener(this);
        btn_mult.setOnClickListener(this);
        btn_div.setOnClickListener(this);
        btn_dot.setOnClickListener(this);
        btn_equal.setOnClickListener(this);
        btn_clear.setOnClickListener(this);
        btn_msave.setOnClickListener(this);
        btn_mread.setOnClickListener(this);
        btn_mclear.setOnClickListener(this);
    }

    private void disableButtons(Button... buttons) {
        for (Button button : buttons) {
            button.setEnabled(false);
        }
    }

    private void enableButtons(Button... buttons) {
        for (Button button : buttons) {
            button.setEnabled(true);
        }
    }

    @Override
    public void onClick(View view) {
        Button btn = (Button) view;
        String btnText = btn.getText().toString();
        String calculationData = calculation.getText().toString();

        if (btnText.equals("C")) {
            calculation.setText("");
            result.setText("0");
            disableButtons(btn_0, btn_plus, btn_minus, btn_mult, btn_div);
            isNumberPressed = false;
            return;
        }

        if (btnText.equals("MS")) {
            saveInMemory();
            return;
        }

        if (btnText.equals("MR")) {
            readFromMemory(calculationData);
            return;
        }

        if (btnText.equals("MC")) {
            clearFromMemory(calculationData);
            return;
        }

        if (btnText.equals("=")) {
            calculation.setText("");
            return;
        } else {
            if (!isNumberPressed && (btnText.equals("0") || btnText.matches("[+\\-*/]"))) {
                return;
            }
            calculationData += btnText;
            calculation.setText(calculationData);
        }

        String calculationResult = getResult(calculationData);
        if (!calculationResult.equals("Err")) {
            result.setText(calculationResult);
        }
        isNumberPressed = true;
        enableButtons(btn_0, btn_plus, btn_minus, btn_mult, btn_div);
    }

    String getResult(String data) {
        try {
            Context rhino = Context.enter();
            rhino.setOptimizationLevel(-1);
            Scriptable scope = rhino.initStandardObjects();
            String calculationResult = rhino.evaluateString(scope, data, "JavaScript", 1, null).toString();

            BigDecimal resultValue = new BigDecimal(calculationResult);
            resultValue = resultValue.setScale(10, BigDecimal.ROUND_HALF_UP);

            calculationResult = resultValue.stripTrailingZeros().toPlainString();
            return calculationResult;
        } catch (Exception e) {
            return "Err";
        } finally {
            Context.exit();
        }
    }

    private void saveInMemory() {
        String result = this.result.getText().toString();
        DataStoreManager.getInstance(this).getDataStore()
                .updateDataAsync(prefsIn -> {
                    MutablePreferences mutablePreferences = prefsIn.toMutablePreferences();
                    mutablePreferences.set(DataStoreManager.MEMORY_KEY, result);
                    return Single.just(mutablePreferences);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    @SuppressLint({"CheckResult", "SetTextI18n"})
    private void readFromMemory(String calculationData) {
        DataStoreManager.getInstance(this).getDataStore()
                .data()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(prefs -> {
                    String savedMemory = prefs.get(DataStoreManager.MEMORY_KEY);
                    if (savedMemory != null) {
                        calculation.setText(calculationData + savedMemory);
                        result.setText(getResult(calculationData + savedMemory));
                        enableButtons(btn_0, btn_plus, btn_minus, btn_mult, btn_div);
                        isNumberPressed = true;
                    }
                });
    }

    @SuppressLint("CheckResult")
    private void clearFromMemory(String calculationData) {
        DataStoreManager.getInstance(this).getDataStore()
                .updateDataAsync(prefsIn -> {
                    MutablePreferences mutablePreferences = prefsIn.toMutablePreferences();
                    mutablePreferences.remove(DataStoreManager.MEMORY_KEY);
                    return Single.just(mutablePreferences);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(prefs -> {
                    if (!calculationData.isEmpty()) {
                        calculation.setText(calculationData);
                        result.setText(getResult(calculationData));
                    } else {
                        calculation.setText("");
                        result.setText("0");
                    }
                });
    }
}
