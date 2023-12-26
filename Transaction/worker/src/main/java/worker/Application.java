package worker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        // Boot up the Spring context
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);

        WorkerNodeServer server = context.getBean(WorkerNodeServer.class);
        try {
            server.start();
            server.blockUntilShutdown();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
