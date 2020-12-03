package com.vg.raiddataparser;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class GoogleSheetsServiceUtil {

    private static final String APPLICATION_NAME = "Raid data parser";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    public static Sheets getSheetsService() throws IOException, GeneralSecurityException {

        System.out.println("vgr getSheetsService()");

        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Credential credentials = GoogleCredentialsUtil.getCredentials(JSON_FACTORY, HTTP_TRANSPORT);

        return new Sheets.Builder(
                HTTP_TRANSPORT,
                JSON_FACTORY,
                credentials
        ).setApplicationName(APPLICATION_NAME).build();

    }

}
