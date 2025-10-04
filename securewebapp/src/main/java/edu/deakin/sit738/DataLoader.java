package edu.deakin.sit738;

import edu.deakin.sit738.entity.User;
import edu.deakin.sit738.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {
    private final UserRepository userRepository;
    public DataLoader(UserRepository userRepository){this.userRepository = userRepository;}
    @Override
    public void run(String... args) throws Exception {
        if(userRepository.count()==0){
            userRepository.save(new User("alice","{noop}password","ROLE_USER","alice@example.com"));
            userRepository.save(new User("bob","{noop}password","ROLE_ADMIN","bob@example.com"));
            userRepository.save(new User("carol","{noop}password","ROLE_AUDITOR","carol@example.com"));
        }
    }
}
