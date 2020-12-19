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
                "A3 Multiplier"
        );

        for (String s : stringValues) {
            cellDataValues.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(s)));
        }

        return headerRow.setValues(cellDataValues);
    }

    @Override
    public void addValueToList(Object o) {

        //FIXME : Do not add hero and skills that have no names or are empty
        Champion c = (Champion) o;

        List<Object> rowValues =  new ArrayList<>();
        rowValues.add(c.getName());

        for (Skill s : c.getSkills()) {
            rowValues.add(s.getName() == null ? "" : s.getName());
            rowValues.add(s.getMultiplierFormula() == null ? "" : s.getMultiplierFormula());
        }

        LOGGER.info("Adding multipliers");

        values.add(rowValues);
    }

}