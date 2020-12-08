package com.vg.raiddataparser.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.Set;

@Entity
public class Champion {

    @Id
    private int id;
    private String name;
    private int affinity;
    private int role;
    private int faction;
    private int rarity;
    private int health;
    private int attack;
    private int defense;
    private int speed;
    private int resistance;
    private int accuracy;
    private int criticalChance;
    private int criticalDamage;
    @OneToMany(mappedBy = "champion")
    private Set<Skill> skills;

    public Champion() {}

    public Champion(int id,
            String name,
            int affinity,
            int role,
            int faction,
            int rarity,
            int health,
            int attack,
            int defense,
            int speed,
            int resistance,
            int accuracy,
            int criticalChance,
            int criticalDamage,
            Set<Skill> skills) {
        this.id = id;
        this.name = name;
        this.affinity = affinity;
        this.role = role;
        this.faction = faction;
        this.rarity = rarity;
        this.health = health;
        this.attack = attack;
        this.defense = defense;
        this.speed = speed;
        this.resistance = resistance;
        this.accuracy = accuracy;
        this.criticalChance = criticalChance;
        this.criticalDamage = criticalDamage;
        this.skills = skills;
    }

    public int getId() { return id; }

    public String getName() { return name; }

    public int getAffinity() { return affinity; }

    public int getRole() { return role; }

    public int getFaction() { return faction; }

    public int getRarity() { return rarity; }

    public int getHealth() { return health; }

    public int getAttack() { return attack; }

    public int getDefense() { return defense; }

    public int getSpeed() { return speed; }

    public int getResistance() { return resistance; }

    public int getAccuracy() { return accuracy; }

    public int getCriticalChance() { return criticalChance; }

    public int getCriticalDamage() { return criticalDamage; }

    public Set<Skill> getSkills() { return skills; }

    @Override
    public String toString() {
        return "Champion{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", affinity=" + affinity +
                ", role=" + role +
                ", faction=" + faction +
                ", rarity=" + rarity +
                ", health=" + health +
                ", attack=" + attack +
                ", defense=" + defense +
                ", speed=" + speed +
                ", resistance=" + resistance +
                ", accuracy=" + accuracy +
                ", criticalChance=" + criticalChance +
                ", criticalDamage=" + criticalDamage +
                ", skills=" + skills +
                '}';
    }

    public static class Builder {

        private int id;
        private String name;
        private int affinity;
        private int role;
        private int faction;
        private int rarity;
        private int health;
        private int attack;
        private int defense;
        private int speed;
        private int resistance;
        private int accuracy;
        private int criticalChance;
        private int criticalDamage;
        private Set<Skill> skills;

        public Builder setId(int id) {
            this.id = id;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setAffinity(int affinity) {
            this.affinity = affinity;
            return this;
        }

        public Builder setRole(int role) {
            this.role = role;
            return this;
        }

        public Builder setFaction(int faction) {
            this.faction = faction;
            return this;
        }

        public Builder setRarity(int rarity) {
            this.rarity = rarity;
            return this;
        }

        public Builder setHealth(int health) {
            this.health = health;
            return this;
        }

        public Builder setAttack(int attack) {
            this.attack = attack;
            return this;
        }

        public Builder setDefense(int defense) {
            this.defense = defense;
            return this;
        }

        public Builder setSpeed(int speed) {
            this.speed = speed;
            return this;
        }

        public Builder setResistance(int resistance) {
            this.resistance = resistance;
            return this;
        }

        public Builder setAccuracy(int accuracy) {
            this.accuracy = accuracy;
            return this;
        }

        public Builder setCriticalChance(int criticalChance) {
            this.criticalChance = criticalChance;
            return this;
        }

        public Builder setCriticalDamage(int criticalDamage) {
            this.criticalDamage = criticalDamage;
            return this;
        }

        public Builder setSkills(Set<Skill> skills) {
            this.skills = skills;
            return this;
        }

        public Champion build() {
            return new Champion(id,
                    name,
                    affinity,
                    role,
                    faction,
                    rarity,
                    health,
                    attack,
                    defense,
                    speed,
                    resistance,
                    accuracy,
                    criticalChance,
                    criticalDamage,
                    skills);
        }
    }

}
