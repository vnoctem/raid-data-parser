package com.vg.raiddataparser.sheet;

import com.google.api.services.sheets.v4.model.*;
import com.vg.raiddataparser.googleservices.sheets.GoogleSheetsService;
import com.vg.raiddataparser.model.champion.Champion;
import com.vg.raiddataparser.model.champion.attributes.ChampionAffinity;
import com.vg.raiddataparser.model.champion.attributes.ChampionFaction;
import com.vg.raiddataparser.model.champion.attributes.ChampionRarity;
import com.vg.raiddataparser.model.champion.attributes.ChampionRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ChampionSheet {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChampionSheet.class.getName());
    private static final String TITLE = "Champions";

    private final GoogleSheetsService sheetsService = new GoogleSheetsService();
    private final List<List<Object>> championValues = new ArrayList<>();

    /**
     * Create a sheet with specific properties and header row
     *
     * @return Sheet instance for champions
     */
    public Sheet create() {
        LOGGER.info("Creating sheet for champions");

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
     * Add the values to the List<List<Object>> championValues
     *
     * @param c Champion to be added
     */
    public void addValuesToList(Champion c) {

        // Add champion only if:
        // - name is not empty
        // - name does not contains "hero"
        if (!c.getName().trim().isEmpty() && !c.getName().toLowerCase().contains("hero")) {
            LOGGER.info("Adding values to list for champion: " + c.getName());

            championValues.add(Arrays.asList(
                    c.getName(),
                    ChampionFaction.getName(c.getFaction()),
                    ChampionRarity.getName(c.getRarity()),
                    ChampionAffinity.getName(c.getAffinity()),
                    ChampionRole.getName(c.getRole()),
                    c.getHealth(),
                    c.getAttack(),
                    c.getDefense(),
                    c.getSpeed(),
                    c.getResistance(),
                    c.getAccuracy(),
                    c.getCriticalChance(),
                    c.getCriticalDamage(),
                    c.getCriticalHeal()
            ));
        }
    }

    /**
     * Populate the sheet with the Champion values via the Sheets service
     *
     * @throws IOException when updating values in sheet (Sheet service)
     */
    public void writeValuesToSheet(String spreadsheetId) throws IOException {
        LOGGER.info("Writing rows to sheet " + TITLE);

        Spreadsheet spreadsheet = sheetsService.getSpreadsheet(spreadsheetId);
        ValueRange body = new ValueRange().setValues(championValues);
        sheetsService.appendValues(spreadsheet, TITLE, body);
    }

    private RowData createHeaderRow() {
        LOGGER.info("Creating header row for champion sheet");

        RowData headerRow = new RowData();
        List<CellData> values = new ArrayList<>();
        List<String> stringValues = Arrays.asList(
                "Name",
                "Faction",
                "Rarity",
                "Affinity",
                "Role",
                "Health",
                "Attack",
                "Defense",
                "Speed",
                "Resistance",
                "Accuracy",
                "Critical Chance",
                "Critical Damage",
                "Critical Heal"
        );

        for (String s : stringValues) {
            values.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(s)));
        }

        return headerRow.setValues(values);
    }

}