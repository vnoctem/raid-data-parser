package com.vg.raiddataparser.googleapis.sheets;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.drive.Drive;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.SpreadsheetProperties;

import java.io.*;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class GoogleSheetRaidData {

    private final Sheets sheetsService = GoogleServiceUtil.getSheetsService();
    private final Drive driveService = GoogleServiceUtil.getDriveService();
    private final static String SPREADSHEET_ID = "1httPAXX73P6suWGPlmC0xHMVt8wyXFQDGsI60L2QzhM";
    private final static String SPREADSHEET_ID_FILE_NAME = "/spreadsheet_id.txt";
    private final static String RESOURCES_PATH = "src/main/resources";

    public GoogleSheetRaidData() throws IOException, GeneralSecurityException {
        createSpreadsheet();
    }

    private void createSpreadsheet() throws IOException {
        System.out.println("vgr GoogleSheetRaidData.create()");

        /*
        1. create spreadsheet if it doesn't exist:
         - check if exist by getting the sheet with Spreadsheet ID in the file spreadsheet_id.txt
         - if file doesn't exist, create file
         - write the id of the created spreadsheet in the file
         */

        //TODO: optimize verification method to check if spreadsheet exists
        //  organize methods for creating, writing and reading file
        if (fileExists2(readSpreadsheetId())) {
            System.out.println("vgr fileExists2");
        } else {
            System.out.println("vgr !fileExists2");
        }

        /*if (!spreadsheetExists()) { // if spreadsheet doesn't exist, create it
            System.out.println("vgr !spreadsheetExists");

            String title = "RSL - Champions' multipliers (last updated: " + getCurrentDateFormatyyyyMMdd() + ")";

            Spreadsheet spreadsheet = new Spreadsheet()
                    .setProperties(new SpreadsheetProperties()
                            .setTitle(title));

            Spreadsheet result = sheetsService.spreadsheets().create(spreadsheet).execute();
            writeSpreadsheetId(result.getSpreadsheetId());
        } else {
            System.out.println("vgr spreadsheetExists");
        }*/
    }

    private boolean fileExists2(String id) throws IOException {
        try {
            com.google.api.services.drive.model.File file = driveService.files().get(id).setFields("id, trashed").execute();
            System.out.println("fileExists2 getId: " + file.getId());
            System.out.println("fileExists2 getTrashed: " + file.getTrashed());
            return true;
        } catch (GoogleJsonResponseException e) {
            System.err.println("Bad id: ");
            e.printStackTrace();
        }
        return false;
    }

    private boolean spreadsheetExists() throws IOException {
        // if file does not exist, create it
        if (!fileExists(RESOURCES_PATH + SPREADSHEET_ID_FILE_NAME)) {
            createFile();
            return false;
        } else { // if file exists
            String spreadsheetId = readSpreadsheetId();
            if (spreadsheetId == null) { // if spreadsheetId is null, return false
                return false;
            } else { // if spreadsheetId exists
                try {
                    sheetsService.spreadsheets().get(spreadsheetId).execute();
                    return true;
                } catch (GoogleJsonResponseException e) {
                    System.err.println("Caught GoogleJsonResponseException: " + e.getDetails());
                    return false;
                }
            }

        }
    }

    private static String getCurrentDateFormatyyyyMMdd() {
        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
        return dateFormatter.format(date);
    }

    private boolean fileExists(String filePath) {
        File f = new File(filePath);
        return f.exists() && !f.isDirectory();
    }

    private File createFile() throws IOException {
        System.out.println("vgr createFile - file path: " + RESOURCES_PATH + SPREADSHEET_ID_FILE_NAME);

        File file = new File(RESOURCES_PATH + SPREADSHEET_ID_FILE_NAME);

        if (file.createNewFile()) {
            System.out.println("File is created");
        } else {
            System.out.println("File already exists");
        }
        return file;
    }

    private void writeSpreadsheetId(String spreadsheetId) throws IOException {
        System.out.println("vgr writeSpreadsheetId");
        BufferedWriter writer = new BufferedWriter(new FileWriter(RESOURCES_PATH + SPREADSHEET_ID_FILE_NAME, false));
        writer.write(spreadsheetId);
        writer.close();
    }

    private String readSpreadsheetId() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(RESOURCES_PATH + SPREADSHEET_ID_FILE_NAME));
        String spreadsheetId = reader.readLine();
        reader.close();
        System.out.println("vgr readSpreadsheetId id: " + spreadsheetId);
        return spreadsheetId;
    }

}
