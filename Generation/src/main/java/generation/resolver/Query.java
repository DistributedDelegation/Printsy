package generation.resolver;

import generation.model.Image;
import generation.model.Feature;
import generation.repository.ImageMySQLRepository;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

// The query should match the Query defined in the schema.
// This is similar to a controller for only GET requests.
@Controller
public class Query {

    // This top part initializes the repository class and "autowires" (essentially
    // injects them) into this class.
    private final ImageMySQLRepository sqlRepository;

    @Autowired
    public Query(ImageMySQLRepository sqlRepository) {
        this.sqlRepository = sqlRepository;
    }

    @QueryMapping
    public List<Feature> fetchFeatures() {
        return Arrays.asList(
            new Feature("Style", Arrays.asList("Abstract", "Realistic", "Cartoon", "Minimalist")),
            new Feature("Color Palette", Arrays.asList("Vibrant", "Pastel", "Monochrome", "Earth Tones")),
            new Feature("Theme", Arrays.asList("Nature", "Urban", "Space", "Fantasy")),
            new Feature("Mood", Arrays.asList("Joyful", "Serene", "Mysterious", "Energetic")),
            new Feature("Subject Focus", Arrays.asList("Animals", "People", "Landscapes", "Objects")),
            new Feature("Composition", Arrays.asList("Centralized", "Asymmetrical", "Layered", "Framed")),
            new Feature("Texture", Arrays.asList("Smooth", "Rough", "Patterned", "Glowing")),
            new Feature("Era", Arrays.asList("Vintage", "Contemporary", "Futuristic", "Historical")),
            new Feature("Cultural Influence", Arrays.asList("Asian", "Western", "African", "Indigenous")),
            new Feature("Graphic Elements", Arrays.asList("Geometric Shapes", "Floral Patterns", "Gradients", "Line Art"))
        );
    }
}