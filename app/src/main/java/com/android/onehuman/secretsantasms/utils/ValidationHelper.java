package com.android.onehuman.secretsantasms.utils;

import android.content.Context;
import android.widget.EditText;
import com.android.onehuman.secretsantasms.database.DBController;
import com.android.onehuman.secretsantasms.model.Group;
import com.android.onehuman.secretsantasms.model.Person;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

public class ValidationHelper {

    private DBController dbController;

    public ValidationHelper(Context c) {
        this.dbController = new DBController(c);
    }

    public boolean validateRequiredField(EditText editText, TextInputLayout errorInputLayout, String message) {
        String value = editText.getText().toString().trim();
        if (value.isEmpty()) {
            errorInputLayout.setError(message);
            return false;
        } else {
            errorInputLayout.setErrorEnabled(false);
        }
        return true;
    }

    public boolean validateDuplicateGroupNameField(Group group, TextInputLayout errorInputLayout, String message) {
        if (dbController.existGroupName(group)) {
            errorInputLayout.setError(message);
            return false;
        } else {
            errorInputLayout.setErrorEnabled(false);
        }
        return true;
    }

    public boolean validateDuplicatePersonNameField(Group group, Person person, TextInputLayout errorInputLayout, String message) {
        if (dbController.existPersonName(person, group)) {
            errorInputLayout.setError(message);
            return false;
        } else {
            errorInputLayout.setErrorEnabled(false);
        }
        return true;
    }

    public boolean validatePhoneNumber(String number, TextInputLayout errorInputLayout, String message) {
        if (!android.util.Patterns.PHONE.matcher(number).matches()) {
            errorInputLayout.setError(message);
            return false;
        } else {
            errorInputLayout.setErrorEnabled(false);
        }
        return true;
    }


    public boolean validateEmoji(EditText editText, TextInputLayout errorInputLayout, String message) {
        for (char ch: editText.getText().toString().toCharArray()) {
            int type = Character.getType(ch);
            if (type == Character.SURROGATE || type == Character.OTHER_SYMBOL) {
                errorInputLayout.setError(message);
                return false;
            }
        }
        return true;
    }

    public boolean validateChips(List<Person> allCandidates, List<Integer> selectedForbiddens, TextInputLayout errorInputLayout, String message) {
        if(allCandidates.size() !=0 && selectedForbiddens.size() == allCandidates.size()) {
            errorInputLayout.setError(message);
            return false;
        }
        return true;
    }


}