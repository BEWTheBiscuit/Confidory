package com.bisbizkuit.whistalk.SpecialClass;

public interface FragmentToActivityCommunicator {
    void passUsernameToActivity(String username);
    void passFullnameToActivity(String firstname, String lastname);
    void passEmailToActivity(String email);
    void passPasswordToActivity(String password);
    void passCreateBooleanToActivity(Boolean accept);
}

