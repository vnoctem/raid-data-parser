package com.vg.raiddataparser.sheet;

import com.vg.raiddataparser.model.Skill;
import com.vg.raiddataparser.model.champion.Champion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class MultiplierSheet extends RaidSheet {

    private static final Logger LOGGER = LoggerFactory.getLogger(MultiplierSheet.class.getName());

    private static final String TITLE = "Multipliers";
    private static final int INDEX = 0;
    private static final List<String> HEADER_ROW_VALUES = Arrays.asList(
            "Champion",
            "A1 Name",
            "A1 Multiplier",
            "A2 Name",
            "A2 Multiplier",
            "A3 Name",
            "A3 Multiplier",
            "A4 Name",
            "A4 Multiplier",
            "A5 Name",
            "A5 Multiplier"
    );

    public MultiplierSheet() {
        super(TITLE, INDEX, HEADER_ROW_VALUES);
        super.values = new ArrayList<>();
    }

    @Override
    public void addValueToList(Object o) {
        Champion c = (Champion) o;
        List<Object> rowValues = null;

        // Add multipliers only if:
        // - champion name is not empty
        // - champion name does not contain "hero"
        if (c.getName() != null
                && !c.getName().trim().isEmpty()
                && !c.getName().toLowerCase().contains("hero")) {
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