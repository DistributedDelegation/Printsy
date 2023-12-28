package gateway.resolver;

import gateway.dto.CountResult;
import gateway.model.Transaction;
import gateway.service.ClientNodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

@Controller
public class Query {

    private final ClientNodeService clientNodeService;

    @Autowired
    public Query(ClientNodeService clientNodeService) {
        this.clientNodeService = clientNodeService;
    }

    @QueryMapping
    public CountResult checkImageTransactionCount(@Argument Long imageId) {
        return new CountResult(clientNodeService.checkImageCount(imageId));
    }
}
