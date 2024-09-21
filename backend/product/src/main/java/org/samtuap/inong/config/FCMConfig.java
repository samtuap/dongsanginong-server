package org.samtuap.inong.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.samtuap.inong.common.exception.BaseCustomException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.samtuap.inong.common.exceptionType.NotificationExceptionType.*;

@Configuration
public class FCMConfig {
    @Value("fcm.secret-file")
    private String secretFileName;

    @PostConstruct
    public void initialize() {
        FileInputStream serviceAccount;
        try {
            serviceAccount = new FileInputStream(secretFileName);

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp.initializeApp(options);
        } catch(FileNotFoundException e) {
            throw new BaseCustomException(SECRET_FILE_NOT_FOUND);
        } catch(IOException e) {
            throw new BaseCustomException(INVALID_SECRET_FILE);
        }

    }
}
