package inpeace.authenticationservice.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import inpeace.authenticationservice.dao.PasswordResetTokenRepository;
import inpeace.authenticationservice.dao.UserRepository;
import inpeace.authenticationservice.exception.UserValidationException;
import inpeace.authenticationservice.model.PasswordResetToken;
import inpeace.authenticationservice.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class EmailService {

    @Value("${sendgrid.api.key}")
    private String sendGridApiKey;

    @Value("${frontend.url}")
    private String frontEndUrl;

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    public EmailService(UserRepository userRepository, PasswordResetTokenRepository passwordResetTokenRepository) {
        this.userRepository = userRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    public void requestPasswordReset(String emailAddress) {
        User user = userRepository.findByEmailAddress(emailAddress)
                .orElseThrow(()-> new UserValidationException(List.of("User not found with the provided email address")));

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(user, token);
        passwordResetTokenRepository.save(resetToken);

        sendPasswordResetEmail(user.getEmailAddress(), token);
    }

    private void sendPasswordResetEmail(String emailAddress, String token){
        String resetUrl = "https://" + frontEndUrl + "/auth/reset-password?token=" + token;
        Email from = new Email("admin@inpeace.ie");
        Email to = new Email(emailAddress);
        String subject = "Password Reset Request";
        Content content = new Content("text/html", "Click <a href='" + resetUrl + "'>here</a> to reset your password.");
        System.out.println("Email content: Click <a href='" + resetUrl + "'>here</a> to reset your password.");
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(sendGridApiKey);

        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            System.out.println(response.getStatusCode());
            System.out.println(response.getBody());
            System.out.println(response.getHeaders());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
