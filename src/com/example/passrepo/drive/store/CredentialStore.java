package com.example.passrepo.drive.store;


public interface CredentialStore {

  String[] read();
  void write(String[]response);
  void clearCredentials();
}
