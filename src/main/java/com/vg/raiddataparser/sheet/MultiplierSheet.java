package com.vg.raiddataparser.sheet;

import com.google.api.services.sheets.v4.model.CellData;
import com.google.api.services.sheets.v4.model.ExtendedValue;
import com.google.api.services.sheets.v4.model.RowData;
import com.vg.raiddataparser.model.Skill;
import com.vg.raiddataparser.model.champion.Champion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MultiplierSheet extends RaidSheet {

    private static final Logger LOGGER = LoggerFactory.getLogger(MultiplierSheet.class.getName());
    private static final String TITLE = "Multipliers";

    public MultiplierSheet() {
        super(TITLE);
        super.values = new ArrayList<>();
    }

    @Override
    protected RowData createHeaderRow() {
        RowData headerRow = new RowData();
        List<CellData> cellDataValues = new ArrayList<>();
        List<String> stringValues = Arrays.asList(
                "Champion",
                "A1 Name",
                "A1 Multiplier",
                "A2 Name",
                "A2 Multiplier",
                "A3 Name",
                "A3 Multiplier",
                "A4 Name",
                "A4 Multiplier"
        );

        for (String s : stringValues) {
            cellDataValues.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(s)));
        }

        return headerRow.setValues(cellDataValues);
    }

    @Override
    public void addValueToList(Object o) {
        Champion c = (Champion) o;
        List<Object> rowValues = null;

        // Add multipliers only if:
        // - champion name is not empty
        // - champion name does not contains "hero"
        if (!c.getName().trim().isEmpty() && !c.getName().toLowerCase().contains("hero")) {
            LOGGER.info("Adding multipliers");

            rowValues = new ArrayList<>();
            rowValues.add(c.getName());

            for (Skill s : c.getSkills()) {
                if (s.getVisibility() == 0) {
                    rowValues.add(s.getName() == null ? "" : s.getName());
                    rowValues.add(s.getMultiplierFormula() == null ? "[None]" : s.getMultiplierFormula());
                }
            }
        }

        values.add(rowValues);
    }

}