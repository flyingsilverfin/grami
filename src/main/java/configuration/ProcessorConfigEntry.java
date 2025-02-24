package configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class ProcessorConfigEntry {

    private static final Logger appLogger = LogManager.getLogger("com.bayer.dt.grami");

    private String processor;
    private String processorType;
    private String schemaType;

    // top-level HashMap has single entry "conceptGenerators"
    // second-level HashMap has two entries: "attributes" and "players" (for relations only)
    // third level HashMap has one entry per attribute/player generator
    private HashMap<String, HashMap<String, ConceptGenerator>> conceptGenerators;

    public String getProcessor() {
        return processor;
    }

    public String getProcessorType() {
        return processorType;
    }

    public String getSchemaType() {
        return schemaType;
    }

    public HashMap<String, HashMap<String, ConceptGenerator>> getConceptGenerators() {
        return conceptGenerators;
    }

    public ConceptGenerator getAttributeGenerator(String key) {
        if(getConceptGenerators().get("attributes") == null) {
            throw new RuntimeException("You have specified the attribute [" + key + "] in your dataConfig file - but there are no attributeGenerators specified in the corresponding processor.");
        }
        for (Map.Entry<String, ConceptGenerator> entry : getConceptGenerators().get("attributes").entrySet()) {
            if (entry.getKey().equals(key)) {
                return entry.getValue();
            }
        }
        throw new IllegalArgumentException("cannot find <" + key + "> under <conceptGenerators><attributes> in processor: " + getProcessor());
    }

    public ConceptGenerator getPlayerGenerator(String key) {
        for (Map.Entry<String, ConceptGenerator> entry : getConceptGenerators().get("players").entrySet()) {
            if (entry.getKey().equals(key)) {
                return entry.getValue();
            }
        }
        throw new IllegalArgumentException("cannot find <" + key + "> under <conceptGenerators><players> in processor: " + getProcessor());
    }

    public ConceptGenerator getRelationPlayerGenerator(String key) {
        for (Map.Entry<String, ConceptGenerator> entry : getConceptGenerators().get("relationPlayers").entrySet()) {
            if (entry.getKey().equals(key)) {
                return entry.getValue();
            }
        }
        throw new IllegalArgumentException("cannot find <" + key + "> under <conceptGenerators><players> in processor: " + getProcessor());
    }

    public HashMap<String,ConceptGenerator> getRelationRequiredPlayers() {
        HashMap<String,ConceptGenerator> relationPlayers = new HashMap<>();
        if (processorType.equals("relation") || processorType.equals("nested-relation") || processorType.equals("attribute-relation")) {
            HashMap<String, ConceptGenerator> playerGenerators = getConceptGenerators().get("players");
            for (Map.Entry<String, ConceptGenerator> pg: playerGenerators.entrySet()) {
                if (pg.getValue().isRequired()) {
                    relationPlayers.put(pg.getKey(), pg.getValue());
                }
            }
            if (getConceptGenerators().get("relationPlayers") != null) {
                HashMap<String, ConceptGenerator> relationPlayerGenerators = getConceptGenerators().get("relationPlayers");
                for (Map.Entry<String, ConceptGenerator> pg: relationPlayerGenerators.entrySet()) {
                    if (pg.getValue().isRequired()) {
                        relationPlayers.put(pg.getKey(), pg.getValue());
                    }
                }
            }
        }
        return relationPlayers;
    }

    public HashMap<String,ConceptGenerator> getRelationPlayers() {
        HashMap<String,ConceptGenerator> relationPlayers = new HashMap<>();
        if (processorType.equals("relation")) {
            HashMap<String, ConceptGenerator> playerGenerators = getConceptGenerators().get("players");
            for (Map.Entry<String, ConceptGenerator> pg : playerGenerators.entrySet()) {
                relationPlayers.put(pg.getKey(), pg.getValue());
            }
        }
        return relationPlayers;
    }

    public HashMap<String,ConceptGenerator> getAttributes() {
        HashMap<String,ConceptGenerator> attributes = new HashMap<>();
        HashMap<String, ConceptGenerator> attGenerators = getConceptGenerators().get("attributes");
        for (Map.Entry<String, ConceptGenerator> ag: attGenerators.entrySet()) {
            attributes.put(ag.getKey(), ag.getValue());
        }
        return attributes;
    }

    public HashMap<String,ConceptGenerator> getRequiredAttributes() {
        HashMap<String,ConceptGenerator> requiredAttributes = new HashMap<>();
        HashMap<String, ConceptGenerator> attGenerators = getConceptGenerators().get("attributes");
        if (attGenerators != null) {
            for (Map.Entry<String, ConceptGenerator> ag: attGenerators.entrySet()) {
                if (ag.getValue().isRequired()) {
                    requiredAttributes.put(ag.getKey(), ag.getValue());
                }
            }
        }
        return requiredAttributes;
    }

    public static class ConceptGenerator {

        // for attributes and players:
        private boolean required;

        // for attributes
        private String attributeType;
        private String valueType;

        // for players
        private String playerType;
        private String roleType;
        private String uniquePlayerId;
        private String idValueType;

        // for relationPlayers
        private HashMap<String, MatchBy> matchByPlayer;
        private HashMap<String, MatchBy> matchByAttribute;

        public boolean isRequired() {
            return required;
        }

        public String getAttributeType() {
            return attributeType;
        }

        public String getValueType() {
            return valueType;
        }

        public String getPlayerType() {
            return playerType;
        }

        public String getRoleType() {
            return roleType;
        }

        public String getUniquePlayerId() {
            return uniquePlayerId;
        }

        public String getIdValueType() {
            return idValueType;
        }

        public HashMap<String, MatchBy> getMatchByPlayer() { return matchByPlayer; }

        public HashMap<String, MatchBy> getMatchByAttribute() {
            return matchByAttribute;
        }

        public static class MatchBy {
            // for attributes and players:
            private boolean required;

            // for attributes
            private String attributeType;
            private String valueType;

            // for players
            private String playerType;
            private String roleType;
            private String uniquePlayerId;
            private String idValueType;

            public boolean isRequired() {
                return required;
            }

            public String getAttributeType() {
                return attributeType;
            }

            public String getValueType() {
                return valueType;
            }

            public String getPlayerType() {
                return playerType;
            }

            public String getRoleType() {
                return roleType;
            }

            public String getUniquePlayerId() {
                return uniquePlayerId;
            }

            public String getIdValueType() {
                return idValueType;
            }
        }
    }

}
