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

    public RaidSheet(String title) {
        this.title = title;
    }

    /**
     * Create header row
     *
     * @return RowData: header row for sheet
     */
    public abstract RowData createHeaderRow();

    /**
     * Add object to List<List<Object>> values (values to be written to the sheet)
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

        return new Sheet()
                .setProperties(properties)
                .setData(Collections.singletonList(gridData));
    }

    /**
     * Write values to sheet
     *
     * @param spreadsheetId Spreadsheet Id
     * @throws IOException when writing values in sheet (Sheet service)
     */
    public void writeValuesToSheet(String spreadsheetId) throws IOException {
        Spreadsheet spreadsheet = sheetsService.getSpreadsheet(spreadsheetId);
        ValueRange body = new ValueRange().setValues(values);

        sheetsService.appendValues(spreadsheet, title, body);
    }

}