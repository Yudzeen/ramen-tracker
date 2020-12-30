package ebj.yujinkun.ramentracker.util;

import android.app.Activity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class SoftKeyboardUtils {

    public static void hideSoftKeyboard(@NonNull Activity activity) {
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);  // if null, create one to get a window token
        }
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        view.clearFocus();
    }

}
