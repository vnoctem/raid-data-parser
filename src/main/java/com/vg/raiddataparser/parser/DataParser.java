package com.vg.raiddataparser.parser;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.vg.raiddataparser.googleservices.SpreadsheetRaidData;
import com.vg.raiddataparser.model.Skill;
import com.vg.raiddataparser.model.champion.Champion;
import com.vg.raiddataparser.repository.ChampionRepository;
import com.vg.raiddataparser.repository.SkillRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataParser {

    @Autowired
    private ChampionRepository championRepository;
    @Autowired
    private SkillRepository skillRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(DataParser.class.getName());

    private static final String JSON_DATA_URL = "https://raw.githubusercontent.com/Da-Teach/RaidStaticData/master/static_data.json";
    private static final String JSON_CHAMPION_DATA_NODE = "HeroData";
    private static final String JSON_CHAMPIONS_NODE = "HeroTypes";
    private static final String JSON_SKILL_DATA_NODE = "SkillData";
    private static final String JSON_SKILLS_NODE = "SkillTypes";
    private static final String JSON_STATIC_DATA_LOCALIZATION_NODE = "StaticDataLocalization";

    private SpreadsheetRaidData spreadsheetRaidData;

    // Method called after bean initialization
    @PostConstruct
    private void parseData() {

        spreadsheetRaidData = new SpreadsheetRaidData();

        InputStream inputStream = null;
        try {
            inputStream = new URL(JSON_DATA_URL).openStream();
        } catch (MalformedURLException e) {
            LOGGER.error("URL is invalid: MalformedURLException", e);
        } catch (IOException e) {
            LOGGER.error("Error while getting JSON from specified URL", e);
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(inputStream);
            parseChampionData(rootNode, mapper);
        } catch (IOException e) {
            LOGGER.error("Error while parsing JSON from file", e);
        }
    }

    private void parseChampionData(JsonNode rootNode, ObjectMapper mapper) {
        LOGGER.info("Parsing data...");

        JsonNode nodeChampionData = rootNode.get(JSON_CHAMPION_DATA_NODE);
        ArrayNode nodeChampions = (ArrayNode) nodeChampionData.get(JSON_CHAMPIONS_NODE);

        for (JsonNode nodeChampion : nodeChampions) {
            // Filter out unwanted champions
            // - with no AwakenMaterials (not fully ascended, where applicable)
            // - with faction values different than 0 (bosses, demon lord, pve waves)
            if (!nodeChampion.has("AwakenMaterials") && nodeChampion.get("Fraction").intValue() != 0) {

                int championId = nodeChampion.get("Id").intValue();

                // Get the StaticDataLocalization node for the championName
                JsonNode nodeStaticDataLocalization = rootNode.get(JSON_STATIC_DATA_LOCALIZATION_NODE);
                String championNameKey = nodeChampion.get("Name").get("Key").textValue();
                String championName = nodeStaticDataLocalization.findPath(championNameKey).textValue();

                int championAffinity = nodeChampion.get("Element").intValue();
                int championRole = nodeChampion.get("Role").intValue();
                int championFaction = nodeChampion.get("Fraction").intValue();
                int championRarity = nodeChampion.get("Rarity").intValue();

                JsonNode nodeBaseStats = nodeChampion.get("BaseStats");
                long championHealth = nodeBaseStats.get("Health").longValue();
                long championAttack = nodeBaseStats.get("Attack").longValue();
                long championDefense = nodeBaseStats.get("Defence").longValue();
                long championSpeed = nodeBaseStats.get("Speed").longValue();
                long championResistance = nodeBaseStats.get("Resistance").longValue();
                long championAccuracy = nodeBaseStats.get("Accuracy").longValue();
                long championCriticalChance = nodeBaseStats.get("CriticalChance").longValue();
                long championCriticalDamage = nodeBaseStats.get("CriticalDamage").longValue();
                long championCriticalHeal = nodeBaseStats.get("CriticalHeal").longValue();

                Champion champion = new Champion.Builder()
                        .setId(championId)
                        .setName(championName)
                        .setAffinity(championAffinity)
                        .setRole(championRole)
                        .setFaction(championFaction)
                        .setRarity(championRarity)
                        .setHealth(calculateScalableStatValue(championHealth) * 15) // for HP only, multiply by 15
                        .setAttack(calculateScalableStatValue(championAttack))
                        .setDefense(calculateScalableStatValue(championDefense))
                        .setSpeed(calculateBaseStatValue(championSpeed))
                        .setResistance(calculateBaseStatValue(championResistance))
                        .setAccuracy(calculateBaseStatValue(championAccuracy))
                        .setCriticalChance(calculateBaseStatValue(championCriticalChance))
                        .setCriticalDamage(calculateBaseStatValue(championCriticalDamage))
                        .setCriticalHeal(calculateBaseStatValue(championCriticalHeal))
                        .build();

                // TODO: review (save champion in DB)
                //championRepository.save(champion);

                // Get SkillData node
                JsonNode nodeSkillData = rootNode.get(JSON_SKILL_DATA_NODE);
                ArrayNode nodeSkills = (ArrayNode) nodeSkillData.get(JSON_SKILLS_NODE);

                try {
                    // Get the Champion's skills IDs as a List
                    // (using findPath() because get() will cause an error if specified node doesn't exist
                    List<Integer> championSkillsIds = mapper.readValue(nodeChampion.findPath("SkillTypeIds").toString(),
                            new TypeReference<List<Integer>>() {});
                    List<Skill> championSkills = new ArrayList<>();

                    for (int championSkillId : championSkillsIds) {
                        for (int i = 0; i < nodeSkills.size(); i++) {

                            // Create a new Skill for skill ID found
                            if (nodeSkills.get(i).findPath("Id").intValue() == championSkillId) {

                                // TODO: review (save skill in DB)
                                // Save Skill in database
                                //skillRepository.save(createSkill(rootNode, nodeSkills.get(i), champion));
                                Skill skill = createSkill(rootNode, nodeSkills.get(i), champion);

                                championSkills.add(skill);
                                spreadsheetRaidData.addSkillToValues(skill);

                                // Remove node to have less nodes to loop through in the next iteration
                                nodeSkills.remove(i);
                            }
                        }
                    }

                    champion.setSkills(championSkills);
                    spreadsheetRaidData.addChampionToValues(champion);
                    spreadsheetRaidData.addMultiplierToValues(champion);

                } catch (IOException e) {
                    LOGGER.error("Error while parsing champion's skills for champion (ID, name): "
                                    + championId
                                    + ", "
                                    + championName,
                            e);
                }
            }
        }

        try {
            if (spreadsheetRaidData.isUpdating()) {
                spreadsheetRaidData.updateMultiplierData();
                spreadsheetRaidData.updateChampionData();
                spreadsheetRaidData.updateSkillData();
            } else {
                spreadsheetRaidData.writeMultiplierDataToSheet();
                spreadsheetRaidData.writeChampionDataToSheet();
                spreadsheetRaidData.writeSkillDataToSheet();
                spreadsheetRaidData.addBandingToSheets();
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }

        LOGGER.info("Data parsing completed");
    }

    private Skill createSkill(JsonNode rootNode, JsonNode nodeSkill, Champion champion) {

        int skillId = nodeSkill.get("Id").intValue();
        int skillRevision = nodeSkill.get("Revision").intValue();
        int skillCooldown = nodeSkill.get("Cooldown").intValue();
        int skillVisibility = nodeSkill.get("Visibility").intValue();
        int skillGroup = nodeSkill.get("Group").intValue();
        String skillMultiplierFormula = nodeSkill.findPath("MultiplierFormula").textValue();

        // Get the StaticDataLocalization node for the skillName and skillDescription values
        JsonNode nodeStaticDataLocalization = rootNode.get(JSON_STATIC_DATA_LOCALIZATION_NODE);
        String skillNameKey = nodeSkill.get("Name").get("Key").textValue();
        String skillName = nodeStaticDataLocalization.findPath(skillNameKey).textValue();
        String skillDescriptionKey = nodeSkill.get("Description").get("Key").textValue();
        String skillDescription = nodeStaticDataLocalization.findPath(skillDescriptionKey).textValue();

        return new Skill.Builder()
                .setId(skillId)
                .setRevision(skillRevision)
                .setName(skillName)
                .setDescription(skillDescription)
                .setCooldown(skillCooldown)
                .setMultiplierFormula(skillMultiplierFormula)
                .setVisibility(skillVisibility)
                .setGroup(skillGroup)
                .setChampion(champion)
                .build();
    }

    /* For scalable stats (health, attack, defence)
     * Formula: BASE STAT * MULTIPLIER_1 * MULTIPLIER_2
     * Ascension level | MULTIPLIER_1 | MULTIPLIER_2
     *        1        |  1           |  2
     *        2        |  1.60000002  |  1.89999998
     *        3        |  2.43199992  |  1.79999995
     *        4        |  3.50207996  |  1.70000005
     *        5        |  4.76282883  |  1.70000005
     *        6        |  6.47744703  |  1.70000005
     */
    private int calculateScalableStatValue(long scalableStat) {
        return (int) Math.round(calculateBaseStatValue(scalableStat) * 6.47744703 * 1.70000005);
    }

    private int calculateBaseStatValue(long stat) {
        return (int) (stat / (Integer.MAX_VALUE * 2L - 1));
    }

}
