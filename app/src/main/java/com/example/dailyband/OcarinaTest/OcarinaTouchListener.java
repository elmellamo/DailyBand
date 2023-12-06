package com.example.dailyband.OcarinaTest;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.dailyband.R;

import java.util.Arrays;

public class OcarinaTouchListener implements View.OnTouchListener {

    private static boolean[] buttons = new boolean[12];
    private static String currentOcarina;

    public OcarinaTouchListener(String c) {
        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = false;
        }
        currentOcarina = c;
    }
// https://gist.github.com/slightfoot/6330866

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        int button = getButtonId(v.getId());

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            setButtons(button, true);

            //PlayAudio.start();  // 소리 생성 시작
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            setButtons(button, false);
        }
        return true;
    }

    public int getButtonId(int i) {
        final int button1 = R.id.button1;
        final int button2 = R.id.button2;
        final int button3 = R.id.button3;

        final int button4 = R.id.button4;
        final int button5 = R.id.button5;
        final int button6 = R.id.button6;
        final int button7 = R.id.button7;
        final int button8 = R.id.button8;

        final int button11 = R.id.button11;
        final int button12 = R.id.button12;

        if (i == button1) {
            return 1;
        } else if (i == button2) {
            return 2;
        } else if (i == button3) {
            return 3;
        } else if (i == button4) {
            return 4;
        } else if (i == button5) {
            return 5;
        } else if (i == button6) {
            return 6;
        } else if (i == button7) {
            return 7;
        } else if (i == button8) {
            return 8;
        } else if (i == button11) {
            return 11;
        } else if (i == button12) {
            return 12;
        } else {
            return 0;
        }
    }


    public static boolean[] getButtons() {
        return buttons;
    }

    public void setButtons(int number, boolean b) {
        if (number != 0) {
            buttons[number - 1] = b;
        }
    }

    static public Note getNote() {

        if (currentOcarina.equals("4Hole")) {
            if (Arrays.equals(buttons, new boolean[] {true, true, true, true, false, false, false, false, false, false, false, false})) {
                return Note.C_5;
            }

            if (Arrays.equals(buttons, new boolean[] {false, true, true, true, false, false, false, false, false, false, false, false})) {
                return Note.D_5;
            }

            if (Arrays.equals(buttons, new boolean[] {true, false, true, true, false, false, false, false, false, false, false, false})) {
                return Note.E_5;
            }

            if (Arrays.equals(buttons, new boolean[] {false, false, true, true, false, false, false, false, false, false, false, false})) {
                return Note.F_5;
            }

            if (Arrays.equals(buttons, new boolean[] {true, true, false, true, false, false, false, false, false, false, false, false})) {
                return Note.F_5_SHARP;
            }

            if (Arrays.equals(buttons, new boolean[] {false, true, false, true, false, false, false, false, false, false, false, false})) {
                return Note.G_5;
            }

            if (Arrays.equals(buttons, new boolean[] {true, false, false, true, false, false, false, false, false, false, false, false})) {
                return Note.G_5_SHARP;
            }

            if (Arrays.equals(buttons, new boolean[] {false, false, false, true, false, false, false, false, false, false, false, false})) {
                return Note.A_5;
            }

            if (Arrays.equals(buttons, new boolean[] {false, true, false, false, false, false, false, false, false, false, false, false})) {
                return Note.A_5_SHARP;
            }

            if (Arrays.equals(buttons, new boolean[] {true, false, false, false, false, false, false, false, false, false, false, false})) {
                return Note.B_5;
            }

            if (Arrays.equals(buttons, new boolean[] {false, false, false, false, false, false, false, false, false, false, false, false})) {
                return Note.NULL; //Should be C_6, need to fall through without baseline
            }

        }
        return Note.NULL;
    }

}
