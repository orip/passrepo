package com.example.passrepo.store;


public interface CredentialStore {

  String[] read();
  void write(String[]response);
  void clearCredentials();
}
