package com.dessylazarova.medicare;

import com.dessylazarova.medicare.data.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class AuthUtils {
    private static AuthUtils instance;
    private FirebaseAuth auth;

    public static AuthUtils getInstance() {
        if (instance == null) {
            instance = new AuthUtils();
        }
        return instance;
    }

    private AuthUtils() {
        auth = FirebaseAuth.getInstance();
    }

    public User getLoggedUser() {
        return auth.getCurrentUser() != null ? new User(auth.getCurrentUser().getUid(),
                auth.getCurrentUser().getEmail()) : null;
    }


    public void loginWithEmail(String email, String password, LoginListener loginListener) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                loginListener.onLoginSuccessful(getLoggedUser());
            }
        }).addOnFailureListener(e -> {
            if (e instanceof FirebaseAuthInvalidUserException) {
                createUserWithEmail(email, password, loginListener);
            } else {
                loginListener.onLoginFailed(e.getLocalizedMessage());
            }
        });
    }

    private void createUserWithEmail(String email, String password, LoginListener loginListener) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> { if (task.isSuccessful()) loginListener.onLoginSuccessful(getLoggedUser()); })
                .addOnFailureListener(e -> loginListener.onLoginFailed(e.getLocalizedMessage()));
    }

    public void logout() {
        auth.signOut();
    }

    public interface LoginListener {
        void onLoginSuccessful(User user);
        void onLoginFailed(String error);
    }
}
