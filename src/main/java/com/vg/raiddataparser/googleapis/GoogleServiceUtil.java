package com.vg.raiddataparser.googleapis;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

public final class GoogleServiceUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(GoogleServiceUtil.class.getName());

    private static final String CREDENTIALS_FILE_PATH = "/google-oauth-credentials.json";
    private static final String TOKENS_DIRECTORY_PATH = "oauth2.0_tokens";
    private static final String APPLICATION_NAME = "Raid data parser";

    private static final List<String> SCOPES = Arrays.asList(
            SheetsScopes.SPREADSHEETS,
            DriveScopes.DRIVE_METADATA_READONLY);

    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    //private static final String REFRESH_TOKEN = "1//04HHDsFDz7OAVCgYIARAAGAQSNwF-L9Irym9EJnbS-gtLftgKcx_GrPhC7vhRtCwg6_hJzrAT0-99B8RrCOoMUaiU0tCj40PQZOA";

    private GoogleServiceUtil() {}


    /**
     * Authorize the user with the credentials in google-oauth-credentials.json
     *
     * @param HTTP_TRANSPORT HTTP transport used for the APIs
     * @return Credential object
     * @throws IOException
     */
    private static Credential authorize(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        LOGGER.info("Authorize user");

        // Load client secrets
        InputStream in = GoogleServiceUtil.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH + ".");
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

    public static Sheets getSheetsService() {
        LOGGER.info("Getting Sheets service");
        try {
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            return new Sheets.Builder(
                    HTTP_TRANSPORT,
                    JSON_FACTORY,
                    authorize(HTTP_TRANSPORT)
            ).setApplicationName(APPLICATION_NAME).build();
        } catch (IOException e) {
            LOGGER.error("Error while getting Sheets service. {}", e.getMessage());
        } catch (GeneralSecurityException e) {
            LOGGER.error("Error while creating HTTP transport for Sheets service");
        }
        return null;
    }

    public static Drive getDriveService() {
        LOGGER.info("Getting Drive service");
        try {
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            return new Drive.Builder(
                    HTTP_TRANSPORT,
                    JSON_FACTORY,
                    authorize(HTTP_TRANSPORT)
            ).setApplicationName(APPLICATION_NAME).build();
        } catch (IOException e) {
            LOGGER.error("Error while getting Drive service. {}", e.getMessage());
        } catch (GeneralSecurityException e) {
            LOGGER.error("Error while creating HTTP transport for Drive service");
        }
        return null;
    }
}
