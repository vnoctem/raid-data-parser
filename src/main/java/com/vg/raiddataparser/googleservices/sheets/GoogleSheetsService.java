package com.vg.raiddataparser.googleservices.sheets;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.SpreadsheetProperties;
import com.vg.raiddataparser.googleservices.GoogleServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;

public class GoogleSheetsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GoogleSheetsService.class.getName());
    private final Sheets sheetsService = GoogleServiceUtil.getSheetsService();

    public GoogleSheetsService() {
        LOGGER.info("Initializing GoogleSheetsService...");
        if (sheetsService == null) {
            throw new NullPointerException("Error while initializing GoogleSheetsService: Sheets service is null.");
        }
        LOGGER.info("GoogleSheetsService initialized");
    }

    public Spreadsheet createSpreadsheet(SpreadsheetProperties properties) throws IOException {
        LOGGER.info("Creating new spreadsheet");
        Spreadsheet spreadsheet = new Spreadsheet().setProperties(properties);
        return Objects.requireNonNull(sheetsService).spreadsheets().create(spreadsheet).execute();
    }

}
