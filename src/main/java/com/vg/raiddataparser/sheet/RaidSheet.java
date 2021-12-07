package com.vg.raiddataparser.sheet;

import com.google.api.services.sheets.v4.model.*;
import com.vg.raiddataparser.googleservices.sheets.GoogleSheetsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class RaidSheet {

    private static final Logger LOGGER = LoggerFactory.getLogger(RaidSheet.class.getName());

    public List<List<Object>> values;
    private final String title;
    private final int index;
    private final List<String> headerRowValues;

    @Autowired
    private GoogleSheetsService sheetsService;

    public RaidSheet(String title, int index, List<String> headerRowValues) {
        this.title = title;
        this.index = index;
        this.headerRowValues = headerRowValues;
    }

    /**
     * Add object to list of values (values to be written to the sheet)
     *
     * @param o Object to be added
     */
    public abstract void addValueToList(Object o);

    /**
     * Create header row
     *
     * @return RowData: header row for sheet
     */
    protected RowData createHeaderRow() {
        LOGGER.info("Creating header row for sheet " + title);

        RowData headerRow = new RowData();
        List<CellData> cellDataValues = new ArrayList<>();

        for (String s : headerRowValues) {
            cellDataValues.add(new CellData()
                    .setUserEnteredValue(new ExtendedValue()
                            .setStringValue(s)
                    )
                    .setUserEnteredFormat(new CellFormat()
                            .setTextFormat(new TextFormat()
                                    .setBold(true)))
            );
        }

        return headerRow.setValues(cellDataValues);
    }

    /**
     * Create a sheet with a frozen header row
     *
     * @return Sheet instance
     */
    public Sheet create() {
        SheetProperties properties = new SheetProperties();
        GridProperties gridProperties = new GridProperties()
                .setFrozenRowCount(1)
                .setFrozenColumnCount(1);
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

    /**
     * Add banding (alternating colors) to the sheet
     * All rows except header row
     *
     * @param spreadsheetId   Spreadsheet Id
     * @param headerColor     Color for the header row
     * @param firstBandColor  Color for the first band
     * @param secondBandColor Color for the second band
     * @throws IOException when adding banding to the sheet
     */
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

    public void updateBanding(String spreadsheetId,
            Color headerColor,
            Color firstBandColor,
            Color secondBandColor) throws IOException {
        try {
            sheetsService.updateBanding(spreadsheetId, index, title, headerColor, firstBandColor, secondBandColor);
        } catch (IOException e) {
            throw new IOException("Error while updating banding to sheet " + title, e);
        }
    }

}