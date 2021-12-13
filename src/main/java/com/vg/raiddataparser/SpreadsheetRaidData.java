package com.vg.raiddataparser;

import com.google.api.services.sheets.v4.model.Color;
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

    private static final Color HEADER_COLOR = new Color().setRed(1f).setGreen(0.7f).setBlue(0.2f);
    private static final Color FIRST_BAND_COLOR = new Color().setRed(0.92f).setGreen(0.92f).setBlue(0.95f);
    private static final Color SECOND_BAND_COLOR = new Color().setRed(1f).setGreen(1f).setBlue(1f);

    private final GoogleDriveService driveService = new GoogleDriveService();
    private final GoogleSheetsService sheetsService = new GoogleSheetsService();

    private final RaidSheet multiplierSheet = new MultiplierSheet();
    private final RaidSheet championSheet = new ChampionSheet();
    private final RaidSheet skillSheet = new SkillSheet();

    private boolean updating = false;

    private String spreadsheetId;

    public SpreadsheetRaidData() {
        initializeRaidData();
    }

    private void initializeRaidData() {
        File file = new File(RESOURCES_PATH + SPREADSHEET_ID_FILE_NAME);

        try {
            if (file.exists()) { // spreadsheet_id.txt exists
                spreadsheetId = readSpreadsheetId(file);

                if (driveService.fileExists(spreadsheetId)) { // spreadsheet with corresponding ID exists on Drive
                    LOGGER.info("Spreadsheet already exists on Drive");
                    updating = true;
                } else { // spreadsheet with corresponding ID doesn't exist on Drive
                    LOGGER.info("Spreadsheet does not exist");
                    createSpreadsheet(file);
                }
            } else { // spreadsheet_id.txt doesn't exist
                LOGGER.info("Creating new local file " + file.getAbsolutePath());
                if (file.createNewFile()) {
                    createSpreadsheet(file);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error occurred when creating spreadsheet. Operation will be aborted.", e);
        }
    }

    public void addBandingToSheets() throws IOException {
        multiplierSheet.addBanding(spreadsheetId, HEADER_COLOR, FIRST_BAND_COLOR, SECOND_BAND_COLOR);
        championSheet.addBanding(spreadsheetId, HEADER_COLOR, FIRST_BAND_COLOR, SECOND_BAND_COLOR);
        skillSheet.addBanding(spreadsheetId, HEADER_COLOR, FIRST_BAND_COLOR, SECOND_BAND_COLOR);
    }

    public boolean isUpdating() { return updating; }

    public void addMultiplierToValues(Champion champion) {
        multiplierSheet.addValueToList(champion);
    }

    public void writeMultiplierDataToSheet() throws IOException {
        multiplierSheet.writeValuesToSheet(getSpreadsheetId());
    }

    public void updateMultiplierData() throws IOException {
        multiplierSheet.updateValues(getSpreadsheetId());
    }

    public void addChampionToValues(Champion champion) {
        championSheet.addValueToList(champion);
    }

    public void writeChampionDataToSheet() throws IOException {
        championSheet.writeValuesToSheet(getSpreadsheetId());
    }

    public void updateChampionData() throws IOException {
        championSheet.updateValues(getSpreadsheetId());
    }

    public void addSkillToValues(Skill skill) {
        skillSheet.addValueToList(skill);
    }

    public void writeSkillDataToSheet() throws IOException {
        skillSheet.writeValuesToSheet(getSpreadsheetId());
    }

    public void updateSkillData() throws IOException {
        skillSheet.updateValues(getSpreadsheetId());
    }

    private void createSpreadsheet(File file) throws IOException {
        SpreadsheetProperties properties = new SpreadsheetProperties().setTitle(getUpdatedSpreadsheetTitle());
        List<Sheet> sheets = new ArrayList<>(Arrays.asList(
                multiplierSheet.create(),
                championSheet.create(),
                skillSheet.create()
        ));
        Spreadsheet result = sheetsService.createSpreadsheet(properties, sheets);
        spreadsheetId = result.getSpreadsheetId();

        writeSpreadsheetIdToFile(file);
    }

    public void updateSpreadsheet() throws IOException {
        LOGGER.info("Updating spreadsheet");
        try {
            // Rename spreadsheet
            sheetsService.renameSpreadsheet(spreadsheetId, getUpdatedSpreadsheetTitle());

            // Update sheets data
            multiplierSheet.updateValues(spreadsheetId);
            championSheet.updateValues(spreadsheetId);
            skillSheet.updateValues(spreadsheetId);

            // Update banding (if necessary)
            multiplierSheet.updateBanding(spreadsheetId, HEADER_COLOR, FIRST_BAND_COLOR, SECOND_BAND_COLOR);
            championSheet.updateBanding(spreadsheetId, HEADER_COLOR, FIRST_BAND_COLOR, SECOND_BAND_COLOR);
            skillSheet.updateBanding(spreadsheetId, HEADER_COLOR, FIRST_BAND_COLOR, SECOND_BAND_COLOR);
        } catch (IOException e) {
            throw new IOException("Error occurred when updating spreadsheet", e);
        }
    }

    private String getUpdatedSpreadsheetTitle() {
        return "RSL - Multipliers (last updated: " + getCurrentDateFormatyyyyMMdd() + ")";
    }

    private String getCurrentDateFormatyyyyMMdd() {
        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
        return dateFormatter.format(date);
    }

    private void writeSpreadsheetIdToFile(File f) throws IOException {
        LOGGER.info("Writing spreadsheet ID to file");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(f, false))) {
            writer.write(spreadsheetId);
        } catch (IOException e) {
            throw new IOException("Error occurred when writing spreadsheet ID to file \""
                    + RESOURCES_PATH + SPREADSHEET_ID_FILE_NAME
                    + "\". Operation will be aborted.", e);
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
                    + "\". Operation will be aborted.", e);
        }
    }

}