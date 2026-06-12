package com.jpmc.midascore.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Balance;
import com.jpmc.midascore.repository.UserRepository;

@RestController
public class BalanceController {

    private final UserRepository userRepository;

    @Autowired
    public BalanceController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    

     // Change Long to lowercase "long" here!
    @GetMapping("/balance")
    public Balance getBalance(@RequestParam long userId) {
        
        // Now Java will match the correct JPMC method and the red line will vanish
        UserRecord user = userRepository.findById(userId);
        
        if (user != null) {
            return new Balance(user.getBalance());
        }
        
        return new Balance(0.0f);
    }

}