package com.vg.raiddataparser.sheet;

import com.vg.raiddataparser.model.Skill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SkillSheet extends RaidSheet {

    private static final Logger LOGGER = LoggerFactory.getLogger(SkillSheet.class.getName());

    private static final String TITLE = "Skills";
    private static final int INDEX = 2;
    private static final List<String> HEADER_ROW_VALUES = Arrays.asList(
            "Name",
            "Description",
            "Cooldown",
            "Multiplier",
            "Champion"
    );

    public SkillSheet() {
        super(TITLE, INDEX, HEADER_ROW_VALUES);
        super.values = new ArrayList<>();
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