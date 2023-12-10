package TransactionGateway.resolver;

import TransactionGateway.dto.TransactionResult;
import TransactionGateway.service.ClientNodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import org.springframework.graphql.data.method.annotation.Argument;
@Controller
public class Mutation {

    private final ClientNodeService clientNodeService;

    @Autowired
    public Mutation(ClientNodeService clientNodeService) {
        this.clientNodeService = clientNodeService;
    }

    @MutationMapping
    public TransactionResult validateTransaction(@Argument String sender, @Argument String receiver, @Argument String imageId) {
        return clientNodeService.makeTestRequest(sender, receiver, imageId);
    }

}
