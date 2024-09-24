package org.samtuap.inong.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;
import org.samtuap.inong.common.exception.BaseCustomException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import javax.annotation.PostConstruct;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.samtuap.inong.common.exceptionType.NotificationExceptionType.INVALID_SECRET_FILE;
import static org.samtuap.inong.common.exceptionType.NotificationExceptionType.SECRET_FILE_NOT_FOUND;


@Slf4j
@Configuration
public class FcmConfig {
    @Value("${fcm.secret-file}")
    private String secretFileName;

    @PostConstruct
    public void initialize() {
        try {
            GoogleCredentials googleCredentials = GoogleCredentials
                    .fromStream(new ClassPathResource(secretFileName).getInputStream());
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(googleCredentials)
                    .build();
            FirebaseApp.initializeApp(options);
        } catch(FileNotFoundException e) {
            throw new BaseCustomException(SECRET_FILE_NOT_FOUND);
        } catch(IOException e) {
            throw new BaseCustomException(INVALID_SECRET_FILE);
        }

    }
}