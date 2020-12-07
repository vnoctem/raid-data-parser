package com.vg.raiddataparser.googleapis.sheets;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.SpreadsheetProperties;
import com.vg.raiddataparser.googleapis.GoogleServiceUtil;
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

    private final static String SPREADSHEET_ID = "1httPAXX73P6suWGPlmC0xHMVt8wyXFQDGsI60L2QzhM";
    private final static String SPREADSHEET_ID_FILE_NAME = "/spreadsheet_id.txt";
    private final static String RESOURCES_PATH = "src/main/resources";

    public GoogleSheetRaidData() {
        initializeRaidData();
    }

    private void initializeRaidData() {

        try {
            if (!driveService.fileExists(readSpreadsheetId(RESOURCES_PATH + SPREADSHEET_ID_FILE_NAME))) {
                LOGGER.info("Creating spreadsheet...");

                String title = "RSL - Champions' multipliers (last updated: " + getCurrentDateFormatyyyyMMdd() + ")";
                SpreadsheetProperties properties = new SpreadsheetProperties().setTitle(title);
                Spreadsheet result = sheetsService.createSpreadsheet(properties);
                writeSpreadsheetId(RESOURCES_PATH + SPREADSHEET_ID_FILE_NAME, result.getSpreadsheetId());
            } else {
                LOGGER.info("Spreadsheet already exists");

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

    private boolean fileExists(String path) {
        File file = new File(path);
        return file.exists();
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

    private void writeSpreadsheetId(String fileName, String spreadsheetId) throws IOException {
        LOGGER.info("Writing spreadsheet ID to file");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, false))) {
            writer.write(spreadsheetId);
        } catch (IOException e) {
            throw new IOException("Error occurred when writing spreadsheet ID to file \""
                    + RESOURCES_PATH + SPREADSHEET_ID_FILE_NAME
                    + "\". Operation will be aborted.");
        }
    }

    private String readSpreadsheetId(String fileName) throws IOException {
        LOGGER.info("Retrieving spreadsheet ID from file");
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            return reader.readLine();
        } catch (IOException e) {
            throw new IOException("Error occurred when retrieving spreadsheet ID from file \""
                    + RESOURCES_PATH + SPREADSHEET_ID_FILE_NAME
                    + "\". Operation will be aborted.");
        }
    }

}
