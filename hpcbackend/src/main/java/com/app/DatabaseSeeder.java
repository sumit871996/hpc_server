package com.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.app.dao.UsersRepository;
import com.app.entities.Users;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    private UsersRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.database.seed}")
    private boolean isDatabaseSeedingEnabled;

    @Override
    public void run(String... args) throws Exception {
        // Check if the database is empty
        if (userRepository.count() == 0 && isDatabaseSeedingEnabled) {
            // Create sample users
            Users user1 = new Users();
            user1.setUsername("user1");
            user1.setPassword(passwordEncoder.encode("password1"));

            Users user2 = new Users();
            user2.setUsername("user2");
            user2.setPassword(passwordEncoder.encode("password2"));

            // Save the users
            userRepository.save(user1);
            userRepository.save(user2);
        }
    }
}
