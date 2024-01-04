package gateway.resolver;

import gateway.dto.TransactionInput;
import gateway.dto.TransactionResult;
import gateway.model.Transaction;
import gateway.service.ClientNodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import org.springframework.graphql.data.method.annotation.Argument;
import worker.WorkerMessages;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Controller
public class Mutation {

    private final ClientNodeService clientNodeService;
    private static final Logger logger = Logger.getLogger(ClientNodeService.class.getName());

    @Autowired
    public Mutation(ClientNodeService clientNodeService) {
        this.clientNodeService = clientNodeService;
    }

    @MutationMapping
    public boolean validateTransaction(@Argument List<TransactionInput> transactionInputs) {

        boolean allValid;
        try {
            List<Boolean> transactionResults = new ArrayList<Boolean>();
            for (TransactionInput transactionInput : transactionInputs) {
                transactionResults.add(clientNodeService.validateTransaction(transactionInput.toTransaction()));
            }
            allValid = transactionResults.stream().allMatch(b -> b);

        } catch (Exception e) {
            allValid = false;
        }
        return allValid;
    }

    @MutationMapping
    public TransactionResult completeTransaction(@Argument List<TransactionInput> transactionInputs) {
        for (TransactionInput input : transactionInputs){
            logger.info("Transaction received: " + input);
        }
        List<Transaction> transactions = transactionInputs.stream().map(TransactionInput::toTransaction).toList();
        return clientNodeService.completeTransaction(transactions);
    }
}
