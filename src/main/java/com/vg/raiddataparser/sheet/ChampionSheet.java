package com.vg.raiddataparser.sheet;

import com.vg.raiddataparser.model.champion.Champion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Component
public class ChampionSheet extends RaidSheet {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChampionSheet.class.getName());

    private static final String TITLE = "Champions";
    private static final int INDEX = 1;
    private static final List<String> HEADER_ROW_VALUES = Arrays.asList(
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

    public ChampionSheet() {
        super(TITLE, INDEX, HEADER_ROW_VALUES);
        super.values = new ArrayList<>();
    }

    @Override
    public void addValueToList(Object o) {
        Champion c = (Champion) o;

        // Add champion only if:
        // - name is not empty
        // - name does not contain "hero"
        if (c.getName() != null
                && !c.getName().trim().isEmpty()
                && !c.getName().toLowerCase().contains("hero")) {
            LOGGER.info("Adding champion to list: " + c.getName());

            values.add(Arrays.asList(
                    c.getName(),
                    c.getFaction(),
                    c.getRarity(),
                    c.getAffinity(),
                    c.getRole(),
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

}