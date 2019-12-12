package com.android.onehuman.secretsantasms.filter;

import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.Gravity;
import android.widget.Toast;

import com.android.onehuman.secretsantasms.R;

public class EmojiExcludeFilter implements InputFilter {

    private Context context;

    public EmojiExcludeFilter(Context c) {
        this.context=c;
    }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            for (int i = start; i < end; i++) {
                int type = Character.getType(source.charAt(i));
                if (type == Character.SURROGATE || type == Character.OTHER_SYMBOL) {
                    Toast noEmojiToast=Toast.makeText(context, context.getResources().getString(R.string.edit_validation_emojis), Toast.LENGTH_SHORT);
                    noEmojiToast.setGravity(Gravity.CENTER, 0, 0);
                    noEmojiToast.show();
                    return "";
                }
            }
            return null;
        }

}
