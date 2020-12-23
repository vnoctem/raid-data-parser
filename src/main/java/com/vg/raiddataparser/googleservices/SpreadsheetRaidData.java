package com.vg.raiddataparser.googleservices;

import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.SpreadsheetProperties;
import com.vg.raiddataparser.googleservices.drive.GoogleDriveService;
import com.vg.raiddataparser.googleservices.sheets.GoogleSheetsService;
import com.vg.raiddataparser.model.Skill;
import com.vg.raiddataparser.model.champion.Champion;
import com.vg.raiddataparser.sheet.ChampionSheet;
import com.vg.raiddataparser.sheet.MultiplierSheet;
import com.vg.raiddataparser.sheet.RaidSheet;
import com.vg.raiddataparser.sheet.SkillSheet;
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

    private final GoogleDriveService driveService = new GoogleDriveService();
    private final GoogleSheetsService sheetsService = new GoogleSheetsService();

    private RaidSheet multiplierSheet;
    private RaidSheet championSheet;
    private RaidSheet skillSheet;

    // TODO: review; maybe for dev purpose only
    //private boolean update = false;

    public SpreadsheetRaidData() {
        initializeRaidData();
    }

    private void initializeRaidData() {
        File file = new File(RESOURCES_PATH + SPREADSHEET_ID_FILE_NAME);

        String title = "RSL - Champions' multipliers (last updated: " + getCurrentDateFormatyyyyMMdd() + ")";
        SpreadsheetProperties properties = new SpreadsheetProperties().setTitle(title);

        multiplierSheet = new MultiplierSheet();
        championSheet = new ChampionSheet();
        skillSheet = new SkillSheet();


        List<Sheet> sheets = new ArrayList<>(Arrays.asList(
                multiplierSheet.create(),
                championSheet.create(),
                skillSheet.create()
        ));

        try {
            if (file.exists()) { // spreadsheet_id.txt exists
                String spreadsheetId = readSpreadsheetId(file);

                if (driveService.fileExists(spreadsheetId)) { // spreadsheet with corresponding ID exists on Drive
                    LOGGER.info("Spreadsheet already exists on Drive");
                    //TODO: Update Raid data
                    update = true;

                    LOGGER.info("Updating Raid data (TO DO)");
                } else { // spreadsheet with corresponding ID doesn't exist on Drive
                    LOGGER.info("Spreadsheet does not exist");
                    Spreadsheet result = sheetsService.createSpreadsheet(properties, sheets);
                    writeSpreadsheetId(file, result.getSpreadsheetId());
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

    public void addMultiplierToValues(Champion champion) {
        multiplierSheet.addValueToList(champion);
    }

    public void writeMultiplierDataToSheet() throws IOException {
        multiplierSheet.writeValuesToSheet(getSpreadsheetId());
    }

    public void addChampionToValues(Champion champion) {
        championSheet.addValueToList(champion);
    }

    public void writeChampionDataToSheet() throws IOException {
        championSheet.writeValuesToSheet(getSpreadsheetId());
    }

    public void addSkillToValues(Skill skill) {
        skillSheet.addValueToList(skill);
    }

    public void writeSkillDataToSheet() throws IOException {
        skillSheet.writeValuesToSheet(getSpreadsheetId());
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