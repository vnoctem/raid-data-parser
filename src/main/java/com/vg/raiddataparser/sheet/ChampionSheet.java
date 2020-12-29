package com.vg.raiddataparser.sheet;

import com.google.api.services.sheets.v4.model.CellData;
import com.google.api.services.sheets.v4.model.ExtendedValue;
import com.google.api.services.sheets.v4.model.RowData;
import com.vg.raiddataparser.model.champion.Champion;
import com.vg.raiddataparser.model.champion.attributes.ChampionAffinity;
import com.vg.raiddataparser.model.champion.attributes.ChampionFaction;
import com.vg.raiddataparser.model.champion.attributes.ChampionRarity;
import com.vg.raiddataparser.model.champion.attributes.ChampionRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a sheet for champions
 */
public class ChampionSheet extends RaidSheet {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChampionSheet.class.getName());
    private static final String TITLE = "Champions";
    private static final int INDEX = 1;

    public ChampionSheet() {
        super(TITLE, INDEX);
        super.values = new ArrayList<>();
    }

    @Override
    protected RowData createHeaderRow() {
        RowData headerRow = new RowData();
        List<CellData> cellDataValues = new ArrayList<>();
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
            cellDataValues.add(new CellData().setUserEnteredValue(new ExtendedValue().setStringValue(s)));
        }

        return headerRow.setValues(cellDataValues);
    }

    @Override
    public void addValueToList(Object o) {
        Champion c = (Champion) o;

        // Add champion only if:
        // - name is not empty
        // - name does not contains "hero"
        if (!c.getName().trim().isEmpty() && !c.getName().toLowerCase().contains("hero")) {
            LOGGER.info("Adding champion to list: " + c.getName());

            values.add(Arrays.asList(
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

}