package gateway.resolver;

import gateway.dto.CountResult;
import gateway.service.ClientNodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.logging.Logger;

@Controller
public class Query {

    private final ClientNodeService clientNodeService;
    private final Logger LOGGER = Logger.getLogger(Query.class.getName());

    @Autowired
    public Query(ClientNodeService clientNodeService) {
        this.clientNodeService = clientNodeService;
    }

    @QueryMapping
    public CountResult checkImageTransactionCount(@Argument String imageId) {
        CountResult result = new CountResult(clientNodeService.checkImageCount(imageId));
        LOGGER.info("Result of query: " + result);
        return result;
    }
}
