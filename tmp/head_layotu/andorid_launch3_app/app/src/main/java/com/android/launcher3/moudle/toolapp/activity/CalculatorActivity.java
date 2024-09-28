package com.android.launcher3.moudle.toolapp.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.android.launcher3.R;
import com.android.launcher3.common.base.BaseActivity;
import com.android.launcher3.utils.BigDecimalManager;

public class CalculatorActivity extends BaseActivity implements View.OnClickListener {

    private TextView textView;
    private String currentNumber = "0";//显示数字
    private String currentOperator = "";//运算符
    private double operand1 = 0;//前一个数字
    private double operand2 = 0;//后一个数字
    private boolean isLastValue = false;

    /**
     * //1.位数多时不应该显示省略号
     * 2.连续点两下运算符，最后计算值错误
     *
     */
    @Override
    protected int getResourceId() {
        return R.layout.activity_calculator;
    }

    @Override
    protected void initView() {
        findViewById(R.id.btn_ac).setOnClickListener(this);
        findViewById(R.id.btn_del).setOnClickListener(this);
        findViewById(R.id.btn_mul).setOnClickListener(this);
        findViewById(R.id.btn_sub).setOnClickListener(this);
        findViewById(R.id.btn_1).setOnClickListener(this);
        findViewById(R.id.btn_2).setOnClickListener(this);
        findViewById(R.id.btn_3).setOnClickListener(this);
        findViewById(R.id.btn_4).setOnClickListener(this);
        findViewById(R.id.btn_5).setOnClickListener(this);
        findViewById(R.id.btn_6).setOnClickListener(this);
        findViewById(R.id.btn_7).setOnClickListener(this);
        findViewById(R.id.btn_8).setOnClickListener(this);
        findViewById(R.id.btn_9).setOnClickListener(this);
        findViewById(R.id.btn_0).setOnClickListener(this);
        findViewById(R.id.btn_equal).setOnClickListener(this);
        findViewById(R.id.btn_jian).setOnClickListener(this);
        findViewById(R.id.btn_add).setOnClickListener(this);
        findViewById(R.id.btn_div).setOnClickListener(this);
        textView = findViewById(R.id.tv_text);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ac:
                onClearClick();
                break;
            case R.id.btn_del:
                delBtnAction();
                break;
            case R.id.btn_mul:
            case R.id.btn_div:
            case R.id.btn_add:
            case R.id.btn_jian:
                onOperatorClick(v);
                break;
            case R.id.btn_sub:
                pointBtnAction();
                break;
            case R.id.btn_equal:
                equalsBntAction();
                break;
            case R.id.btn_0:
            case R.id.btn_1:
            case R.id.btn_2:
            case R.id.btn_3:
            case R.id.btn_4:
            case R.id.btn_5:
            case R.id.btn_6:
            case R.id.btn_7:
            case R.id.btn_8:
            case R.id.btn_9:
                numberBtnAction(v);
                break;
        }
    }

    /**
     * 数字键
     *
     * @param view
     */
    private void numberBtnAction(View view) {
        if (isLastValue) {
            currentNumber = "0";
            isLastValue = false;
        }

        if ((currentNumber.contains(".") && currentNumber.length() > 9) || (!currentNumber.contains(".") && currentNumber.length() >= 9)) {
            return;
        }
        Button button = (Button) view;
        String num = button.getText().toString();
        if (currentNumber.equals("0") || TextUtils.isEmpty(currentNumber)) {
            currentNumber = num;
        } else {
            currentNumber += num;
        }
        textView.setText(currentNumber);
    }

    /**
     * 小数点
     */
    private void pointBtnAction() {
        if (isLastValue) {
            currentNumber = "0";
            isLastValue = false;
        }

        if (currentNumber.equals(".") || TextUtils.isEmpty(currentNumber)) {
            currentNumber = "0.";
        }
        if (!currentNumber.contains(".")) {
            currentNumber += ".";
        }
        textView.setText(currentNumber);
    }

    /**
     * 加减乘除
     *
     * @param view
     */
    public void onOperatorClick(View view) {
        Button button = (Button) view;
        if (!currentNumber.equals("")) {
            if (!currentOperator.equals("")) {
                equalsBntAction();
            }
            operand1 = Double.parseDouble(currentNumber);
        }
        currentOperator = button.getText().toString();
        currentNumber = "";
    }

    /**
     * 等于号
     */
    private void equalsBntAction() {

        if (!TextUtils.isEmpty(currentNumber)) {
            operand2 = Double.parseDouble(currentNumber);
        } else {
            operand2 = operand1;
        }
        double result = 0;

        switch (currentOperator) {
            case "":
                result = operand2;
                break;
            case "+":
                result = BigDecimalManager.additionDouble(operand1, operand2);
                break;
            case "-":
                result = BigDecimalManager.subtractionDouble(operand1, operand2);
                break;
            case "×":
                result = BigDecimalManager.multiplicationDouble(operand1, operand2, 8);
                break;
            case "÷":
                if (operand2 != 0) {
                    result = BigDecimalManager.divisionDouble(operand1, operand2, 8);
                } else {
                    currentNumber = "0";
                    currentOperator = "";
                    operand1 = 0;
                    operand2 = 0;
                    textView.setText("错误");
                    return;
                }
                break;
        }
        isLastValue = true;

        String value = String.valueOf(result);

        if (value.contains("E")) {
            //四舍五入
            double numberBeforeE = BigDecimalManager.getNumberBeforeE(result);

            double doubleValue = BigDecimalManager.getDoubleValue(numberBeforeE, 9);

            String str = String.valueOf(doubleValue);

            String substring = "";

            substring = value.substring(value.indexOf("E"));
            value = str + substring;
        } else {
            if (value.length() >= 15) {
                result = BigDecimalManager.getDoubleValue(result, 7);
                value = String.valueOf(result);
            }
        }

        if ((int) result == result) {
            //强制转换后会丢失精度,如果丢失精度的数和原数相等，说明就是整数
            value = String.valueOf((int) result);
        }

        textView.setText(value);
        currentNumber = value;
        currentOperator = "";

    }

    /**
     * 全部清除
     */
    public void onClearClick() {
        currentNumber = "0";
        currentOperator = "";
        operand1 = 0;
        operand2 = 0;
        textView.setText("0");
    }

    /**
     * 清除
     */
    private void delBtnAction() {
        currentNumber = "0";
        textView.setText("0");
    }

}