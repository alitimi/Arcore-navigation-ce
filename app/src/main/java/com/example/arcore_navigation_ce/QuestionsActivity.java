package com.example.arcore_navigation_ce;

import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class QuestionsActivity extends AppCompatActivity {

    TextView text;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        Window window = this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        text = findViewById(R.id.layout1);
        text.setText("مسیریابی دانشگاه امیرکبیر" + "\n");
        text.append("1.\tدر صفحه اصلی برنامه گزینه مسیریابی دانشگاه امیرکبیر را انتخاب کنید. (از روشن بودن GPS و DATA تلفن همراه خود اطمینان حاصل کنید.)\n" +
                "\n" +
                "2.\tدر صفحه باز شده مکان مورد نظر خود را انتخاب کنید.\n" +
                "\n" +
                "3.\tبرنامه شما را تا مقصد راهنمایی خواهد کرد.\n" +
                "\n" +
                "مسیریابی دانشکده مهندسی کامپیوتر\n" +
                "        \n" +
                "1.\tدر صفحه اصلی برنامه گزینه مسیریابی دانشکده کامپیوتر را انتخاب کنید.\n" +
                "        \n" +
                "2.\tدر صفحه باز شده مکان مورد نظر خود را انتخاب کنید.\n" +
                "        \n" +
                "3.\tحال نزدیک\u200Cترین QRCode را که مشاهده می\u200Cکنید اسکن کنید.\n" +
                "        \n" +
                "4.\tبرنامه شروع به مسیریابی می\u200Cکند و تا مقصد شما را راهنمایی می\u200Cکند.");

    }
}
