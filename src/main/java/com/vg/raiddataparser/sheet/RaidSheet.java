package com.vg.raiddataparser.sheet;

import com.google.api.services.sheets.v4.model.*;
import com.vg.raiddataparser.googleservices.sheets.GoogleSheetsService;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public abstract class RaidSheet {

    private final GoogleSheetsService sheetsService = new GoogleSheetsService();
    public List<List<Object>> values;
    private final String title;
    private final int index;

    public RaidSheet(String title, int index) {
        this.title = title;
        this.index = index;
    }

    /**
     * Create header row
     *
     * @return RowData: header row for sheet
     */
    protected abstract RowData createHeaderRow();

    /**
     * Add object to list of values (values to be written to the sheet)
     *
     * @param o Object to be added
     */
    public abstract void addValueToList(Object o);

    /**
     * Create a sheet with a frozen header row
     *
     * @return Sheet instance
     */
    public Sheet create() {
        SheetProperties properties = new SheetProperties();
        GridProperties gridProperties = new GridProperties().setFrozenRowCount(1);
        GridData gridData = new GridData().setRowData(Collections.singletonList(createHeaderRow()));

        properties
                .setTitle(title)
                .setGridProperties(gridProperties);

        Sheet sheet = new Sheet()
                .setProperties(properties)
                .setData(Collections.singletonList(gridData));

        //sheetId = sheet.getProperties().getSheetId();

        return sheet;
    }

    /**
     * Write values to sheet
     *
     * @param spreadsheetId Spreadsheet Id
     * @throws IOException when writing values in sheet (Sheet service)
     */
    public void writeValuesToSheet(String spreadsheetId) throws IOException {
        try {
            ValueRange body = new ValueRange().setValues(values);

            sheetsService.appendValues(spreadsheetId, title, body);
        } catch (IOException e) {
            throw new IOException("Error while writing to sheet " + title, e);
        }
    }

    /**
     * Update values
     *
     * @param spreadsheetId Spreadsheet Id
     * @throws IOException when updating values in sheet (Sheets service)
     */
    public void updateValues(String spreadsheetId) throws IOException {
        try {
            ValueRange body = new ValueRange().setValues(values);
            String range = title + "!A2:Z";

            sheetsService.updateValues(spreadsheetId, range, body);
        } catch (IOException e) {
            throw new IOException("Error while updating sheet " + title, e);
        }
    }

    public void addBanding(String spreadsheetId,
            Color headerColor,
            Color firstBandColor,
            Color secondBandColor) throws IOException {
        try {
            sheetsService.addBanding(spreadsheetId, index, title, headerColor, firstBandColor, secondBandColor);
        } catch (IOException e) {
            throw new IOException("Error while adding banding to sheet " + title, e);
        }
    }
}