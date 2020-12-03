package com.vg.raiddataparser;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.*;
import java.util.Collections;
import java.util.List;

public class GoogleCredentialsUtil {

    private static final String CREDENTIALS_FILE_PATH = "/google-oauth-credentials.json";
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
    private static final String REFRESH_TOKEN = "1//04HHDsFDz7OAVCgYIARAAGAQSNwF-L9Irym9EJnbS-gtLftgKcx_GrPhC7vhRtCwg6_hJzrAT0-99B8RrCOoMUaiU0tCj40PQZOA";
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    public static Credential getCredentials(
            JsonFactory jsonFactory,
            NetHttpTransport httpTransport) throws IOException {

        System.out.println("vgr getCredentials()");

        // Load client secrets from JSON file
        InputStream inputStream = GoogleCredentialsUtil.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (inputStream == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }

        //FIXME: implement GoogleCredentials with refresh token: 1//04HHDsFDz7OAVCgYIARAAGAQSNwF-L9Irym9EJnbS-gtLftgKcx_GrPhC7vhRtCwg6_hJzrAT0-99B8RrCOoMUaiU0tCj40PQZOA

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory, new InputStreamReader(inputStream));

        /*GoogleCredential credential = new GoogleCredential.Builder()
                .setTransport(httpTransport)
                .setClientSecrets(clientSecrets)
                .setJsonFactory(jsonFactory)
                .setServiceAccountScopes(SCOPES)
                .build()
                .setRefreshToken(REFRESH_TOKEN);*/

        //TODO: remove comments after full implementation of google sheet updater
        // Build flow and trigger user authorization request
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport,
                jsonFactory,
                clientSecrets,
                SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8080).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");

        //return credential;
    }
}
