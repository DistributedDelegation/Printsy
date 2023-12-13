package generation.model;

import java.util.List;

public class Feature {
    private String name;
    private List<String> options;

    public Feature(String name, List<String> options) {
        this.name = name;
        this.options = options;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }
}
