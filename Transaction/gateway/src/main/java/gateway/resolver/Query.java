package gateway.resolver;

import gateway.model.Transaction;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

@Controller
public class Query {

    @QueryMapping
    public List<Transaction> getTransactionsForImage(@Argument String imageId){
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction("Alice", "Bob", imageId));
        transactions.add(new Transaction("Charlie", "David", imageId));
        return transactions;
    }
}
