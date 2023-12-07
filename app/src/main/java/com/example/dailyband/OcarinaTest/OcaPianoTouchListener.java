package com.example.dailyband.OcarinaTest;

import android.view.MotionEvent;
import android.view.View;

import com.example.dailyband.R;

import java.util.Arrays;

public class OcaPianoTouchListener implements View.OnTouchListener{
    private static int note;
    public OcaPianoTouchListener() {
    }

    @Override
    public boolean onTouch (View v, MotionEvent event) {
        note = getButtonId(v.getId());

        if (event.getAction() == MotionEvent.ACTION_UP) {
            note = 0;
        }
        return true;
    }

    public int getButtonId(int i) {
        if (i == R.id.oca_p13 || i==R.id.oca_ta4) {
            return 1;
        } else if (i == R.id.oca_b10) {
            return 2;
        } else if (i == R.id.oca_p14 || i==R.id.oca_tb4) {
            return 3;
        } else if (i == R.id.oca_p15|| i==R.id.oca_tc5) {
            return 4;
        } else if (i == R.id.oca_b11) {
            return 5;
        } else if (i == R.id.oca_p16 || i==R.id.oca_td5) {
            return 6;
        } else if (i == R.id.oca_b12) {
            return 7;
        } else if (i == R.id.oca_p17 || i == R.id.oca_te5) {
            return 8;
        } else if (i == R.id.oca_p18 || i==R.id.oca_tf5) {
            return 9;
        } else if (i == R.id.oca_b13) {
            return 10;
        } else if (i == R.id.oca_p19 || i == R.id.oca_tg5) {
            return 11;
        } else if (i == R.id.oca_b14) {
            return 12;
        } else if (i == R.id.oca_p20 || i==R.id.oca_ta5) {
            return 13;
        } else if (i == R.id.oca_b15) {
            return 14;
        } else if (i == R.id.oca_p21 || i==R.id.oca_tb5) {
            return 15;
        } else if (i == R.id.oca_p22|| i==R.id.oca_tc6) {
            return 16;
        } else if (i == R.id.oca_b16) {
            return 17;
        } else if (i == R.id.oca_p23 || i==R.id.oca_td6) {
            return 18;
        } else if (i == R.id.oca_b17) {
            return 19;
        } else if (i == R.id.oca_p24 || i == R.id.oca_te6) {
            return 20;
        } else {
            return 0;
        }
    }

    static public Note getNote() {
        if(note == 1){
            return Note.A_4;
        }else if(note == 2){
            return Note.A_4_SHARP;
        }else if(note ==3){
            return Note.B_4;
        }else if(note == 4){
            return Note.C_5;
        }else if(note == 5){
            return Note.C_5_SHARP;
        }else if(note == 6){
            return Note.D_5;
        }else if(note == 7){
            return Note.D_5_SHARP;
        }else if(note == 8){
            return Note.E_5;
        }else if(note == 9){
            return Note.F_5;
        }else if(note == 10){
            return Note.F_5_SHARP;
        }else if(note == 11){
            return Note.G_5;
        }else if(note == 12){
            return Note.G_5_SHARP;
        }else if(note == 13){
            return Note.A_5;
        }else if(note == 14){
            return Note.A_5_SHARP;
        }else if(note == 15){
            return Note.B_5;
        }else if(note == 16){
            return Note.C_6;
        }else if(note == 17){
            return Note.C_6_SHARP;
        }else if(note == 18){
            return Note.D_6;
        }else if(note == 19){
            return Note.D_6_SHARP;
        }else if(note == 20){
            return Note.E_6;
        }else if(note ==0){
            return Note.NULL;
        }

        return Note.NULL;
    }

}
