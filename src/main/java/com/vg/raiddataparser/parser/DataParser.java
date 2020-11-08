package com.vg.raiddataparser.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.vg.raiddataparser.model.Champion;
import com.vg.raiddataparser.repository.ChampionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

@Component
public class DataParser {

    @Autowired
    private ChampionRepository championRepository;

    private static final String JSON_DATA_URL = "https://raw.githubusercontent.com/Da-Teach/RaidStaticData/master/static_data.json";

    // Method called after bean initialization
    @PostConstruct
    public void parseData() {
        System.out.println("TESTVGR parseData");
        parseChampionData();
    }

    public void parseChampionData() {

        // Load JSON file
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

        // Create root node
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = null;
        try {
            rootNode = mapper.readTree(inputStream);
        } catch (IOException e) {
            System.out.println("Error while parsing JSON from file");
            e.printStackTrace();
        }

        // Create model.Champion data node
        JsonNode nodeChampionData = rootNode.get("HeroData");
        ArrayNode nodeChampions = (ArrayNode) nodeChampionData.get("HeroTypes");

        ArrayList<Champion> arrChampions = new ArrayList<>();

        // Traverse children
        for (int i = 0; i < nodeChampions.size(); i++) {

            // Create a Champion for each child without the field "AwakenMaterials"
            // So we create one unique champion instance
            if (!nodeChampions.get(i).has("AwakenMaterials")) {

                JsonNode championNode = nodeChampions.get(i);

                int championId = championNode.get("Id").intValue();
                String championName = championNode.get("Name").get("DefaultValue").textValue();
                int championAffinity = championNode.get("Element").intValue();
                int championRole = championNode.get("Role").intValue();
                int championFaction = championNode.get("Fraction").intValue();
                int championRarity = championNode.get("Rarity").intValue();

                Champion champion = new Champion(
                        championId,
                        championName,
                        championAffinity,
                        championRole,
                        championFaction,
                        championRarity);

                arrChampions.add(champion);
            }
        }

        // Print selected node
        for (Champion champion:arrChampions) {
            championRepository.save(champion);
            System.out.println(champion);
        }
    }
}
