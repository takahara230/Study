package com.example.takas.study;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements OnParingListChangeListener, EasyPermissions.PermissionCallbacks {
    public CmDef.PAIR_SOURCE m_parSource;

    public static String PREF_REG_PLAYERS = "reg_players";  // ローカル登録
    public static String PREF_GOOGLE_PLAYERS = "google_players";  // GoogleSpreadから読み込んだデータのローカル保存データ
    public static String PREF_GOOGLE_SELECTTION = "google_selection"; //
    public static String PREF_LOCAL_SELECTTION = "local_selection"; //

    public static String PREF_MATCH_TABLE = "match_table";
    public static String PREF_PLAYER_INFO = "player_info";

    public static String PREF_MAKE_MODE = "make_mode";

    public static String MODE_INDEX = "mode_index";
    public static String MODE_USER_REG = "mode_user_reg";
    public static String MODE_GOOGLE = "mode_google";

    public static String KEY_NAME = "name";
    public static String KEY_SELECTION = "selection";
    public static String KEY_LEVEL = "level";

    ProgressDialog mProgress;
    MainActivity _activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, MatchTableFragment.newInstance(), MatchTableFragment.TAG);
            transaction.commit();
        }

        m_parSource = CmDef.PAIR_SOURCE.PAIR_SOURCE_INDEX;
        _activity = this;
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Calling Google Sheets API ...");

        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        /*
            MenuItem changeMenu = menu.findItem(R.id.action_select_change);
            changeMenu.setVisible(m_parSource != CmDef.PAIR_SOURCE.PAIR_SOURCE_INDEX);
*/
        /*
            MenuItem google = menu.findItem(R.id.action_google);
            google.setVisible(m_parSource != CmDef.PAIR_SOURCE.PAIR_SOURCE_GOOGLE_SPREAD);

            MenuItem reg = menu.findItem(R.id.action_reg_member);
            reg.setVisible(m_parSource != CmDef.PAIR_SOURCE.PAIR_SOURCE_LOCAL_REG);

            MenuItem index = menu.findItem(R.id.action_index);
            index.setVisible(m_parSource != CmDef.PAIR_SOURCE.PAIR_SOURCE_INDEX);
            */

        return true;
    }

    public void setMode(String mode){
        if(mode.equals(MainActivity.MODE_GOOGLE)){
            m_parSource = CmDef.PAIR_SOURCE.PAIR_SOURCE_GOOGLE_SPREAD;
        }else{
            m_parSource = CmDef.PAIR_SOURCE.PAIR_SOURCE_INDEX;
        }
    }

    public void changeButton()
    {
        if(m_parSource== CmDef.PAIR_SOURCE.PAIR_SOURCE_GOOGLE_SPREAD){
            readGoogleSpread(true);
        }else if(m_parSource == CmDef.PAIR_SOURCE.PAIR_SOURCE_LOCAL_REG){
            openRegNameDialog();
        }else if(m_parSource == CmDef.PAIR_SOURCE.PAIR_SOURCE_INDEX){
            SelectNumber();
        }
    }

    public void SelectNumber()
    {
        String title = getResources().getString(R.string.action_index);
        CommonDialogFragment newFragment = CommonDialogFragment.newInstance(title, m_number,REQUEST_SELECT_NUMBER);
        newFragment.show(getSupportFragmentManager(), CommonDialogFragment.TAG);
    }
    private  int m_number = 4;
    public void MembarOfNumber(Intent data)
    {
        m_number = CommonDialogFragment.getNumber(data);
        FragmentManager fragmentManager;
        fragmentManager = getSupportFragmentManager();
        MatchTableFragment contentFragment = (MatchTableFragment) fragmentManager.findFragmentByTag(MatchTableFragment.TAG);
        if (!(contentFragment == null
                || !contentFragment.isVisible())) {
            m_parSource = CmDef.PAIR_SOURCE.PAIR_SOURCE_INDEX;
            PrefSave(PREF_MAKE_MODE,MODE_INDEX);
            contentFragment.makeParIndex(true,m_number);
        }
    }


    /**
     * メニュー選択
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_google) {
            readGoogleSpread(m_parSource == CmDef.PAIR_SOURCE.PAIR_SOURCE_GOOGLE_SPREAD);
        }/* else if (id == R.id.action_select_change) {
            if(m_parSource== CmDef.PAIR_SOURCE.PAIR_SOURCE_GOOGLE_SPREAD){
                readGoogleSpread(true);
            }else if(m_parSource == CmDef.PAIR_SOURCE.PAIR_SOURCE_LOCAL_REG){
                openRegNameDialog();
            }
        }*/else if(id == R.id.action_reg_member) {
            openRegNameDialog();
        }else if(id == R.id.action_index) {
            SelectNumber();
        }else if(id == R.id.action_upload_google){
            uploadGameResults();
        } else if (id == R.id.action_settings) {

        }
        return super.onOptionsItemSelected(item);
    }

    public void openRegNameDialog()
    {
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        Gson gson = new Gson();
        ArrayList<HashMap<String,String>> players = gson.fromJson(pref.getString(MainActivity.PREF_REG_PLAYERS, ""), new TypeToken<ArrayList<HashMap<String,String>>>() {
        }.getType());
        if (players != null) {
            ArrayList<HashMap<String, String>> data = CmUtils.UserPrefJsonGet(this, MainActivity.PREF_LOCAL_SELECTTION, new TypeToken<ArrayList<HashMap<String, String>>>() {
            }.getType());
            if (data != null) {
                for (int index = 0; index < players.size(); index++) {
                    HashMap<String, String> p = players.get(index);
                    HashMap<String, String> s = null;
                    if (index < data.size()) {
                        s = data.get(index);
                    }
                    String selection = "1";
                    if (s != null) {
                        selection = s.get(MainActivity.KEY_SELECTION);
                    }
                    p.put(MainActivity.KEY_SELECTION, selection);
                }
            }
        }else{
            players = new ArrayList<>();
        }

        // DialogFragment を表示します
        SelectPlayerDialogFragment exampleDialogFragment = SelectPlayerDialogFragment.newInstance(false,players);
        exampleDialogFragment.show(getSupportFragmentManager(),
                SelectPlayerDialogFragment.class.getSimpleName());
    }

    public void readGoogleSpread(boolean currentGoogle)
    {
        SettingGoogleSpreadDialogFragment dialogFragment = SettingGoogleSpreadDialogFragment.newInstance(currentGoogle);
//        dialogFragment.setArguments(args);
//        FragmentActivity activity = (FragmentActivity) getContext();
        dialogFragment.show(getSupportFragmentManager(), getString(R.string.add_member));
    }

    /**
     * OnParingListChangeListener リスナー
     * メンバー確定
     * @param data
     * @param googleSpread true:google spread から false:ローカル登録
     */
    @Override
    public void onParingListChanged(ArrayList<HashMap<String, String>> data,boolean googleSpread) {
        FragmentManager fragmentManager;
        fragmentManager = getSupportFragmentManager();
        MatchTableFragment contentFragment = (MatchTableFragment) fragmentManager.findFragmentByTag(MatchTableFragment.TAG);
        if (!(contentFragment == null
                || !contentFragment.isVisible())) {

            SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
            Gson gson = new Gson();
            String key = googleSpread?PREF_GOOGLE_SELECTTION:PREF_LOCAL_SELECTTION;
            pref.edit().putString(key, gson.toJson(data)).commit();




            if(googleSpread) {
                m_parSource = CmDef.PAIR_SOURCE.PAIR_SOURCE_GOOGLE_SPREAD;
                PrefSave(PREF_MAKE_MODE,MODE_GOOGLE);
            } else {
                m_parSource = CmDef.PAIR_SOURCE.PAIR_SOURCE_LOCAL_REG;
                PrefSave(PREF_MAKE_MODE,MODE_USER_REG);
            }

            // 選択されたメンバーから組み合わせ作成
            contentFragment.makeFirstPar2(data,googleSpread,m_allClear);
        }
    }

    public void PrefSave(String key,Object obj){
        SharedPreferences pref = getPreferences(MODE_PRIVATE);
        Gson gson = new Gson();
        SharedPreferences.Editor e = pref.edit();
        e.putString(key, gson.toJson(obj)).commit();
        e.commit();
    }

    public Object PrefGet(String key, Type typeOfT)
    {
        SharedPreferences pref = getPreferences(MODE_PRIVATE);
        Gson gson = new Gson();
        return gson.fromJson(pref.getString(key, ""), typeOfT);
    }

    /**
     * google spread データを保存してから選択ダイアログ呼び出し。
     * @param players
     */
    public void SelectPlayers(ArrayList<HashMap<String, String>> players) {
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        Gson gson = new Gson();
        pref.edit().putString(PREF_GOOGLE_PLAYERS, gson.toJson(players)).commit();
        SelectPlayersSub(players);
    }

    public ArrayList<HashMap<String,String>> getSaveGoogleData() {
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        Gson gson = new Gson();
        ArrayList<HashMap<String, String>> data = gson.fromJson(pref.getString(PREF_GOOGLE_PLAYERS, ""), new TypeToken<ArrayList<HashMap<String, String>>>() {
        }.getType());
        return data;
    }

    /**
     * google soread から読み込んだデータから選択
     * @param players
     */
    public void SelectPlayersSub(ArrayList<HashMap<String, String>> players) {
        ArrayList<HashMap<String,String>> data = CmUtils.UserPrefJsonGet(this,PREF_GOOGLE_SELECTTION, new TypeToken<ArrayList<HashMap<String, String>>>() {
        }.getType());
        if(data!=null){
            for(int index=0;index<players.size();index++){
                HashMap<String,String> p = players.get(index);
                HashMap<String,String> s = null;
                if(index<data.size()) {
                    s = data.get(index);
                }
                String selection = "1";
                if(s!=null){
                    selection = s.get(KEY_SELECTION);
                }
                p.put(KEY_SELECTION,selection);
            }
        }

        SelectPlayerDialogFragment exampleDialogFragment = SelectPlayerDialogFragment.newInstance(true, players);
        exampleDialogFragment.show(getSupportFragmentManager(),
                SelectPlayerDialogFragment.class.getSimpleName());
    }

    boolean m_allClear;

    public void readFromGoogle(boolean reset,String spreadsheetId,String sheetName,boolean allClear) {
        m_allClear = allClear;

        SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();

        editor.putString(CmDef.PREF_SPREADSHEETID,spreadsheetId);
        editor.putString(CmDef.PREF_SHEETNAME,sheetName);

        editor.commit();

        getResultsFromApi(reset);
    }

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    static final int REQUEST_SELECT_NUMBER = 1004;

    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {SheetsScopes.SPREADSHEETS_READONLY};
    GoogleAccountCredential mCredential;


    /**
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     */
    public void getResultsFromApi(boolean reset) {
        if (!reset) {
            ArrayList<HashMap<String, String>> data = getSaveGoogleData();
            if (data != null) {
                SelectPlayersSub(data);
                return;
            }
        }
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (!isDeviceOnline()) {
            //mOutputText.setText("No network connection available.");
//            Log.d("","No network connection available.");
            String text = "No network connection available.";
            Toast.makeText(_activity, text, Toast.LENGTH_SHORT).show();

        } else {
            new MakeRequestTask(mCredential).execute();
        }
    }

    public void uploadGameResults()
    {
        if (!isGooglePlayServicesAvailable()) {
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount2();
        } else if (!isDeviceOnline()) {
            //mOutputText.setText("No network connection available.");
//            Log.d("","No network connection available.");
            String text = "No network connection available.";
            Toast.makeText(_activity, text, Toast.LENGTH_SHORT).show();

        } else {
            uploadGameResultsBody();
        }
    }

    void uploadGameResultsBody(){
        FragmentManager fragmentManager;
        fragmentManager = getSupportFragmentManager();
        MatchTableFragment contentFragment = (MatchTableFragment) fragmentManager.findFragmentByTag(MatchTableFragment.TAG);
        if (contentFragment != null && contentFragment.isVisible()) {
            ArrayList<String> data = contentFragment.getGameRes();
            if(data==null || data.size()<=0) return;
            new UploadResultsTask(mCredential).execute(data);
        }
    }

    /**
     * Attempts to set the account used with the API credentials. If an account
     * name was previously saved it will use that one; otherwise an account
     * picker dialog will be shown to the user. Note that the setting the
     * account to use with the credentials object requires the app to have the
     * GET_ACCOUNTS permission, which is requested here if it is not already
     * present. The AfterPermissionGranted annotation indicates that this
     * function will be rerun automatically whenever the GET_ACCOUNTS permission
     * is granted.
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi(true);
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount2() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                //getResultsFromApi(true);
                uploadGameResultsBody();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     *
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode  code indicating the result of the incoming
     *                    activity result.
     * @param data        Intent (containing result data) returned by incoming
     *                    activity result.
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    //               mOutputText.setText(
                    //                       "This app requires Google Play Services. Please install " +
                    //                               "Google Play Services on your device and relaunch this app.");
                } else {
                    getResultsFromApi(true);
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi(true);
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi(true);
                }
                break;
            case REQUEST_SELECT_NUMBER:
                if(resultCode == RESULT_OK){
                    MembarOfNumber(data);
                }
                break;
        }
    }

    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     *
     * @param requestCode  The request code passed in
     *                     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    /**
     * Callback for when a permission is granted using the EasyPermissions
     * library.
     *
     * @param requestCode The request code associated with the requested
     *                    permission
     * @param list        The requested permission list. Never null.
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Callback for when a permission is denied using the EasyPermissions
     * library.
     *
     * @param requestCode The request code associated with the requested
     *                    permission
     * @param list        The requested permission list. Never null.
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Checks whether the device currently has a network connection.
     *
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services APK is installed and up to date.
     *
     * @return true if Google Play Services is available and up to
     * date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }


    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     *
     * @param connectionStatusCode code describing the presence (or lack of)
     *                             Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                MainActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    /**
     * An asynchronous task that handles the Google Sheets API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class MakeRequestTask extends AsyncTask<Void, Void, ArrayList<HashMap<String, String>>> {
        private com.google.api.services.sheets.v4.Sheets mService = null;
        private Exception mLastError = null;

        MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.sheets.v4.Sheets.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Google Sheets API Android Quickstart")
                    .build();
        }

        /**
         * Background task to call Google Sheets API.
         *
         * @param params no parameters needed for this task.
         */
        @Override
        protected ArrayList<HashMap<String, String>> doInBackground(Void... params) {
            try {
                return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        /**
         * Fetch a list of names and majors of students in a sample spreadsheet:
         * https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
         * https://docs.google.com/spreadsheets/d/1jhz33Rp4ad-KwuLW4XbVa-M3DI_PMH7J6R8PiR0hP1I/edit
         *
         * @return List of names and majors
         * @throws IOException
         */
        private ArrayList<HashMap<String, String>> getDataFromApi() throws IOException {
            String spreadsheetId = getPreferences(Context.MODE_PRIVATE).getString(CmDef.PREF_SPREADSHEETID,null);
            String sheetName = getPreferences(Context.MODE_PRIVATE).getString(CmDef.PREF_SHEETNAME,null);

//            String spreadsheetId = "1jhz33Rp4ad-KwuLW4XbVa-M3DI_PMH7J6R8PiR0hP1I";
//            String range0 = "players!";
            String range0 = sheetName+"!";
            ArrayList<HashMap<String, String>> results = new ArrayList<>();

            int start = 1;
            int len = 10;
            for (; ; ) {
                String range = range0 + "A" + start + ":B" + (start + len-1);
                ValueRange response = this.mService.spreadsheets().values()
                        .get(spreadsheetId, range)
                        .execute();
                List<List<Object>> values = response.getValues();
                if (values != null) {
                    for (List row : values) {
                        String name = (String) row.get(0);
                        if (name.length() == 0) return results;
                        String res = (String) row.get(1);
                        HashMap<String, String> player = new HashMap<>();
                        player.put(KEY_NAME, name);
                        player.put(KEY_LEVEL, res);
                        results.add(player);
                    }
                } else {
                    break;
                }
                start += len;
            }
            return results;
        }


        @Override
        protected void onPreExecute() {
            //mOutputText.setText("");
            mProgress.show();
        }

        @Override
        protected void onPostExecute(ArrayList<HashMap<String, String>> output) {
            mProgress.hide();
            if (output == null || output.size() == 0) {
//                mOutputText.setText("No results returned.");
                Toast.makeText(_activity, "No results returned.", Toast.LENGTH_SHORT).show();
            } else {
                /*
                output.add(0, "Data retrieved using the Google Sheets API:");
                String text = TextUtils.join("\n", output);
                Toast.makeText(_activity,text , Toast.LENGTH_SHORT).show();
                */
                _activity.SelectPlayers(output);
            }
        }

        @Override
        protected void onCancelled() {
            mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            MainActivity.REQUEST_AUTHORIZATION);
                } else {
//                    mOutputText.setText("The following error occurred:\n"                            + mLastError.getMessage());
                    String text = "The following error occurred:\n" + mLastError.getMessage();
                    Toast.makeText(_activity, text, Toast.LENGTH_SHORT).show();
                }
            } else {
                //mOutputText.setText("Request cancelled.");
                String text = "Request cancelled.";
                Toast.makeText(_activity, text, Toast.LENGTH_SHORT).show();
            }
        }

    }


    /**
     * An asynchronous task that handles the Google Sheets API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class UploadResultsTask extends AsyncTask<ArrayList<String>, Void, Integer> {
        private com.google.api.services.sheets.v4.Sheets mService = null;
        private Exception mLastError = null;

        UploadResultsTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.sheets.v4.Sheets.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Google Sheets API Android Quickstart")
                    .build();
        }

        @Override
        protected Integer doInBackground(ArrayList<String>... value) {
            try {
                putDataFromApi(value[0]);
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return 0;
            }
            return 0;
        }


        @Override
        protected void onPreExecute() {
            //mOutputText.setText("");
            mProgress.show();
        }

        @Override
        protected void onPostExecute(Integer res) {
            mProgress.hide();
        }

        @Override
        protected void onCancelled() {
            mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            MainActivity.REQUEST_AUTHORIZATION);
                } else {
//                    mOutputText.setText("The following error occurred:\n"                            + mLastError.getMessage());
                    String text = "The following error occurred:\n" + mLastError.getMessage();
                    Toast.makeText(_activity, text, Toast.LENGTH_SHORT).show();
                }
            } else {
                //mOutputText.setText("Request cancelled.");
                String text = "Request cancelled.";
                Toast.makeText(_activity, text, Toast.LENGTH_SHORT).show();
            }
        }

        /**
         * データを書き込むメソッド
         *
         * @throws IOException
         */
        private void putDataFromApi(ArrayList<String> values) throws IOException {
            ArrayList<List<Object>> data = new ArrayList<>();
            for (String text:values
                 ) {
                List<Object> v = new ArrayList<>();
                v.add(text);
                data.add(v);
            }
            String spreadsheetId = getPreferences(Context.MODE_PRIVATE).getString(CmDef.PREF_SPREADSHEETID,null);
            String range = "シート2!A1:A" + (values.size() + 1);
            ValueRange valueRange = new ValueRange();
            valueRange.setValues(data);
            valueRange.setRange(range);
            this.mService.spreadsheets().values()
                    .update(spreadsheetId, range, valueRange)
                    .setValueInputOption("USER_ENTERED")
                    .execute();

/*
            String spreadsheetId = getPreferences(Context.MODE_PRIVATE).getString(CmDef.PREF_SPREADSHEETID,null);
            String range = "シート1!A2:D2";
            ValueRange valueRange = new ValueRange();
            List row = new ArrayList<>();
            List col = Arrays.asList("This", "is", "test", "test");
            row.add(col);
            valueRange.setValues(row);
            valueRange.setRange(range);
            this.mService.spreadsheets().values()
                    .update(spreadsheetId, range, valueRange)
                    .setValueInputOption("USER_ENTERED")
                    .execute();
            */
        }
    }
}
