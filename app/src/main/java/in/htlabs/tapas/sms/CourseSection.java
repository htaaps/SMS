package in.htlabs.tapas.sms;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tapas on 4/30/2015.
 */
public class CourseSection extends Activity implements View.OnClickListener,AdapterView.OnItemSelectedListener {
    //views declaration
    private TextView cs_tv_selection;
    private Button cs_bt_select,cs_bt_send_selection,cs_bt_get_course,cs_bt_get_section;
    private Spinner cs_sp_course,cs_sp_section;

    //Array list to hold the course and section
    private ArrayList<CourseDetails> coursedetailsList;
    private ArrayList<SectionDetails> sectiondetailsList;

    //To check internet connection
    ConnectionDetector cd;


    //to find the id of the selected item
    private static int cid,sid;

    //to check the details has been fetched from server
    private boolean c_fetch,s_fetch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_section);
        this.joinViews();
    }

    private void joinViews(){

        cd = new ConnectionDetector(getApplicationContext());

        //Views initialization
        cs_tv_selection=(TextView)findViewById(R.id.cs_tv_selection);
        cs_sp_course=(Spinner)findViewById(R.id.cs_sp_course);
        cs_sp_section=(Spinner)findViewById(R.id.cs_sp_section);
        cs_bt_select=(Button)findViewById(R.id.cs_bt_select);
        cs_bt_send_selection=(Button)findViewById(R.id.cs_bt_send_selection);
        cs_bt_get_course=(Button)findViewById(R.id.cs_bt_get_course);
        cs_bt_get_section=(Button)findViewById(R.id.cs_bt_get_section);

        cid=sid=0;
        c_fetch=s_fetch=false;

        coursedetailsList = new ArrayList<CourseDetails>();
        sectiondetailsList = new ArrayList<SectionDetails>();

        cs_sp_course.setOnItemSelectedListener(this);
        cs_sp_section.setOnItemSelectedListener(this);

        //initializing the on click listeners
        cs_bt_select.setOnClickListener(this);
        cs_bt_send_selection.setOnClickListener(this);
        cs_bt_get_course.setOnClickListener(this);
        cs_bt_get_section.setOnClickListener(this);

    }
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.cs_bt_get_course:
                if(cd.isConnectingToInternet()) {
                    if(!c_fetch){
                        new getCourse().execute();
                    }
                    else{Toast.makeText(this, " Already fetched all the courses ", Toast.LENGTH_LONG).show();}
                }else { Toast.makeText(this, " No Internet connection pls connect and try again ", Toast.LENGTH_LONG).show();}
                break;

            case R.id.cs_bt_get_section:
                if(cd.isConnectingToInternet()) {
                    if(!s_fetch){
                        new getSection().execute();
                    }
                    else{Toast.makeText(this, " Already fetched all the sections ", Toast.LENGTH_LONG).show();}
                }else { Toast.makeText(this, " No Internet connection pls connect and try again ", Toast.LENGTH_LONG).show();}
                break;

            case R.id.cs_bt_select:
                //displaying the selected items
                String course_item="",section_item="",selected_item="";

                if(c_fetch && s_fetch){
                    cid=cs_sp_course.getSelectedItemPosition();
                    course_item=coursedetailsList.get(cid).getCourseName();

                    sid=cs_sp_section.getSelectedItemPosition();
                    section_item=sectiondetailsList.get(sid).getSectionName();

                    selected_item=course_item+" & "+section_item;
                    cs_tv_selection.setText(selected_item);
                }
                else{Toast.makeText(this, " First fetch course and section and then select ", Toast.LENGTH_LONG).show();}
                break;

            case R.id.cs_bt_send_selection:
                if(cs_tv_selection.getText().length()>0){
                    if(cd.isConnectingToInternet()) {
                        new sendCourseSection().execute();
                    }else
                    { Toast.makeText(this, " No Internet connection pls connect and try again ", Toast.LENGTH_LONG).show();}
                }else{Toast.makeText(this, " First select course and section and then send the pair ", Toast.LENGTH_LONG).show();}
                break;
        }
    }

    //asynchronous class to fetch all the courses
    class getCourse extends AsyncTask<String, String, String> {

        // Progress Dialog
        private ProgressDialog pDialog;

        // JSON parser class
        JSONParser jsonParser = new JSONParser();

        //testing on Emulator:
        private static final String COURSE_URL = "http://www.htlabs.in/tapas/sms/course.php";

        private static final String TAG_SUCCESS = "success";
        private static final String TAG_MESSAGE = "message";

        /**
         * Before starting background thread Show Progress Dialog
         * */
        boolean failure = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(CourseSection.this);
            pDialog.setMessage("Fetching the courses offered...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            // Check for success tag
            int success;
            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();

                Log.d("request!", "starting");

                // getting product details by making HTTP request
                JSONObject json = jsonParser.makeHttpRequest(COURSE_URL, "POST", params);

                // check your log for json response
                Log.d("Attempt to fetch the courses", json.toString());

                // json success tag
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    JSONArray jcourseDetails = json.getJSONArray("courses");
                    for (int i = 0; i < jcourseDetails.length(); i++) {
                        JSONObject jobj = (JSONObject) jcourseDetails.get(i);
                        CourseDetails cd= new CourseDetails(jobj.getString("course_id"),jobj.getString("course_name"),jobj.getString("contact_hours"));
                        coursedetailsList.add(cd);
                    }
                    Log.d("Login Successful!", json.toString());
                    return json.getString(TAG_MESSAGE);
                }else{
                    Log.d("Login Failure!", json.getString(TAG_MESSAGE));
                    return json.getString(TAG_MESSAGE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;

        }
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
            pDialog.dismiss();
            if (file_url != null){
                populateCourseSpinner();
            }
            c_fetch=true;
        }
    }
    //asynchronous class to fetch all the sections
    class getSection extends AsyncTask<String, String, String> {

        // Progress Dialog
        private ProgressDialog pDialog;

        // JSON parser class
        JSONParser jsonParser = new JSONParser();

        //testing on Emulator:
        private static final String COURSE_URL = "http://www.htlabs.in/tapas/sms/section.php";

        private static final String TAG_SUCCESS = "success";
        private static final String TAG_MESSAGE = "message";

        /**
         * Before starting background thread Show Progress Dialog
         * */
        boolean failure = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(CourseSection.this);
            pDialog.setMessage("Fetching all the sections...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            // Check for success tag
            int success;
            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();

                Log.d("request!", "starting");

                // getting product details by making HTTP request
                JSONObject json = jsonParser.makeHttpRequest(COURSE_URL, "POST", params);

                // check your log for json response
                Log.d("Attempt to fetch the sections", json.toString());

                // json success tag
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    JSONArray jcourseDetails = json.getJSONArray("sections");
                    for (int i = 0; i < jcourseDetails.length(); i++) {
                        JSONObject jobj = (JSONObject) jcourseDetails.get(i);
                        SectionDetails sd= new SectionDetails(jobj.getString("section_id"),jobj.getString("section_name"));
                        sectiondetailsList.add(sd);
                    }
                    Log.d("Login Successful!", json.toString());
                    return json.getString(TAG_MESSAGE);
                }else{
                    Log.d("Login Failure!", json.getString(TAG_MESSAGE));
                    return json.getString(TAG_MESSAGE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;

        }
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
            pDialog.dismiss();
            if (file_url != null){
                populateSectionSpinner();
            }
            s_fetch=true;
        }
    }
    //asynchronous class to send all the matches
    class sendCourseSection extends AsyncTask<String, String, String> {

        // Progress Dialog
        private ProgressDialog pDialog;

        // JSON parser class
        JSONParser jsonParser = new JSONParser();

        //testing on Emulator:
        private static final String COURSESECTION_URL = "http://www.htlabs.in/tapas/sms/coursesection.php";

        private static final String TAG_SUCCESS = "success";
        private static final String TAG_MESSAGE = "message";

        /**
         * Before starting background thread Show Progress Dialog
         * */
        boolean failure = false;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(CourseSection.this);
            pDialog.setMessage("Sending the Selection to Server...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            // Check for success tag
            int success;
            String cou_id=coursedetailsList.get(cid).getCourseId();
            String sec_id=sectiondetailsList.get(sid).getSectionId();
            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("course_id",cou_id));
                params.add(new BasicNameValuePair("section_id",sec_id));

                Log.d("request!", "starting");
                Log.d("the course id is ",cou_id);
                Log.d("the section id is ",sec_id);
                // getting product details by making HTTP request
                JSONObject json = jsonParser.makeHttpRequest(COURSESECTION_URL, "POST", params);

                // check your log for json response
                Log.d("Login attempt", json.toString());

                // json success tag
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    Log.d("Details Entered in database!", json.getString(TAG_MESSAGE));
                    return json.getString(TAG_MESSAGE);
                }else{
                    Log.d("Details Entry Failed!", json.getString(TAG_MESSAGE));
                    return json.getString(TAG_MESSAGE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;

        }
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
            pDialog.dismiss();
            if (file_url != null){
                Toast.makeText(CourseSection.this, file_url, Toast.LENGTH_LONG).show();
            }

        }

    }


    //loading the spinners with the data fetched from server
    private void populateSectionSpinner() {

        List<String> lables = new ArrayList<String>();

        for (int i = 0; i < sectiondetailsList.size(); i++) {
            lables.add(sectiondetailsList.get(i).getSectionName());
        }

        // Creating adapter for spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, lables);

        // Drop down layout style - list view with radio button
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        cs_sp_section.setAdapter(spinnerAdapter);
    }

    private void populateCourseSpinner() {

        List<String> lables = new ArrayList<String>();

        for (int i = 0; i < coursedetailsList.size(); i++) {
            lables.add(coursedetailsList.get(i).getCourseName());
        }

        // Creating adapter for spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, lables);

        // Drop down layout style - list view with radio button
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        cs_sp_course.setAdapter(spinnerAdapter);
    }



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}
