package com.l2f.vitheakids;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.l2f.vitheakids.rest.FetchChildLogin;

import butterknife.ButterKnife;
import butterknife.BindView;

import com.l2f.vitheakids.util.ConnectionDetector;
import com.l2f.vitheakids.util.TaskListener;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;

/**
 * Updated by Soraia Meneses Alarcão on 21/07/2017
 */

public class LoginActivity extends AppCompatActivity implements TaskListener,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int PERMISSION_REQUEST_EXTERNAL_STORAGE = 0;

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private ProgressDialog progressDialog;

    private static final String USERNAME_TAG = "username";
    private static final String PASSWORD_TAG = "password";

    @BindView(R.id.username) EditText _usernameText;
    @BindView(R.id.password) EditText _passwordText;
    @BindView(R.id.submit) Button _loginButton;

    ConnectionDetector cd;
    Boolean isInternetPresent = false;

    // @BindView(R.id.link_signup) TextView _signupLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkStoragePermission();

        SharedPreferences settings = LoginActivity.this.getSharedPreferences(getString(R.string.APP_PREFERENCES), MODE_PRIVATE);
        cd = new ConnectionDetector(getApplicationContext());
        isInternetPresent = cd.isConnectingToInternet();

        if(settings.contains("authorization") && isInternetPresent) {
            Intent i = new Intent(this, VitheaKidsActivity.class);
            LoginActivity.this.startActivity(i);
        } else {
            setContentView(R.layout.activity_login);

            //TODO request storage permission

            ButterKnife.bind(this);
            if (!isInternetPresent)
                Toast.makeText(getBaseContext(), "Não existe ligação de Internet disponível.", Toast.LENGTH_LONG).show();

            _loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cd = new ConnectionDetector(getApplicationContext());
                    isInternetPresent = cd.isConnectingToInternet();

                    if (!isInternetPresent) {
                        Toast.makeText(getBaseContext(), "Não existe ligação de Internet disponível.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    else login();
                }
            });

            /*_signupLink.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // Start the Signup activity
                    Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                    startActivityForResult(intent, REQUEST_SIGNUP);
                }
            });*/

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop(){
        super.onStop();

    }

    public void login() {
        Log.d(TAG, "Login");

        hideKeyboard();

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add(USERNAME_TAG, ((EditText) findViewById(R.id.username)).getText().toString());
        body.add(PASSWORD_TAG, ((EditText)findViewById(R.id.password)).getText().toString());

        final String url = getString(R.string.ws_uri) + getString(R.string.child_login_uri);

        new FetchChildLogin(body, this, url, this).execute();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and logger them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), R.string.login_failed, Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
    }

    // TODO Add the verification if the username is not from a child, but a caregiver
    public boolean validate() {
        boolean valid = true;

        String username = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();

        if (username.isEmpty()) {
            _usernameText.setError(getString(R.string.InvalidUsername));
            valid = false;
        } else {
            _usernameText.setError(null);
        }

        if (password.isEmpty()) {
            _passwordText.setError(getString(R.string.InvalidPassword));
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    /**
     * This method will hide the softkeyboard
     */
    private void hideKeyboard(){

        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onTaskStarted() {
        lockScreenOrientation();
        setProgressDialogVisible();
    }

    /***
     * All the actions must be process here
     * @param response
     */
    @Override
    public void onTaskFinished(ResponseEntity<String> response) {
        String token = null;
        boolean authorized = false;
        final String APP_PREFERENCES = "AppPreferences";
        final String AUTHORIZATION = "authorization";

        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        unlockScreenOrientation();

        if (response != null) {
            try {
                HttpStatus status = response.getStatusCode();

                authorized = status != HttpStatus.UNAUTHORIZED;

                JSONObject jsonOb = new JSONObject(response.getBody());
                token = jsonOb.getString("authToken");

            } catch (HttpClientErrorException e) {
                Log.e(TAG, e.getLocalizedMessage(), e);
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (authorized && token != null) {
                SharedPreferences settings = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
                SharedPreferences.Editor prefEditor = settings.edit();
                prefEditor.putString(AUTHORIZATION, token);
                prefEditor.commit();
                Intent i = new Intent(getApplicationContext(), VitheaKidsActivity.class);
                startActivity(i);

            } else {
                loginInvalid();
            }

        } else {
            loginInvalid();
        }
    }

    private void loginInvalid(){
        Toast.makeText(getBaseContext(), R.string.login_failed, Toast.LENGTH_LONG).show();
        findViewById(R.id.submit).setEnabled(true);
        ((EditText)findViewById(R.id.password)).setText("");
    }

    private void setProgressDialogVisible(){

        progressDialog = new ProgressDialog(this, R.style.Base_Theme_AppCompat_Dialog);

        // set a transparent background to the progressDialog
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getResources().getString(R.string.loading_login));
       // progressDialog.show();
    }

    /***
     * ugly
     */
    private void lockScreenOrientation() {
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    /***
     * ugly
     */
    private void unlockScreenOrientation() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }


    /** Storage Permission Methods **/

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        // BEGIN_INCLUDE(onRequestPermissionsResult)
        if (requestCode == PERMISSION_REQUEST_EXTERNAL_STORAGE) {
            // Request for write external storage permission.
            if (grantResults.length == 1 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                // Permission request was denied.
                requestStoragePermission();
            }
        }
        // END_INCLUDE(onRequestPermissionsResult)
    }

    private void checkStoragePermission() {
        // Check if the Storage permission has been granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // if was not granted
            requestStoragePermission();
        }
    }

    /**
     * Requests the {@link android.Manifest.permission#WRITE_EXTERNAL_STORAGE} permission.
     * If an additional rationale should be displayed, the user has to launch the request from
     * an AlertDialog that includes additional information.
     */
    private void requestStoragePermission() {
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a AlertDialog with a button to request the missing permission.
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            dialogBuilder.setTitle("Permissões");
            dialogBuilder.setMessage("É necessária a permissão de armazenamento para a realização de testes.");
            dialogBuilder.setCancelable(true);
            dialogBuilder.setNeutralButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        // Request the permission
                        ActivityCompat.requestPermissions(LoginActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                PERMISSION_REQUEST_EXTERNAL_STORAGE);
                    }
                });

            AlertDialog permissionAlertDialog = dialogBuilder.create();
            permissionAlertDialog.show();

        } else {
            //Permission is not available. Requesting storage permission.

            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_EXTERNAL_STORAGE);
        }
    }
}