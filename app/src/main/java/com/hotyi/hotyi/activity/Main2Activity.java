package com.hotyi.hotyi.activity;

import android.os.Bundle;
import android.app.Activity;
import android.widget.TextView;

import com.hotyi.hotyi.R;
import com.hotyi.hotyi.utils.Base64;
import com.hotyi.hotyi.utils.RSAUtil;

import org.w3c.dom.Text;

public class Main2Activity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        TextView textView1 = (TextView)findViewById(R.id.text1);
        TextView textView2 = (TextView)findViewById(R.id.text2);
        TextView textView3 = (TextView)findViewById(R.id.text3);
        TextView textView4 = (TextView)findViewById(R.id.text4);
        String str = "123456";
        try{
            String rsa_str = RSAUtil.encryptByPublicKey(str);
            textView1.setText(str);
            textView2.setText(rsa_str);
            textView3.setText(RSAUtil.decryptByPrivateKey(rsa_str));
        }catch (Exception e){
            e.toString();
        }
    }

}
