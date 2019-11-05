package fingerprint.mammy.fingerprint;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class AppointmentFragment extends AppCompatActivity implements appointmentAdapter.ListItemClickListener, SwipeRefreshLayout.OnRefreshListener{

    private RecyclerView rAppointmentList;
    ArrayList<appointmentInfo> appointments;
    private static final int NUM_LIST_ITEMS = 100;
    private appointmentAdapter myAdapter;
    Class fragmentClass;
    public static Fragment fragment;
    String resultCalendar;
    private Toast mToast;
    private Bundle b;
    private String startDate;
    private String resultRequest;
    public static appointmentInfo choosenAppoinment;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_appointment);
        //new getAppointmentInfos().execute();
        final SwipeRefreshLayout swipe = findViewById(R.id.swiperefresh);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                //new HttpAsyncTask().execute("http://halisaha.appoint.online/api/FField_Calendar/GetFootballFields");

                new getAppointmentInfos().execute();
                new Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                swipe.setRefreshing(false);
                            }
                        }, 1500);
            }
        });


        rAppointmentList = findViewById(R.id.rv_appointment);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rAppointmentList.setLayoutManager(layoutManager);
        rAppointmentList.setHasFixedSize(true);
        myAdapter = new appointmentAdapter(NUM_LIST_ITEMS, this);
        rAppointmentList.setAdapter(myAdapter);

        new getAppointmentInfos().execute();
        final ProgressDialog progressDialog = new ProgressDialog(AppointmentFragment.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Onay Listeniz Hazırlanıyor...");
        progressDialog.show();
        new Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        progressDialog.dismiss();
                    }
                }, 1000);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        switch (itemId) {
            /*
             * When you click the reset menu item, we want to start all over
             * and display the pretty gradient again. There are a few similar
             * ways of doing this, with this one being the simplest of those
             * ways. (in our humble opinion)
             */
            //case R.id.action_refresh:
            //    myAdapter = new appointmentAdapter(NUM_LIST_ITEMS, this);
            //    rAppointmentList.setAdapter(myAdapter);
            //    return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        if (mToast != null) {
            mToast.cancel();
        }
        //String choosendate = b.getString("choosenDate");
        //calendarInfo choosenField = calendar.get(clickedItemIndex);
        String toastMessage = "Calendar";
        //mToast = Toast.makeText(getActivity(), toastMessage, Toast.LENGTH_LONG);

        //mToast.show();

    }

    @Override
    public void onRefresh() {

    } private class getAppointmentInfos extends AsyncTask<Void, Void, Void> implements appointmentAdapter.ListItemClickListener {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Toast.makeText(RecyclerView.this, "Json Data is downloading", Toast.LENGTH_LONG).show();

        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected Void doInBackground(Void... arg0) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            int myID = preferences.getInt("userID",0);
            HttpHandler sh = new HttpHandler();
            String url = "http://ojs.okmport.com/api/WebMethods/GetLoginRequestList";
            InputStream inputStream = null;
            String result = "";
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);
                String json = "";
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("ID", myID );
                //jsonObject.accumulate("OPERATION", "LOGIN" );
                json = jsonObject.toString();
                StringEntity se = new StringEntity(json);
                httpPost.setEntity(se);
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");
                HttpResponse httpResponse = httpclient.execute(httpPost);
                inputStream = httpResponse.getEntity().getContent();
                if(inputStream != null){
                    result = convertInputStreamToString(inputStream);
                    //Toast.makeText(getBaseContext(), result, Toast.LENGTH_LONG).show();
                    }
                else{
                    result = "Did not work!";
                    //Toast.makeText(getBaseContext(), result, Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                Log.d("InputStream", e.getLocalizedMessage());
            }
            //String jsonStr = sh.makeServiceCall(url);
            appointments = new ArrayList<appointmentInfo>();

            if (inputStream != null) {
                try {
                    JSONArray jsonarray = new JSONArray(result);

                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject c = jsonarray.getJSONObject(i);

                        int ID = c.getInt("ID");
                        int userID = c.getInt("userID");
                        String loginDate = c.getString("loginDate");
                        int state = c.getInt("state");
                        int appID = c.getInt("appID");
                        String appName = c.getString("appName");
                        int active = c.getInt("active");
                        appointments.add(new appointmentInfo(ID,userID,loginDate,state,appID,appName,active));
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            myAdapter = new appointmentAdapter(8, this);
            myAdapter.setAppointmentInfo(appointments);
            rAppointmentList.setAdapter(myAdapter);
        }


        private String convertInputStreamToString(InputStream inputStream) throws IOException {
            BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
            String line = "";
            String result = "";
            while((line = bufferedReader.readLine()) != null)
                result += line;

            inputStream.close();
            return result;

        }



        @Override
        public void onListItemClick(int clickedItemIndex) {
            if (mToast != null) {
                mToast.cancel();
            }


            choosenAppoinment = appointments.get(clickedItemIndex);
            appointmentInfo appointmentInfo = new appointmentInfo(choosenAppoinment);
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(AppointmentFragment.this, android.R.style.Theme_DeviceDefault_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(AppointmentFragment.this);
            }
            builder.setTitle("Uygulama onaylama isteği")
                    .setMessage("Onay vermek istediğinizden emin misiniz ?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //new FieldCalendarFragment.HttpAsyncTask2().execute("http://halisaha.appoint.online/api/Calendar/CreateNewCalendar");
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivityForResult(intent, 0);
                            overridePendingTransition(R.anim.animation_totheleft, R.anim.animation_totheright);
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

        }

    }


}
