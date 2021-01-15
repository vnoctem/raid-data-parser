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
        Objects.requireNonNull(SERVICE_SHEETS, "Error while initializing GoogleSheetsService: Sheets service is null.");
        LOGGER.info("GoogleSheetsService initialized");
    }

    public Spreadsheet createSpreadsheet(SpreadsheetProperties properties, List<Sheet> sheets) throws IOException {
        LOGGER.info("Creating spreadsheet " + properties.getTitle());
        Spreadsheet spreadsheet = new Spreadsheet()
                .setProperties(properties)
                .setSheets(sheets);
        return Objects.requireNonNull(SERVICE_SHEETS).spreadsheets().create(spreadsheet).execute();
    }

    public void updateValues(String spreadsheetId, String range, ValueRange body) throws IOException {
        Objects.requireNonNull(SERVICE_SHEETS).spreadsheets()
                .values()
                .update(spreadsheetId, range, body)
                .setValueInputOption("RAW")
                .execute();
    }

    public void appendValues(String spreadsheetId, String range, ValueRange body) throws IOException {
        Objects.requireNonNull(SERVICE_SHEETS).spreadsheets()
                .values()
                .append(spreadsheetId, range, body)
                .setValueInputOption("RAW")
                .execute();
    }

    public Spreadsheet getSpreadsheet(String id) throws IOException {
        return Objects.requireNonNull(SERVICE_SHEETS)
                .spreadsheets()
                .get(id)
                .execute();
    }

    private int getSheetId(String spreadsheetId, int index) throws IOException {
        return getSpreadsheet(spreadsheetId).getSheets().get(index).getProperties().getSheetId();
    }

    private int getNumberOfRows(String spreadsheetId, String range) throws IOException {
        return Objects.requireNonNull(SERVICE_SHEETS)
                .spreadsheets()
                .values()
                .get(spreadsheetId, range)
                .execute()
                .getValues()
                .size();
    }

    private int getBandedRangeId(String spreadsheetId, int sheetIndex, int bandedRangeIndex) throws IOException {
        return getSpreadsheet(spreadsheetId).getSheets()
                .get(sheetIndex)
                .getBandedRanges()
                .get(bandedRangeIndex)
                .getBandedRangeId();
    }

    public void addBanding(String spreadsheetId,
            int sheetIndex,
            String range,
            Color headerColor,
            Color firstBandColor,
            Color secondBandColor) throws IOException {
        LOGGER.info("Adding banding to sheet " + range);
        List<Request> requests = new ArrayList<>();
        BatchUpdateSpreadsheetRequest requestBody = new BatchUpdateSpreadsheetRequest();

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

        requestBody.setRequests(requests);

        Objects.requireNonNull(SERVICE_SHEETS)
                .spreadsheets()
                .batchUpdate(spreadsheetId, requestBody)
                .execute();
    }

    public void updateBanding(String spreadsheetId,
            int sheetIndex,
            String range,
            Color headerColor,
            Color firstBandColor,
            Color secondBandColor) throws IOException {
        LOGGER.info("Updating banding to sheet " + range);
        List<Request> requests = new ArrayList<>();
        BatchUpdateSpreadsheetRequest requestBody = new BatchUpdateSpreadsheetRequest();

        requests.add(new Request()
                .setUpdateBanding(new UpdateBandingRequest()
                        .setFields("*")
                        .setBandedRange(new BandedRange()
                                .setBandedRangeId(getBandedRangeId(spreadsheetId, sheetIndex, 0))
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

        requestBody.setRequests(requests);

        Objects.requireNonNull(SERVICE_SHEETS)
                .spreadsheets()
                .batchUpdate(spreadsheetId, requestBody)
                .execute();
    }

    public void renameSpreadsheet(String spreadsheetId, String title) throws IOException {
        LOGGER.info("Renaming spreadsheet with last updated date");
        SpreadsheetProperties properties = new SpreadsheetProperties().setTitle(title);
        List<Request> requests = new ArrayList<>();
        BatchUpdateSpreadsheetRequest requestBody = new BatchUpdateSpreadsheetRequest();

        requests.add(new Request().setUpdateSpreadsheetProperties(
                new UpdateSpreadsheetPropertiesRequest().setFields(
                        "title").setProperties(properties)));

        requestBody.setRequests(requests);

        Objects.requireNonNull(SERVICE_SHEETS)
                .spreadsheets()
                .batchUpdate(spreadsheetId, requestBody)
                .execute();
    }

}