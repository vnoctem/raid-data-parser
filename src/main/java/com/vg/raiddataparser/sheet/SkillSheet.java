package com.vg.raiddataparser.sheet;

import com.google.api.services.sheets.v4.model.*;
import com.vg.raiddataparser.googleservices.sheets.GoogleSheetsService;
import com.vg.raiddataparser.model.Skill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SkillSheet {

    private static final Logger LOGGER = LoggerFactory.getLogger(SkillSheet.class.getName());
    private static final String TITLE = "Skills";

    private final GoogleSheetsService sheetsService = new GoogleSheetsService();
    private final List<List<Object>> values = new ArrayList<>();

    /**
     * Create a sheet with specific properties and header row
     *
     * @return Sheet instance for skills
     */
    public Sheet create() {
        LOGGER.info("Creating sheet for skills");

        SheetProperties properties = new SheetProperties();
        GridProperties gridProperties = new GridProperties().setFrozenRowCount(1);
        GridData gridData = new GridData().setRowData(Collections.singletonList(createHeaderRow()));

        properties
                .setTitle(TITLE)
                .setGridProperties(gridProperties);

        return new Sheet()
                .setProperties(properties)
                .setData(Collections.singletonList(gridData));
    }

    /**
     * Add skill to list
     *
     * @param s Skill to be added
     */
    public void addValueToList(Skill s) {

        // Add skill only if:
        // - name is not empty
        // - name does not contain "skill" and "name"
        // - description is not empty
        if (!s.getName().trim().isEmpty()
                && !(s.getName().toLowerCase().contains("skill") && s.getName().toLowerCase().contains("name"))
                && !s.getDescription().trim().isEmpty()) {

            LOGGER.info("Adding skill to list: " + s.getName());

            values.add(Arrays.asList(
                    s.getName(),
                    s.getDescription(),
                    s.getCooldown(),
                    s.getMultiplierFormula() == null ? "" : s.getMultiplierFormula(),
                    s.getChampion().getName()
            ));
        }
    }

    /**
     * Populate the sheet with the Skill values via the Sheets service
     *
     * @throws IOException when updating values in sheet (Sheet service)
     */
    public void writeValuesToSheet(String spreadsheetId) throws IOException {
        LOGGER.info("Writing rows to sheet " + TITLE);

        Spreadsheet spreadsheet = sheetsService.getSpreadsheet(spreadsheetId);
        ValueRange body = new ValueRange().setValues(values);
        sheetsService.appendValues(spreadsheet, TITLE, body);
    }

    private RowData createHeaderRow() {
        LOGGER.info("Creating header row for skill sheet");

        RowData headerRow = new RowData();
        List<CellData> cellDataValues = new ArrayList<>();
        List<String> stringValues = Arrays.asList(
                "Name",
                "Description",
                "Cooldown",
                "Multiplier",
                "Champion"
        );

        for (String s : stringValues) {
            cellDataValues.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(s)));
        }

        return headerRow.setValues(cellDataValues);
    }


}
