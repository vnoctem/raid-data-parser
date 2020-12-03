package com.vg.raiddataparser;

import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.SpreadsheetProperties;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class GoogleSpreadsheetBuilder {

    public static void create() throws IOException, GeneralSecurityException {

        System.out.println("vgr create()");

        String title = "RSL - Champions' multipliers (last updated: " + getCurrentDateFormatyyyyMMdd() + ")";

        Spreadsheet spreadsheet = new Spreadsheet()
                .setProperties(new SpreadsheetProperties()
                        .setTitle(title));

        Spreadsheet result = GoogleSheetsServiceUtil.getSheetsService().spreadsheets().create(spreadsheet).execute();
    }

    private static String getCurrentDateFormatyyyyMMdd() {
        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
        return dateFormatter.format(date);
    }
}
