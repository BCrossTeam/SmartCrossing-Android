package com.futurologeek.smartcrossing;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextValidator implements TextWatcher {
    public static class ValidText {
        public String text = "";
        public Boolean valid = false;

        public ValidText () {}

        public ValidText (String text, Boolean valid) {
            this.text = text;
            this.valid = valid;
        }
    }

    public enum RegexMode {
        None(false),
        NotContains(false),
        Equals(true);

        private final Boolean value;
        RegexMode(Boolean value){
            this.value = value;
        }
        private Boolean value() { return value; }
    }

    private EditText editText = null;
    private ValidText input = null;
    private String regex = null;
    private RegexMode regexMode = null;
    private int minLen = 0;
    private int maxLen = 0;

    public TextValidator() {}

    public TextValidator(EditText editText, ValidText input, String regex, RegexMode regexMode, int minLen, int maxLen) {
        this.editText = editText;
        this.input = input;
        this.regex = regex;
        this.regexMode = regexMode;
        this.minLen = minLen;
        this.maxLen = maxLen;
    }

    public void validateInput(EditText editText, ValidText input, String regex, RegexMode regexMode, int minLen, int maxLen) {
        input.valid = validate(input, regex, regexMode, minLen, maxLen);
    }

    @Override
    public void afterTextChanged(Editable s) {
        input.text = editText.getText().toString();
        validateInput(this.editText, this.input, this.regex, this.regexMode, this.minLen, this.maxLen);
    }

    @Override
    final public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    final public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    public static boolean validate(ValidText input, String regex, RegexMode regexMode, int minLen, int maxLen){
        if(regexMode == RegexMode.None){
            return (input.text.length() >= minLen && input.text.length() <= maxLen);
        } else {
            Pattern validatePattern = Pattern.compile(regex);
            Matcher validateMatcher = validatePattern.matcher(input.text);
            return (validateMatcher.find() == regexMode.value()) & (input.text.length() >= minLen && input.text.length() <= maxLen);
        }
    }

    public static boolean validate(String input, String regex, RegexMode regexMode, int minLen, int maxLen){
        if(regexMode == RegexMode.None){
            return (input.length() >= minLen && input.length() <= maxLen);
        } else {
            Pattern validatePattern = Pattern.compile(regex);
            Matcher validateMatcher = validatePattern.matcher(input);
            return (validateMatcher.find() == regexMode.value()) & (input.length() >= minLen && input.length() <= maxLen);
        }
    }
}


