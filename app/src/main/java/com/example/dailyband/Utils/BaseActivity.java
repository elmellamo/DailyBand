package com.example.dailyband.Utils;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dailyband.R;

public abstract class BaseActivity extends AppCompatActivity {
    private TransitionMode transitionMode;

    // 생성자
    protected BaseActivity(TransitionMode transitionMode) {
        this.transitionMode = transitionMode;
    }

    protected BaseActivity() {
        this.transitionMode = TransitionMode.NONE;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // onInject 메서드 호출
        onInject(savedInstanceState);

        // onCreate에서 transition 애니메이션 설정
        switch (transitionMode) {
            case HORIZON:
                overridePendingTransition(R.anim.horizon_enter, R.anim.none);
                break;
            case VERTICAL:
                overridePendingTransition(R.anim.vertical_enter, R.anim.none);
                break;
            default:
                break;
        }
    }

    @Override
    public void finish() {
        super.finish();

        // finish에서 transition 애니메이션 설정
        switch (transitionMode) {
            case HORIZON:
                overridePendingTransition(R.anim.none, R.anim.horizon_exit);
                break;
            case VERTICAL:
                overridePendingTransition(R.anim.none, R.anim.vertical_exit);
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        // onBackPressed에서 transition 애니메이션 설정
        if (isFinishing()) {
            switch (transitionMode) {
                case HORIZON:
                    overridePendingTransition(R.anim.none, R.anim.horizon_exit);
                    break;
                case VERTICAL:
                    overridePendingTransition(R.anim.none, R.anim.vertical_exit);
                    break;
                default:
                    break;
            }
        }
    }

    // 추상 메서드
    protected abstract void onInject(Bundle savedInstanceState);

    // 열거형: transition 모드 정의
    public enum TransitionMode {
        NONE,
        HORIZON,
        VERTICAL
    }
}
