package com.vg.raiddataparser.googleservices;

import com.google.api.services.sheets.v4.model.*;
import com.vg.raiddataparser.googleservices.drive.GoogleDriveService;
import com.vg.raiddataparser.googleservices.sheets.GoogleSheetsService;
import com.vg.raiddataparser.model.champion.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class SpreadsheetRaidData {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpreadsheetRaidData.class.getName());

    private static final String SPREADSHEET_ID_FILE_NAME = "/spreadsheet_id.txt";
    private static final String RESOURCES_PATH = "src/main/resources";
    private static final String SHEET_CHAMPIONS_TITLE = "Champions";
    private static final String SHEET_SKILLS_TITLE = "Skills";

    private final GoogleDriveService driveService = new GoogleDriveService();
    private final GoogleSheetsService sheetsService = new GoogleSheetsService();

    private final List<List<Object>> championValues = new ArrayList<>();

    public SpreadsheetRaidData() {
        initializeRaidData();
    }

    private void initializeRaidData() {

        File file = new File(RESOURCES_PATH + SPREADSHEET_ID_FILE_NAME);

        String title = "RSL - Champions' multipliers (last updated: " + getCurrentDateFormatyyyyMMdd() + ")";
        SpreadsheetProperties properties = new SpreadsheetProperties().setTitle(title);

        List<Sheet> sheets = new ArrayList<>();
        Sheet championSheet = new Sheet().setProperties(new SheetProperties().setTitle(SHEET_CHAMPIONS_TITLE));
        Sheet skillSheet = new Sheet().setProperties(new SheetProperties().setTitle(SHEET_SKILLS_TITLE));
        sheets.add(championSheet);
        sheets.add(skillSheet);

        try {
            if (file.exists()) { // spreadsheet_id.txt exists
                String spreadsheetId = readSpreadsheetId(file);

                if (driveService.fileExists(spreadsheetId)) { // spreadsheet with corresponding ID exists on Drive
                    LOGGER.info("Spreadsheet already exists on Drive");
                    //TODO: Update Raid data
                    LOGGER.info("Updating Raid data (TO DO)");
                } else { // spreadsheet with corresponding ID doesn't exist on Drive
                    LOGGER.info("Spreadsheet does not exist");
                    Spreadsheet result = sheetsService.createSpreadsheet(properties, sheets);
                    writeSpreadsheetId(file, result.getSpreadsheetId());

                    //populateSpreadsheet(result);
                }
            } else { // spreadsheet_id.txt doesn't exist
                LOGGER.info("Creating new local file " + file.getAbsolutePath());
                if (file.createNewFile()) {
                    Spreadsheet result = sheetsService.createSpreadsheet(properties, sheets);
                    writeSpreadsheetId(file, result.getSpreadsheetId());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error occurred when creating spreadsheet. Operation will be aborted.", e);
        }
    }

    public void addChampionValues(Champion c) throws IOException {
        LOGGER.info("Creating champion values for " + c.getName() + "...");

        championValues.add(
                Arrays.asList(
                        c.getName(),
                        ChampionAffinity.getName(c.getAffinity()),
                        ChampionFaction.getName(c.getFaction()),
                        ChampionRole.getName(c.getRole()),
                        ChampionRarity.getName(c.getRarity())
                        /*c.getHealth(),
                        c.getAttack(),
                        c.getDefense(),
                        c.getSpeed(),
                        c.getResistance(),
                        c.getAccuracy(),
                        c.getCriticalChance(),
                        c.getCriticalDamage()*/
                ));
    }

    public void populateSheetChampion() throws IOException {
        String spreadsheetId = getSpreadsheetId();
        LOGGER.info("Populating champion data for spreadsheet " + spreadsheetId + "...");

        Spreadsheet spreadsheet = sheetsService.getSpreadsheet(spreadsheetId);

        ValueRange body = new ValueRange().setValues(championValues);
        sheetsService.writeValuesSingleRange(spreadsheet, body, "Champions");
    }

    private void populateSpreadsheet(Spreadsheet spreadsheet) {
        LOGGER.info("Populating spreadsheet " + spreadsheet.getSpreadsheetId() + "...");

        Champion championTest1 = new Champion.Builder()
                .setId(123)
                .setName("Champion1")
                .setFaction(1)
                .build();

        Champion championTest2 = new Champion.Builder()
                .setId(456)
                .setName("Champion2")
                .setFaction(1)
                .build();

        Champion championTest3 = new Champion.Builder()
                .setId(789)
                .setName("Champion3")
                .setFaction(1)
                .build();

        List<List<Object>> values = Arrays.asList(
                Arrays.asList(championTest1.getId(), championTest1.getName(), championTest1.getFaction()),
                Arrays.asList(championTest2.getId(), championTest2.getName(), championTest2.getFaction()),
                Arrays.asList(championTest3.getId(), championTest3.getName(), championTest3.getFaction())
        );

        ValueRange body = new ValueRange().setValues(values);
        try {
            sheetsService.writeValuesSingleRange(spreadsheet, body, "Champions");
        } catch (IOException e) {
            LOGGER.error("Error while populating sheet Champion", e);
        }

        //UpdateValuesResponse result = sheetsService.u



        /*try {
            // Rename default sheet title to Champions
            sheetsService.renameSheet(spreadsheet, new SheetProperties()
                    .setSheetId(spreadsheet.getSheets().get(0).getProperties().getSheetId())
                    .setTitle(SHEET_CHAMPIONS_TITLE));

            // Create sheet Skills
            sheetsService.createSheet(spreadsheet, new SheetProperties().setTitle(SHEET_SKILLS_TITLE));


        } catch (IOException e) {
            LOGGER.error("Error while creating sheet " + SHEET_CHAMPIONS_TITLE, e);
        }*/

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

    private String getSpreadsheetId() throws IOException {
        File file = new File(RESOURCES_PATH + SPREADSHEET_ID_FILE_NAME);
        return readSpreadsheetId(file);
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
