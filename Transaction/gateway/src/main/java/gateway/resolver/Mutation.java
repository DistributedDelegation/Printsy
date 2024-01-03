package gateway.resolver;

import gateway.dto.TransactionInput;
import gateway.dto.TransactionResult;
import gateway.model.Transaction;
import gateway.service.ClientNodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import org.springframework.graphql.data.method.annotation.Argument;

import java.util.ArrayList;
import java.util.List;

@Controller
public class Mutation {

    private final ClientNodeService clientNodeService;

    @Autowired
    public Mutation(ClientNodeService clientNodeService) {
        this.clientNodeService = clientNodeService;
    }

    @MutationMapping
    public boolean validateTransaction(@Argument List<TransactionInput> transactionInputs) {
        
        boolean allValid;
        try {
            List<Boolean> transactionResults = new ArrayList<Boolean>();
            for (TransactionInput transactionInput : transactionInputs){
                transactionResults.add(clientNodeService.validateTransaction(transactionInput.toTransaction()));
            }
            allValid = transactionResults.stream().allMatch(b -> b);

        } catch (Exception e){
            allValid = false;
        }
        return allValid; 
    }

    @MutationMapping
    public TransactionResult completeTransaction(@Argument List<TransactionInput> transactionInputs){
        List<Boolean> transactionResults = new ArrayList<Boolean>();
        for (TransactionInput transactionInput : transactionInputs){
            transactionResults.add(clientNodeService.(transactionInput.toTransaction()));
        }
    }

}
