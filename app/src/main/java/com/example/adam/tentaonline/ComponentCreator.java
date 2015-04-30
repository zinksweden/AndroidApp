package com.example.adam.tentaonline;

import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Adam on 2015-04-26.
 */
public class ComponentCreator {

    public Button createButton(String txt, int txtColor, int bg, ActionBarActivity aba){
        Button button = new Button(aba);
        button.setText(txt);
        button.setTextColor(aba.getResources().getColor(txtColor));
        button.setBackgroundResource(bg);
        return button;
    }

    public EditText createEditTextBox(int questionId ,float weight, int id, int height, ActionBarActivity aba){
        final EditText codeBox = new EditText(aba);
        codeBox.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS |
                InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        codeBox.setSingleLine(false);
        codeBox.setGravity(Gravity.TOP | Gravity.LEFT);
        codeBox.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, height, weight));
        codeBox.setId(id + questionId);
        codeBox.setBackgroundResource(R.drawable.back_border);
        return codeBox;
    }

    public LinearLayout createQuestionTitle(String question, int textSize, ActionBarActivity aba){
        LinearLayout questionNumberLayout = new LinearLayout(aba);

        TextView questionView = new TextView(aba);
        LinearLayout.LayoutParams tees = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        questionView.setLayoutParams(tees);
        questionView.setGravity(Gravity.CENTER_HORIZONTAL);
        questionView.setText(" " + question + " ");
        questionView.setTextColor(Color.WHITE);
        questionView.setTextSize(textSize);
        questionView.setBackgroundColor(aba.getResources().getColor(R.color.darkblue));
        questionNumberLayout.addView(questionView);
        return questionNumberLayout;
    }

    public CheckBox[] createCheckboxes(ArrayList<String> options, ActionBarActivity aba){
        CheckBox[] result = new CheckBox[options.size()];
        for(int i=0;i<options.size();i++){
            final CheckBox checkBox = new CheckBox(aba);
            checkBox.setText(options.get(i));
            checkBox.setId(0 + i);
            result[i]=checkBox;
        }
        return result;
    }

    public RadioGroup createRadiobuttons(ArrayList<String> options,LinearLayout.LayoutParams radioParams,
                                   ActionBarActivity aba){
        RadioGroup radioGroup = new RadioGroup(aba);
        for(int i=0;i<options.size();i++){
            RadioButton radioButton = new RadioButton(aba);
            radioButton.setText(options.get(i));
            radioButton.setTextColor(aba.getResources().getColor(R.color.black));
            radioButton.setLayoutParams(radioParams);
            radioGroup.addView(radioButton);
        }
        return radioGroup;
    }



}
