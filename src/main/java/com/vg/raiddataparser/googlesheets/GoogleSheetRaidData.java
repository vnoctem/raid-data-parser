package com.vg.raiddataparser.googlesheets;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.SpreadsheetProperties;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class GoogleSheetRaidData {

    private final Sheets sheetsService = GoogleSheetsServiceUtil.getSheetsService();

    private final static String SPREADSHEET_ID = "1httPAXX73P6suWGPlmC0xHMVt8wyXFQDGsI60L2QzhM";

    public GoogleSheetRaidData() throws IOException, GeneralSecurityException {
        createSpreadsheet();
    }

    private void createSpreadsheet() throws IOException {
        System.out.println("vgr GoogleSheetRaidData.create()");

        if (!spreadsheetExists()) {
            System.out.println("vgr !spreadsheetExists");

            String title = "RSL - Champions' multipliers (last updated: " + getCurrentDateFormatyyyyMMdd() + ")";

            Spreadsheet spreadsheet = new Spreadsheet()
                    .setProperties(new SpreadsheetProperties()
                            .setTitle(title));

            //TODO: persist spreadsheetId in a file
            Spreadsheet result = sheetsService.spreadsheets().create(spreadsheet).execute();
        } else {
            System.out.println("vgr spreadsheetExists");
        }
    }

    private boolean spreadsheetExists() throws IOException {
        try {
            sheetsService.spreadsheets().get(SPREADSHEET_ID).execute();
            return true;
        } catch (GoogleJsonResponseException e) {
            System.err.println("Caught GoogleJsonResponseException: " + e.getDetails());
            return false;
        }
    }

    private static String getCurrentDateFormatyyyyMMdd() {
        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
        return dateFormatter.format(date);
    }

}
