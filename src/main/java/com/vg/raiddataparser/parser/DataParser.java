package com.vg.raiddataparser.parser;

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

    // Method called after bean initialization
    @PostConstruct
    public void parseData() {
        System.out.println("TESTVGR parseData BEGIN");

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

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = null;
        try {
            rootNode = mapper.readTree(inputStream);
        } catch (IOException e) {
            System.out.println("Error while parsing JSON from file");
            e.printStackTrace();
        }
        assert rootNode != null : "Error: root node is null";

        //parseChampionData(rootNode);
        parseSkillData(rootNode);
    }

    private void parseChampionData(JsonNode rootNode) {

        System.out.println("TESTVGR parseChampionData BEGIN");

        JsonNode nodeChampionData = rootNode.get(JSON_CHAMPION_DATA_NODE);
        ArrayNode nodeChampions = (ArrayNode) nodeChampionData.get(JSON_CHAMPIONS_NODE);

        for (JsonNode nodeChampion:nodeChampions) {

            // Filter out unwanted champions
            // - with no AwakenMaterials (not fully ascended, where applicable)
            // - with faction values different than 0 (bosses, demon lord, pve waves)
            if (!nodeChampion.has("AwakenMaterials")
                    && nodeChampion.get("Fraction").intValue() != 0) {

                int championId = nodeChampion.get("Id").intValue();
                String championName = nodeChampion.get("Name").get("DefaultValue").textValue();
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
                //System.out.println(champion.toString());
            }
        }
    }

    private void parseSkillData(JsonNode rootNode) {

        System.out.println("TESTVGR parseSkillData BEGIN");

        JsonNode nodeSkillData = rootNode.get(JSON_SKILL_DATA_NODE);
        ArrayNode nodeSkills = (ArrayNode) nodeSkillData.get(JSON_SKILLS_NODE);

        for (JsonNode nodeSkill:nodeSkills) {

            int skillId = nodeSkill.get("Id").intValue();
            int skillRevision = nodeSkill.get("Revision").intValue();
            // FIXME Get the StaticDataLocalization for the skillName and skillDescription values
            String skillName = nodeSkill.get("Name").get("DefaultValue").textValue();
            String skillDescription = nodeSkill.get("Description").get("DefaultValue").textValue();
            int skillCooldown = nodeSkill.get("Cooldown").intValue();
            String skillMultiplierFormula = nodeSkill.findPath("MultiplierFormula").textValue();

            Skill skill = new Skill(
                    skillId,
                    skillRevision,
                    skillName,
                    skillDescription,
                    skillCooldown,
                    skillMultiplierFormula
            );

            skillRepository.save(skill);
            //System.out.println(skill.toString());
        }
    }

}
