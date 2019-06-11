package com.example.crypt;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.nio.charset.StandardCharsets;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.text_plain)
    EditText plainText;

    @BindView(R.id.text_key)
    EditText keyText;

    @BindView(R.id.hint_plain)
    TextView hintPlain;

    @BindView(R.id.result_text)
    TextView resultText;

    @BindView(R.id.generate_result)
    Button generateResult;

    @BindView(R.id.encrypt_text)
    TextView encrypt;

    @BindView(R.id.decrypt_text)
    TextView decrypt;

    @BindView(R.id.cipher_mode)
    RadioGroup chosenCipher;

    //IV for AES. has to be 16 bytes
    private static final String IV = "LarasRizkyKripto";

    //IV for 3DES. has to be 8 bytes
    private static final String IV2 = "LarasRiz";

    //Marker for encrypt/decrypt/ true if encrypt
    private boolean encryptMode = true;

    //Marker for chosen mode. true if aes is chosen
    private boolean aesMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        //Listener for Encrypt / Decrypt
        encrypt.setOnClickListener((View v) ->{
            setEncryptMode(true);
            Toast alert = Toast.makeText(getApplicationContext(), R.string.encrypt_mode, Toast.LENGTH_SHORT);
            alert.show();
            changeTextStyle();
        });

        decrypt.setOnClickListener((View v) ->{
            setEncryptMode(false);
            Toast alert = Toast.makeText(getApplicationContext(), R.string.decrypt_mode, Toast.LENGTH_SHORT);
            alert.show();
            changeTextStyle();
        });

        //Listener for Generate Button
        generateResult.setOnClickListener((View v) ->{
            //marker whether key valid or not
            boolean canGenerate = false;

            //assign aesMode. True for AES, False for 3DES
            aesMode = chosenCipher.getCheckedRadioButtonId() == R.id.cipher_mode_aes;

            //for AES, key must be 16/24 bytes, while for 3DES key must be 8 bytes
            if (aesMode){
                if (keyText.getText().toString().length() != 16 && keyText.getText().toString().length() != 24){
                    keyText.setError("Key must be 16 or 24 character!");
                } else canGenerate = true;
            } else {
                if (keyText.getText().toString().length() != 8){
                    keyText.setError("Key must be 8 character!");
                } else canGenerate = true;
            }

            if(canGenerate) {
                //convert textView to String
                String text = plainText.getText().toString();
                String keytext = keyText.getText().toString();

                //initiate string result
                String cipherResult = null;

                try {
                    if (isEncryptMode()){
                        cipherResult = encrypt(text, keytext, aesMode);
                    } else{
                        cipherResult = decrypt(text, keytext, aesMode);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                resultText.setText(cipherResult);
            }
        });
    }

    //getter setter for encryptMode
    public boolean isEncryptMode() {
        return encryptMode;
    }

    public void setEncryptMode(boolean encryptMode) {
        this.encryptMode = encryptMode;
    }

    //change title style Encrypt Decrypt
    public void changeTextStyle(){
        if (isEncryptMode()){
            encrypt.setTypeface(Typeface.DEFAULT_BOLD);
            decrypt.setTypeface(Typeface.DEFAULT);
            hintPlain.setText(R.string.insert_plain_text);
            plainText.setHint(R.string.insert_plain_text_here);
            generateResult.setText(R.string.encrypt);
            encrypt.setBackgroundColor(0x44000000);
            decrypt.setBackgroundColor(0x3D3D3d);
        } else {
            encrypt.setTypeface(Typeface.DEFAULT);
            decrypt.setTypeface(Typeface.DEFAULT_BOLD);
            hintPlain.setText(R.string.insert_cipher_text);
            plainText.setHint(R.string.insert_cipher_text_here);
            generateResult.setText(R.string.decrypt);
            encrypt.setBackgroundColor(0x3D3D3d);
            decrypt.setBackgroundColor(0x44000000);
        }
    }

    //function for encrypt. Return the encrypted text
    public static String encrypt(String strToEncrypt, String secret, boolean aesMode)
    {
        try
        {
            //Initiation
            IvParameterSpec iv;
            SecretKeySpec secretKeySpec;
            Cipher cipher;

            //Initiation depend on AES/3DES
            if (aesMode){
                iv = new IvParameterSpec(IV.getBytes(StandardCharsets.UTF_8));
                secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "AES");
                cipher = Cipher.getInstance("AES/OFB/PKCS5Padding");
            } else {
                iv = new IvParameterSpec(IV2.getBytes(StandardCharsets.UTF_8));
                secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "DESede");
                cipher = Cipher.getInstance("DES/OFB/PKCS5Padding");
            }

            //encrypting..
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, iv);

            byte[] encrypted = cipher.doFinal(strToEncrypt.getBytes());
            return Base64.encodeToString(encrypted, Base64.DEFAULT);

        }
        catch (Exception e)
        {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }

    public static String decrypt(String strToDecrypt, String secret, boolean aesMode)
    {
        try
        {
            //Initiation
            IvParameterSpec iv;
            SecretKeySpec secretKeySpec;
            Cipher cipher;

            //Initiation depend on AES/3DES
            if (aesMode){
                iv = new IvParameterSpec(IV.getBytes(StandardCharsets.UTF_8));
                secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "AES");
                cipher = Cipher.getInstance("AES/OFB/PKCS5Padding");
            } else {
                iv = new IvParameterSpec(IV2.getBytes(StandardCharsets.UTF_8));
                secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "DESede");
                cipher = Cipher.getInstance("DES/OFB/PKCS5Padding");
            }

            //decrypting..
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, iv);

            byte[] original = cipher.doFinal(Base64.decode(strToDecrypt, Base64.DEFAULT));
            return new String(original);
        }
        catch (Exception e)
        {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }
}
