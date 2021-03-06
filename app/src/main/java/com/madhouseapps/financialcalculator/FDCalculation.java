package com.madhouseapps.financialcalculator;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.IntegerRes;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class FDCalculation extends Fragment {

    /*
   true for year
   false for month
    */
    boolean mory = true;

    /*
    1  - Monthly
    2  - Quarterly
    3  - Half Yearly
    4  - Yearly
     */
    int comp = 4;
    double progress_value = 5;

    String rupee = "₹";


    TextView MVtitle, MVAmount, InterestTitle, InterestAmount, TotalTitle, TotalAmount;
    TextView DepositTitle;
    EditText DepositInput;
    TextView RateTitle, RatePercent;
    SeekBar RateChanger;
    TextView TenureTitle, TenureOptionsYearly, TenureOptionsMonthly;
    EditText TenureInput;
    TextView CompoundingTitle;
    Spinner CompoundingOptions;
    Button statsButton;

    double decimalProgress;

    List<String> compoundingOptions = new ArrayList<>();

    TextWatcher textWatcher;

    public FDCalculation() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_fdcalculation, container, false);


        MVtitle = (TextView) rootView.findViewById(R.id.MVtitle);
        MVAmount = (TextView) rootView.findViewById(R.id.MVAmount);
        InterestTitle = (TextView) rootView.findViewById(R.id.InterestTitle);
        InterestAmount = (TextView) rootView.findViewById(R.id.InterestAmount);
        TotalTitle = (TextView) rootView.findViewById(R.id.TotalTitle);
        TotalAmount = (TextView) rootView.findViewById(R.id.TotalAmount);
        DepositTitle = (TextView) rootView.findViewById(R.id.DepositTitle);
        RateTitle = (TextView) rootView.findViewById(R.id.RateTitle);
        RatePercent = (TextView) rootView.findViewById(R.id.RatePercent);
        TenureTitle = (TextView) rootView.findViewById(R.id.TenureTitle);
        TenureOptionsMonthly = (TextView) rootView.findViewById(R.id.TenureOptionsMonthly);
        TenureOptionsYearly = (TextView) rootView.findViewById(R.id.TenureOptionsYearly);
        CompoundingTitle = (TextView) rootView.findViewById(R.id.CompoundTitle);

        DepositInput = (EditText) rootView.findViewById(R.id.DepositInput);
        TenureInput = (EditText) rootView.findViewById(R.id.TenureInput);
        RateChanger = (SeekBar) rootView.findViewById(R.id.RateChanger);
        CompoundingOptions = (Spinner) rootView.findViewById(R.id.CompoundingOptions);
        statsButton = (Button) rootView.findViewById(R.id.statsButton);

        RateChanger.setProgress(50);

        compoundingOptions.clear();
        compoundingOptions.add("MONTHLY"); //1/12
        compoundingOptions.add("QUARTERLY"); //1/4
        compoundingOptions.add("HALF YEARLY"); //1/2
        compoundingOptions.add("YEARLY"); //1

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, compoundingOptions);
        CompoundingOptions.setAdapter(dataAdapter);
        setFont();

        CalculateAndSet(Integer.parseInt(DepositInput.getText().toString().trim()),
                comp,
                Integer.parseInt(TenureInput.getText().toString().trim()));


        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.length() > 0) {

                    if (s.charAt(s.toString().length() - 1) == '.' || s.charAt(0) == '.') {
                        //Dont do anything, wait for him to enter something after the '.'
                    } else {
                        if (!s.toString().equals("")) {

                            double value = Double.parseDouble(s.toString());
                            value = value * 10;
                            RateChanger.setProgress((int) value);
                            decimalProgress = (float) (Double.parseDouble(s.toString()));
                            progress_value = decimalProgress;
                            CalculateAndSet(Integer.parseInt(DepositInput.getText().toString().trim()),
                                    comp,
                                    Integer.parseInt(TenureInput.getText().toString().trim()));

                        }
                    }
                }


            }
        };



        /*

        Live edits & outputs along with seeker progress

         */
        RateChanger.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if(fromUser){

                    if(progress==0){
                        seekBar.setProgress(1);
                        decimalProgress = 0.1;
                        progress_value = decimalProgress;
                        RatePercent.setText(""+decimalProgress);
                    } else {
                        decimalProgress = ((float) progress) / 10.0;
                        progress_value = decimalProgress;
                        RatePercent.setText(""+decimalProgress);
                    }

                    CalculateAndSet(Integer.parseInt(DepositInput.getText().toString().trim()),
                            comp,
                            Integer.parseInt(TenureInput.getText().toString().trim()));


                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                RatePercent.removeTextChangedListener(textWatcher);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                RatePercent.addTextChangedListener(textWatcher);
            }
        });

        CompoundingOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 1:
                        comp = 1;
                        break;
                    case 2:
                        comp = 2;
                        break;
                    case 3:
                        comp = 3;
                        break;
                    case 4:
                        comp = 4;
                        break;
                }

                CalculateAndSet(Integer.parseInt(DepositInput.getText().toString().trim()),
                        comp,
                        Integer.parseInt(TenureInput.getText().toString().trim()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        /*
        Tenure Option Change, Changes From Monthly to Yearly & Vice Versa
         */

        TenureOptionsYearly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TenureOptionsYearly.setTextColor(getResources().getColor(R.color.primary_fd));
                TenureOptionsYearly.setTextSize(14);
                TenureOptionsMonthly.setTextColor(Color.parseColor("#000000"));
                TenureOptionsMonthly.setTextSize(10);
                mory = true;

                CalculateAndSet(Integer.parseInt(DepositInput.getText().toString().trim()),
                        comp,
                        Integer.parseInt(TenureInput.getText().toString().trim()));

            }
        });

        TenureOptionsMonthly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TenureOptionsMonthly.setTextColor(getResources().getColor(R.color.primary_fd));
                TenureOptionsMonthly.setTextSize(14);
                TenureOptionsYearly.setTextColor(Color.parseColor("#000000"));
                TenureOptionsYearly.setTextSize(10);
                mory = false;

                CalculateAndSet(Integer.parseInt(DepositInput.getText().toString().trim()),
                        comp,
                        Integer.parseInt(TenureInput.getText().toString().trim()));

            }
        });

        DepositInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals("")) {
                    CalculateAndSet(Integer.parseInt(s.toString().trim()),
                            comp,
                            Integer.parseInt(TenureInput.getText().toString().trim()));

                }
            }
        });

        TenureInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().equals("")){
                    CalculateAndSet(Integer.parseInt(DepositInput.getText().toString().trim()),
                            comp,
                            Integer.parseInt(s.toString().trim()));
                }
            }
        });

        RatePercent.addTextChangedListener(textWatcher);

        statsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Statistics.class);

                intent.putExtra("Amount", Float.parseFloat(DepositInput.getText().toString()));
                intent.putExtra("Rate", progress_value);
                intent.putExtra("Tenure", Integer.parseInt(TenureInput.getText().toString()));
                intent.putExtra("Compounding", comp);
                intent.putExtra("TenureType", returnforMory());
                intent.putExtra("Calculation", 2);
                intent.putExtra("MV", Float.parseFloat(MVAmount.getText().toString()));
                intent.putExtra("Interest", Float.parseFloat(InterestAmount.getText().toString()));

                startActivity(intent);
            }
        });

        return rootView;
    }

    public int returnforMory(){
        if(mory){
            return 1;
        } else {
            return 0;
        }
    }

    public void setFont(){

        Typeface poppins_bold = Typeface.createFromAsset(getContext().getAssets(), "fonts/poppinsb.ttf");
        MVtitle.setTypeface(poppins_bold);
        MVAmount.setTypeface(poppins_bold);
        InterestTitle.setTypeface(poppins_bold);
        InterestAmount.setTypeface(poppins_bold);
        TotalTitle.setTypeface(poppins_bold);
        TotalAmount.setTypeface(poppins_bold);
        DepositTitle.setTypeface(poppins_bold);
        RateTitle.setTypeface(poppins_bold);
        RatePercent.setTypeface(poppins_bold);
        TenureTitle.setTypeface(poppins_bold);
        TenureOptionsMonthly.setTypeface(poppins_bold);
        TenureOptionsYearly.setTypeface(poppins_bold);
        CompoundingTitle.setTypeface(poppins_bold);
        TenureInput.setTypeface(poppins_bold);
        statsButton.setTypeface(poppins_bold);
        DepositInput.setTypeface(poppins_bold);

    }


    public void CalculateAndSet(int amount, int comp, int tenure){

        double rate2 = (progress_value) / 100;
        int n = 1;
        switch (comp){
            case 1:
                n = 12;
                break;
            case 2:
                n = 4;
                break;
            case 3:
                n = 2;
                break;
            case 4:
                n = 1;
                break;
        }

        //converting months to years
        double ten=0;
        if(!mory){
            ten = ((double) tenure)/12;
        } else {
            ten = tenure;
        }

        double MV = Math.round(amount*(Math.pow(1+(rate2/n), n*ten)));
        MVAmount.setText(""+Math.round(MV));
        double interest = MV - amount;
        InterestAmount.setText(""+Math.round(interest));



    }
}