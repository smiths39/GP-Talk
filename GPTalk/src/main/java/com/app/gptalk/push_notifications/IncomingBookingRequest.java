package com.app.gptalk.push_notifications;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.app.gptalk.R;

public class IncomingBookingRequest extends Activity {

     @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.gp_received_booking_request);
        Intent referenceIntent = getIntent();
        String referenceCode = referenceIntent.getExtras().getString("appointmentReference").toString();

         try {
             // gp = notification from general practitioner
             if (referenceCode.substring(0, 2).equals("gp")) {

                 // show change request from patient
                 Intent patientChangeRequest = new Intent("com.app.gptalk.homepage.patient.PATIENTRECEIVEDBOOKINGCANCEL");
                 patientChangeRequest.putExtra("appointmentReference", referenceCode);
                 startActivity(patientChangeRequest);
             }
             // pa = notification from patient
             else if (referenceCode.substring(0, 2).equals("pa")) {

                 // show change request from patient
                 Intent patientChangeRequest = new Intent("com.app.gptalk.homepage.gp.GPRECEIVEDBOOKINGCANCEL");
                 patientChangeRequest.putExtra("appointmentReference", referenceCode);
                 startActivity(patientChangeRequest);
             } else if (referenceCode.substring(0, 1).equals("p")) {

                // update booking status on patient side
                Intent patientBookingRequest = new Intent("com.app.gptalk.homepage.patient.PATIENTRECEIVEDBOOKINGREPLY");
                patientBookingRequest.putExtra("appointmentReference", referenceCode.substring(1, referenceCode.length()));
                startActivity(patientBookingRequest);
            } else {

               // show booking details for GP
                Intent gpBookingRequest = new Intent("com.app.gptalk.homepage.gp.GPRECEIVEDBOOKINGREQUEST");
                gpBookingRequest.putExtra("appointmentReference", referenceCode);
                startActivity(gpBookingRequest);
            }
         } catch(Exception e) {
             Toast.makeText(IncomingBookingRequest.this, "Error", Toast.LENGTH_LONG).show();
         }
         finish();
    }
}
