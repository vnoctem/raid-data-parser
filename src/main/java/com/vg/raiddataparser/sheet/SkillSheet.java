package com.vg.raiddataparser.sheet;

import com.google.api.services.sheets.v4.model.CellData;
import com.google.api.services.sheets.v4.model.ExtendedValue;
import com.google.api.services.sheets.v4.model.RowData;
import com.vg.raiddataparser.model.Skill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SkillSheet extends RaidSheet {

    private static final Logger LOGGER = LoggerFactory.getLogger(SkillSheet.class.getName());
    private static final String TITLE = "Skills";

    public SkillSheet() {
        super(TITLE);
        super.values = new ArrayList<>();
    }

    @Override
    protected RowData createHeaderRow() {
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

    @Override
    public void addValueToList(Object o) {
        Skill s = (Skill) o;

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


}