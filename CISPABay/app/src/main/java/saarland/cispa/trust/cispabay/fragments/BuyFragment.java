package saarland.cispa.trust.cispabay.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;


import saarland.cispa.trust.cispabay.R;
import saarland.cispa.trust.cispabay.managers.KeyStoreManager;

public class BuyFragment extends Fragment {
    private final String TAG = "CISPABay-BuyFragment";
    private Button confirmButton;


    public BuyFragment() {
    }

    public static BuyFragment newInstance(int id) {
        return new BuyFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private boolean isLoggedIn() {
        String encryptedAuthToken = getStoredAuthenticationToken();
        if (encryptedAuthToken == null) {
            return false;
        }
        return true;
    }

    private void authenticateUser() {
        if (isLoggedIn()) {
            readAndDecryptAuthenticationToken();
        }

        // @TODO: Trigger the Biometric authentication
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buy, container, false);
        confirmButton = view.findViewById(R.id.confirmBtn);
        final Button cancelButton = view.findViewById(R.id.cancelBtn);

        configureFingerprintPromptAndResponseHandler();

        View.OnClickListener onConfirmButtonClickedListener = view1 -> authenticateUser();
        confirmButton.setOnClickListener(onConfirmButtonClickedListener);

        View.OnClickListener onCancelButtonClickedListener = view12 -> getFragmentManager().popBackStack();
        cancelButton.setOnClickListener(onCancelButtonClickedListener);

        return view;
    }

    private void configureFingerprintPromptAndResponseHandler() {
        // @TODO: IMPLEMENT-ME
    }

    private String getStoredAuthenticationToken() {
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(this.getContext());
        return preferences.getString("auth_key", null);
    }

    private void readAndDecryptAuthenticationToken() {
        String encryptedAuthToken = getStoredAuthenticationToken();
        Log.d(TAG, "encryptedAuthToken: " + encryptedAuthToken);

        KeyStoreManager keyStoreManager = KeyStoreManager.getInstance();
        String decryptedAuthToken = keyStoreManager.decrypt(encryptedAuthToken);
        if (decryptedAuthToken != null) {
            Log.d(TAG, "encryptedAuthToken: " + decryptedAuthToken);
            confirmButton.setEnabled(false);

            Toast.makeText(this.getContext(), "The item is bought!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(
                    this.getContext(),
                    "Error! we couldn't decrypt the authentication token!",
                    Toast.LENGTH_LONG
            ).show();
        }
    }
}