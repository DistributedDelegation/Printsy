package gateway.resolver;

import gateway.dto.TransactionResult;
import gateway.service.ClientNodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import org.springframework.graphql.data.method.annotation.Argument;

import java.util.List;

@Controller
public class Mutation {

    private final ClientNodeService clientNodeService;

    @Autowired
    public Mutation(ClientNodeService clientNodeService) {
        this.clientNodeService = clientNodeService;
    }

    @MutationMapping
    public List<TransactionResult> validateTransaction(@Argument Long imageId) {
        return clientNodeService.vali(imageId);
    }

    @MutationMapping
    public int checkImageTransactionCount(@Argument Long imageId) {
        return clientNodeService.checkImageCount(imageId);
    }

}
