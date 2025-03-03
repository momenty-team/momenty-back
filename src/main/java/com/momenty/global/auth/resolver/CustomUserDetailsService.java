package com.momenty.global.auth.resolver;

import com.momenty.global.exception.UserNotFoundException;
import com.momenty.user.domain.User;
import com.momenty.user.repository.UserRepository;
import java.util.Collection;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        Integer id = Integer.parseInt(userId);

        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        Collection<GrantedAuthority> authorities = Collections.emptyList();

        return new CustomUserDetails(user.getId());
    }
}