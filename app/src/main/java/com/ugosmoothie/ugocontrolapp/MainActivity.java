package com.ugosmoothie.ugocontrolapp;

import android.graphics.drawable.ShapeDrawable;
import android.support.annotation.DrawableRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ToggleButton;

import com.ugosmoothie.ugocontrolapp.USB_COMM.ArduinoComm;

public class MainActivity extends AppCompatActivity {
    private ArduinoComm myarduinoComm;
    ImageView indicator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myarduinoComm = new ArduinoComm(this);
        final Button blend = (Button) findViewById((R.id.blendbutton));
        final Button reblend = (Button) findViewById((R.id.reblendbutton));
        final Button clean = (Button) findViewById((R.id.cleanbutton));
        final Button initialize = (Button) findViewById((R.id.initializebutton));
        final Button stop = (Button) findViewById((R.id.stopbutton));

        final ToggleButton moveup = (ToggleButton) findViewById((R.id.moveupbutton));
        final ToggleButton movedown = (ToggleButton) findViewById((R.id.movedownbutton));
        final ToggleButton jogtop = (ToggleButton) findViewById((R.id.jogtopbutton));
        final ToggleButton jogbottom = (ToggleButton) findViewById((R.id.jogbottombutton));
        final ToggleButton disablekeypad = (ToggleButton) findViewById((R.id.disablekeypadbutton));


        final Button connect = (Button) findViewById((R.id.connectbutton));
        indicator = (ImageView) findViewById((R.id.redbutton));
        indicator.setImageResource(R.drawable.greenindicator);

        blend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickblend();
            }
        });
        reblend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickreblend();
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickstop();
            }
        });
        initialize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickinitialize();
            }
        });
        clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickclean();
            }
        });
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickconnect();
            }
        });


        moveup.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    clickmoveup();
                    moveup.setBackgroundDrawable(getResources().getDrawable(R.drawable.shadebuttonshape));
                } else {
                    clickmoveup();
                    moveup.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttonshape));
                }
            }
        });

        movedown.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    clickmovedown();
                    movedown.setBackgroundDrawable(getResources().getDrawable(R.drawable.shadebuttonshape));
                } else {
                    clickmovedown();
                    movedown.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttonshape));
                }
            }
        });


        disablekeypad.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    clickdisablekeypad();
                    disablekeypad.setBackgroundDrawable(getResources().getDrawable(R.drawable.shadebuttonshape));
                } else {
                    clickdisablekeypad();
                    disablekeypad.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttonshape));
                }
            }
        });



        jogtop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    clickjogtop();
                    jogtop.setBackgroundDrawable(getResources().getDrawable(R.drawable.shadebuttonshape));
                } else {
                    clickjogtop();
                    jogtop.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttonshape));
                }
            }
        });

        jogbottom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    clickjogbottom();
                    jogbottom.setBackgroundDrawable(getResources().getDrawable(R.drawable.shadebuttonshape));
                } else {
                    clickjogbottom();
                    jogbottom.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttonshape));
                }
            }
        });





    }


    void clickblend (){
        byte AutoCycle[] = new byte [1];
        myarduinoComm.WriteMessageToArduino((short) ArduinoComm.ArduinoMessageId.AutoCycle.getValue(), AutoCycle, (short)AutoCycle.length);
    }

    void clickclean(){
        byte Sanitize[] = new byte [1];
        myarduinoComm.WriteMessageToArduino((short) ArduinoComm.ArduinoMessageId.Sanitize.getValue(),Sanitize, (short)Sanitize.length);
    }

    void clickstop(){
        byte Stop[] = new byte [1];
        myarduinoComm.WriteMessageToArduino((short) ArduinoComm.ArduinoMessageId.Stop.getValue(),Stop, (short)Stop.length);
    }

    void clickinitialize (){
        byte Initialize[] = new byte [1];
        myarduinoComm.WriteMessageToArduino((short) ArduinoComm.ArduinoMessageId.Initialize.getValue(),Initialize, (short)Initialize.length);
    }

    void clickreblend (){
        byte Reblend[] = new byte [1];
        myarduinoComm.WriteMessageToArduino((short) ArduinoComm.ArduinoMessageId.Reblend.getValue(),Reblend, (short)Reblend.length);
    }


    void clickmoveup (){
        byte Moveup[] = new byte [1];
        myarduinoComm.WriteMessageToArduino((short) ArduinoComm.ArduinoMessageId.MoveUp.getValue(),Moveup, (short)Moveup.length);

    }
    void clickmovedown (){
        byte Movedown[] = new byte [1];
        myarduinoComm.WriteMessageToArduino((short) ArduinoComm.ArduinoMessageId.MoveDown.getValue(),Movedown, (short)Movedown.length);

    }
    void clickjogtop (){
        byte Jogtop[] = new byte [1];
        myarduinoComm.WriteMessageToArduino((short) ArduinoComm.ArduinoMessageId.JogTop.getValue(),Jogtop, (short)Jogtop.length);

    }
    void clickjogbottom (){
        byte Jogbottom[] = new byte [1];
        myarduinoComm.WriteMessageToArduino((short) ArduinoComm.ArduinoMessageId.JogBottom.getValue(),Jogbottom, (short)Jogbottom.length);

    }

    void clickconnect () {
        myarduinoComm.RefreshDeviceList();
    }


    void clickdisablekeypad (){
        byte Disablekeypad[] = new byte [1];
        myarduinoComm.WriteMessageToArduino((short) ArduinoComm.ArduinoMessageId.DisableKeyPad.getValue(),Disablekeypad, (short)Disablekeypad.length);

    }

    boolean isred = false;

    public void ToggleIndication(){
        if (isred){
            indicator.setImageResource(R.drawable.greenindicator);
        }
        else{
            indicator.setImageResource(R.drawable.redindicator);
        }
        isred = !isred;
    }

}
