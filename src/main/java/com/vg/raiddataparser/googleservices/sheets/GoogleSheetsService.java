package com.vg.raiddataparser.googleservices.sheets;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import com.vg.raiddataparser.googleservices.GoogleServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GoogleSheetsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GoogleSheetsService.class.getName());
    private static final Sheets SERVICE_SHEETS = GoogleServiceUtil.getSheetsService();

    public GoogleSheetsService() {
        LOGGER.info("Initializing GoogleSheetsService...");
        if (SERVICE_SHEETS == null) {
            throw new NullPointerException("Error while initializing GoogleSheetsService: Sheets service is null.");
        }
        LOGGER.info("GoogleSheetsService initialized");
    }

    public Spreadsheet createSpreadsheet(SpreadsheetProperties properties, List<Sheet> sheets) throws IOException {
        LOGGER.info("Creating spreadsheet " + properties.getTitle());
        Spreadsheet spreadsheet = new Spreadsheet()
                .setProperties(properties)
                .setSheets(sheets);
        return Objects.requireNonNull(SERVICE_SHEETS).spreadsheets().create(spreadsheet).execute();
    }

    public UpdateValuesResponse updateValues(String spreadsheetId,
            String range,
            ValueRange body) throws IOException {
        return Objects.requireNonNull(SERVICE_SHEETS).spreadsheets()
                .values()
                .update(spreadsheetId, range, body)
                .setValueInputOption("RAW")
                .execute();
    }

    public AppendValuesResponse appendValues(String spreadsheetId,
            String range,
            ValueRange body) throws IOException {
        return Objects.requireNonNull(SERVICE_SHEETS).spreadsheets()
                .values()
                .append(spreadsheetId, range, body)
                .setValueInputOption("RAW")
                .execute();
    }

    public Spreadsheet getSpreadsheet(String id) throws IOException {
        return Objects.requireNonNull(SERVICE_SHEETS).spreadsheets().get(id).execute();
    }

    public int getSheetId(String spreadsheetId, int index) throws IOException {
        return getSpreadsheet(spreadsheetId).getSheets().get(index).getProperties().getSheetId();
    }

    public int getNumberOfRows(String spreadsheetId, String range) throws IOException {
        return Objects.requireNonNull(SERVICE_SHEETS)
                .spreadsheets()
                .values()
                .get(spreadsheetId, range)
                .execute()
                .getValues()
                .size();
    }

    public BatchUpdateSpreadsheetResponse addBanding(String spreadsheetId,
            int sheetIndex,
            String range,
            Color headerColor,
            Color firstBandColor,
            Color secondBandColor) throws IOException {
        LOGGER.info("Adding banding to sheet " + range);
        List<Request> requests = new ArrayList<>();

        requests.add(new Request()
                .setAddBanding(new AddBandingRequest()
                        .setBandedRange(new BandedRange()
                                .setRange(new GridRange()
                                        .setSheetId(getSheetId(spreadsheetId, sheetIndex))
                                        .setEndRowIndex(getNumberOfRows(spreadsheetId, range)))
                                .setRowProperties(new BandingProperties()
                                        .setHeaderColor(headerColor)
                                        .setFirstBandColor(firstBandColor)
                                        .setSecondBandColor(secondBandColor)
                                )
                        )
                )
        );

        BatchUpdateSpreadsheetRequest requestBody = new BatchUpdateSpreadsheetRequest();
        requestBody.setRequests(requests);

        return Objects.requireNonNull(SERVICE_SHEETS)
                .spreadsheets()
                .batchUpdate(spreadsheetId, requestBody)
                .execute();
    }

    public BatchUpdateSpreadsheetResponse renameSpreadsheet(String spreadsheetId, String title) throws IOException {
        LOGGER.info("Renaming spreadsheet with last updated date");
        SpreadsheetProperties properties = new SpreadsheetProperties().setTitle(title);
        List<Request> requests = new ArrayList<>();

        requests.add(new Request().setUpdateSpreadsheetProperties(
                new UpdateSpreadsheetPropertiesRequest().setFields(
                        "title").setProperties(properties)));

        BatchUpdateSpreadsheetRequest requestBody = new BatchUpdateSpreadsheetRequest();
        requestBody.setRequests(requests);

        return Objects.requireNonNull(SERVICE_SHEETS)
                .spreadsheets()
                .batchUpdate(spreadsheetId, requestBody)
                .execute();
    }

    public BatchUpdateSpreadsheetResponse createSheet(Spreadsheet spreadsheet,
            SheetProperties properties) throws IOException {
        LOGGER.info("Creating sheet " + properties.getTitle());
        List<Request> requests = new ArrayList<>();

        requests.add(new Request()
                .setAddSheet(new AddSheetRequest()
                        .setProperties(properties)));
        BatchUpdateSpreadsheetRequest requestBody = new BatchUpdateSpreadsheetRequest();
        requestBody.setRequests(requests);
        requestBody.setIncludeSpreadsheetInResponse(true);

        return Objects.requireNonNull(SERVICE_SHEETS)
                .spreadsheets()
                .batchUpdate(spreadsheet.getSpreadsheetId(), requestBody)
                .execute();
    }

    public void renameSheet(Spreadsheet spreadsheet, SheetProperties properties) throws IOException {
        LOGGER.info("Renaming sheet[" + properties.getSheetId() + "] to " + properties.getTitle());
        List<Request> requests = new ArrayList<>();

        requests.add(new Request()
                .setUpdateSheetProperties(new UpdateSheetPropertiesRequest()
                        .setFields("title")
                        .setProperties(properties)));

        BatchUpdateSpreadsheetRequest requestBody = new BatchUpdateSpreadsheetRequest();
        requestBody.setRequests(requests);
        requestBody.setIncludeSpreadsheetInResponse(true);

        Objects.requireNonNull(SERVICE_SHEETS)
                .spreadsheets()
                .batchUpdate(spreadsheet.getSpreadsheetId(), requestBody)
                .execute();
    }

}
