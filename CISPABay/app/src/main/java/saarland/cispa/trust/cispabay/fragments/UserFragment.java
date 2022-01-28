package saarland.cispa.trust.cispabay.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.attestation.server.AttestationServer;
import com.attestation.server.Payload;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import saarland.cispa.trust.cispabay.R;
import saarland.cispa.trust.cispabay.managers.KeyStoreManager;

public class UserFragment extends Fragment {
    public UserFragment() {}

    public static UserFragment newInstance() {
        return new UserFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void storeAuthenticationTokenSecurely(String authToken) {
        KeyStoreManager keyStoreManager = KeyStoreManager.getInstance();
        String encryptedData = keyStoreManager.encrypt(authToken);

        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(this.getContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("auth_key", encryptedData);
        editor.commit();

        Toast.makeText(this.getContext(), "Login was successful!", Toast.LENGTH_LONG).show();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        Button loginButton = (Button) view.findViewById(R.id.loginBtn);

        loginButton.setOnClickListener(v -> {
            String username =
                    ((EditText) view.findViewById(R.id.usernameField)).getText().toString();
            String password =
                    ((EditText) view.findViewById(R.id.passwordField)).getText().toString();

            login(username, password);
        });

        return view;
    }

    private void login(String username, String password) {
        // @TODO: IMPLEMENT-ME (prepare payload and signature to authenticate successfully)

        // An example tha you can keep: we get the certificate, serialize it, and add it the payload
        KeyStoreManager keyStoreManager = KeyStoreManager.getInstance();
        Payload payload = new Payload();
        byte[] certificate = new byte[0];
        try {
            certificate = keyStoreManager.encodeCertificate(
                    KeyStoreManager.getInstance().getCertificateChain()[0]
            );
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        payload.setCertificate(certificate);
        // ... Fill the other attributes.

        // TODO: Sign the payload

        String authToken = AttestationServer.authenticate(null, null);
        if (authToken == null) {
            Toast.makeText(
                    this.getContext(),
                    "Login failed!",
                    Toast.LENGTH_LONG
            ).show();
            return;
        }
        storeAuthenticationTokenSecurely(authToken);

        getFragmentManager().popBackStack();
    }
}