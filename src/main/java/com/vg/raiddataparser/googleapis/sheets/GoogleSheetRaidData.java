package com.vg.raiddataparser.googleapis.sheets;

import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.SpreadsheetProperties;
import com.vg.raiddataparser.googleapis.drive.GoogleDriveService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class GoogleSheetRaidData {

    private static final Logger LOGGER = LoggerFactory.getLogger(GoogleSheetRaidData.class.getName());

    private final GoogleDriveService driveService = new GoogleDriveService();
    private final GoogleSheetsService sheetsService = new GoogleSheetsService();

    private final static String SPREADSHEET_ID_FILE_NAME = "/spreadsheet_id.txt";
    private final static String RESOURCES_PATH = "src/main/resources";

    public GoogleSheetRaidData() {
        initializeRaidData();
    }

    private void initializeRaidData() {

        File file = new File(RESOURCES_PATH + SPREADSHEET_ID_FILE_NAME);
        String title = "RSL - Champions' multipliers (last updated: " + getCurrentDateFormatyyyyMMdd() + ")";
        SpreadsheetProperties properties = new SpreadsheetProperties().setTitle(title);

        try {
            if (file.exists()) { // spreadsheet_id.txt exists
                String spreadsheetId = readSpreadsheetId(file);

                if (driveService.fileExists(spreadsheetId)) { // spreadsheet with corresponding ID exists on Drive
                    LOGGER.info("Spreadsheet already exists on Drive");
                    //TODO: Update Raid data
                    LOGGER.info("Updating Raid data (to do)");
                } else { // spreadsheet with corresponding ID doesn't exist on Drive
                    Spreadsheet result = sheetsService.createSpreadsheet(properties);
                    writeSpreadsheetId(file, result.getSpreadsheetId());
                }
            } else { // spreadsheet_id.txt doesn't exist
                LOGGER.info("Creating new local file " + file.getAbsolutePath());
                if (file.createNewFile()) {
                    Spreadsheet result = sheetsService.createSpreadsheet(properties);
                    writeSpreadsheetId(file, result.getSpreadsheetId());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error occurred when creating spreadsheet. Operation will be aborted.", e);
        }
    }

    private static String getCurrentDateFormatyyyyMMdd() {
        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
        return dateFormatter.format(date);
    }

    private void writeSpreadsheetId(File f, String spreadsheetId) throws IOException {
        LOGGER.info("Writing spreadsheet ID to file");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(f, false))) {
            writer.write(spreadsheetId);
        } catch (IOException e) {
            throw new IOException("Error occurred when writing spreadsheet ID to file \""
                    + RESOURCES_PATH + SPREADSHEET_ID_FILE_NAME
                    + "\". Operation will be aborted.");
        }
    }

    private String readSpreadsheetId(File f) throws IOException {
        LOGGER.info("Retrieving spreadsheet ID from file");
        try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
            return reader.readLine();
        } catch (IOException e) {
            throw new IOException("Error occurred when retrieving spreadsheet ID from file \""
                    + RESOURCES_PATH + SPREADSHEET_ID_FILE_NAME
                    + "\". Operation will be aborted.");
        }
    }

}
