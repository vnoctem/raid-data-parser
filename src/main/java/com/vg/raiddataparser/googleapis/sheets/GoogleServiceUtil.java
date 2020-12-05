package com.vg.raiddataparser.googleapis.sheets;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

public final class GoogleServiceUtil {

    private static final String CREDENTIALS_FILE_PATH = "/google-oauth-credentials.json";
    private static final String TOKENS_DIRECTORY_PATH = "oauth2.0_tokens";
    private static final String APPLICATION_NAME = "Raid data parser";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Arrays.asList(
            SheetsScopes.SPREADSHEETS,
            DriveScopes.DRIVE_METADATA_READONLY);

    //private static final String REFRESH_TOKEN = "1//04HHDsFDz7OAVCgYIARAAGAQSNwF-L9Irym9EJnbS-gtLftgKcx_GrPhC7vhRtCwg6_hJzrAT0-99B8RrCOoMUaiU0tCj40PQZOA";

    private GoogleServiceUtil() {}

    private static Credential authorize(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        System.out.println("vgr GoogleSheetsService.authorize()");

        // Load client secrets
        InputStream in = GoogleServiceUtil.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found" + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT,
                JSON_FACTORY,
                clientSecrets,
                SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();

        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver.Builder().setPort(8888).build()).authorize("user");
    }

    public static Sheets getSheetsService() throws IOException, GeneralSecurityException {
        System.out.println("vgr GoogleService.getSheetsService()");

        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        return new Sheets.Builder(
                HTTP_TRANSPORT,
                JSON_FACTORY,
                authorize(HTTP_TRANSPORT)
        ).setApplicationName(APPLICATION_NAME).build();
    }

    public static Drive getDriveService() throws IOException, GeneralSecurityException {
        System.out.println("vgr GoogleService.getDriveService()");

        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        return new Drive.Builder(
                HTTP_TRANSPORT,
                JSON_FACTORY,
                authorize(HTTP_TRANSPORT)
        ).setApplicationName(APPLICATION_NAME).build();
    }
}
