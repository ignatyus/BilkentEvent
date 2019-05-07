package com.example.bilkentevent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;

public class CalendarActivity extends AppCompatActivity{
    CalendarView myCalender;
    Event myEvent;
    TextView text;
    Date date;
    private Button mybutton;
    int day;
    int thismonth;
    int thisyear;



    EventBox box;
    EventBox thatDayBox;
    private FirebaseAuth mAuth;
    private DatabaseReference userDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        myCalender = findViewById(R.id.calender);
        text = findViewById(R.id.textView8);
        mybutton = (Button) findViewById(R.id.button3);
        //final Intent i =new Intent(this ,Main2Activity.class);
        final Intent i = new Intent(CalendarActivity.this,AddPersonalEvent.class);
        box = new EventBox();
        thatDayBox = new EventBox();
        getEvents();

        myCalender.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth)
            {
                date = new Date(dayOfMonth,month+1,year);
                i.putExtra("day",dayOfMonth);
                i.putExtra("month",month);
                i.putExtra("year",year);
                for(int i = 0 ; i < box.getSize() ; i++){
                    Event checker = box.getEvent(i);
                    Date eventDay = checker.getDayOfEvent();
                    System.out.println(eventDay.getDay() + " " + eventDay.getMonth() + " " + eventDay.getYear());
                    if(eventDay.getDay()==dayOfMonth && eventDay.getMonth()==(month+1) && eventDay.getYear()==year) {
                        thatDayBox.addEvent(checker);
                    }
                }
                if(thatDayBox.getSize()!=0) {
                    Event e = thatDayBox.getEvent(0);
                    if(e!=null)
                        text.setText(e.getTopic());
                }
            }
        });

        mybutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (CalendarActivity.this , AddPersonalEvent.class);
                startActivity(intent);
                finish();
                return;
            }
        });
    }


    public void getEvents(){
        mAuth = FirebaseAuth.getInstance();
        String currentUID = mAuth.getUid();

        final DatabaseReference getter = FirebaseDatabase.getInstance().getReference().child("Users").child("Person").child(currentUID).child("PersonalEvents");
        getter.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {


                            HashMap<String, Object> datas = (HashMap<String, Object>) dataSnapshot.getValue();
                            if(datas==null)
                                return;
                            String topic = (String)datas.get("Topic");
                            String day = (String)datas.get("Day");
                            String month = (String)datas.get("Month");
                            String year = (String)datas.get("Year");
                            String hour = (String)datas.get("Hour");
                            String minute = (String)datas.get("Minute");

                            if(isPast(day,month,year) == false){
                                DatabaseReference r = dataSnapshot.getRef();
                                r.child("Passed").setValue(true);
                            }

                                int dayOfEvent = Integer.parseInt(day);
                                int monthOfEvent = Integer.parseInt(month);
                                int yearOfEvent = Integer.parseInt(year);
                                int hourOfEvent = Integer.parseInt(hour);
                                int minuteOfEvent = Integer.parseInt(minute);
                                Date date = new Date(dayOfEvent,monthOfEvent,yearOfEvent);
                                Time time = new Time(hourOfEvent,minuteOfEvent);
                                PersonalEvent pers = new PersonalEvent(date,time,time,topic);
                                box.addEvent(pers);

                        }


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final DatabaseReference getterC = FirebaseDatabase.getInstance().getReference().child("Users").child("Person").child(currentUID).child("Connections");
        getterC.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if (dataSnapshot.exists()) {

                    final String clubID = dataSnapshot.getKey();
                    for (DataSnapshot childSnapshot : dataSnapshot.child("Attend").getChildren()) {

                        if(childSnapshot.exists()) {

                            String eventID = childSnapshot.getKey();
                            DatabaseReference event = FirebaseDatabase.getInstance().getReference().child("Users").child("Clubs").child(clubID).child("Events").child(eventID);
                            event.child("Profile").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {
                                    HashMap<String, Object> datas = (HashMap<String, Object>) snapshot.getValue();
                                    if (datas == null)
                                        return;
                                    System.out.println(snapshot.toString());
                                    String name = (String) datas.get("Club Name");
                                    String id = (String) datas.get("Event Email");
                                    String day = (String) datas.get("Day");
                                    String month = (String) datas.get("Month");
                                    String year = (String) datas.get("Year");
                                    String topic = (String) datas.get("Topic");
                                    String location = (String) datas.get("Location");

                                    if (isPast(day, month, year) == false) {
                                        DatabaseReference r = snapshot.getRef();
                                        r.child("Profile").child("Passed").setValue(true);
                                    }
                                        ClubEvent temp = new ClubEvent(new Date(Integer.parseInt(day), Integer.parseInt(month), Integer.parseInt(year)), new Time(16, 00), new Time(18, 00), topic, clubID, snapshot.getKey(), location,(int) snapshot.child("Connections").child("Attend").getChildrenCount());
                                        box.addEvent(temp);


                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                        }

                    }

                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private boolean isPast(String day, String month, String year) {
        int iDay = Integer.parseInt(day);
        int iMonth = Integer.parseInt(month);
        int iYear = Integer.parseInt(year);

        int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        if(currentYear > iYear)
            return false;
        else if(currentYear == iYear)   {
            if(currentMonth > iMonth)   {
                return false;
            }
            else if(currentMonth == iMonth) {
                if(currentDay>iDay) {
                    return false;
                }
                else if(currentDay<= iDay)
                    return true;
            }
            else
                return true;

        }
        else
            return true;
        return true;
    }
}

