package com.vg.raiddataparser.parser;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.vg.raiddataparser.model.Champion;
import com.vg.raiddataparser.model.Skill;
import com.vg.raiddataparser.repository.ChampionRepository;
import com.vg.raiddataparser.repository.SkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

@Component
public class DataParser {

    @Autowired
    private ChampionRepository championRepository;
    @Autowired
    private SkillRepository skillRepository;

    private static final String JSON_DATA_URL = "https://raw.githubusercontent.com/Da-Teach/RaidStaticData/master/static_data.json";
    private static final String JSON_CHAMPION_DATA_NODE = "HeroData";
    private static final String JSON_CHAMPIONS_NODE = "HeroTypes";
    private static final String JSON_SKILL_DATA_NODE = "SkillData";
    private static final String JSON_SKILLS_NODE = "SkillTypes";
    private static final String JSON_STATIC_DATA_LOCALIZATION_NODE = "StaticDataLocalization";

    // Method called after bean initialization
    @PostConstruct
    public void parseData() {

        InputStream inputStream = null;
        try {
            inputStream = new URL(JSON_DATA_URL).openStream();
        } catch (MalformedURLException e) {
            System.out.println("MalformedURLException");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Error while getting JSON from specified URL");
            e.printStackTrace();
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(inputStream);

            parseChampionData(rootNode, mapper);
            //parseSkillData(rootNode, mapper);
        } catch (IOException e) {
            System.out.println("Error while parsing JSON from file");
            e.printStackTrace();
        }
    }

    private void parseChampionData(JsonNode rootNode, ObjectMapper mapper) {

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

                Champion champion = new Champion(
                        championId,
                        championName,
                        championAffinity,
                        championRole,
                        championFaction,
                        championRarity);

                championRepository.save(champion);

                // Get SkillData node
                JsonNode nodeSkillData = rootNode.get(JSON_SKILL_DATA_NODE);
                ArrayNode nodeSkills = (ArrayNode) nodeSkillData.get(JSON_SKILLS_NODE);

                try {
                    // Get the Champion's skills IDs as a List
                    List<Integer> championSkillsIds = mapper.readValue(nodeChampion.findPath("SkillTypeIds").toString(), new TypeReference<List<Integer>>() {});

                    for (int championSkillId : championSkillsIds) {

                        for (int i = 0; i < nodeSkills.size(); i++) {

                            // Create a new Skill for skill ID found
                            if (nodeSkills.get(i).findPath("Id").intValue() == championSkillId) {
                                skillRepository.save(createSkill(rootNode, nodeSkills.get(i), champion));

                                // Remove node to have less nodes to loop through in the next iteration
                                nodeSkills.remove(i);
                            }
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Error while parsing champion's skills for champion id: " + championId);
                    e.printStackTrace();
                }
            }
        }
    }

    private Skill createSkill(JsonNode rootNode, JsonNode nodeSkill, Champion champion) {

        int skillId = nodeSkill.get("Id").intValue();
        int skillRevision = nodeSkill.get("Revision").intValue();
        int skillCooldown = nodeSkill.get("Cooldown").intValue();
        String skillMultiplierFormula = nodeSkill.findPath("MultiplierFormula").textValue();

        // Get the StaticDataLocalization node for the skillName and skillDescription values
        JsonNode nodeStaticDataLocalization = rootNode.get(JSON_STATIC_DATA_LOCALIZATION_NODE);
        String skillNameKey = nodeSkill.get("Name").get("Key").textValue();
        String skillName = nodeStaticDataLocalization.findPath(skillNameKey).textValue();
        String skillDescriptionKey = nodeSkill.get("Description").get("Key").textValue();
        String skillDescription = nodeStaticDataLocalization.findPath(skillDescriptionKey).textValue();

        return new Skill(
                skillId,
                skillRevision,
                skillName,
                skillDescription,
                skillCooldown,
                skillMultiplierFormula,
                champion
        );
    }

}
