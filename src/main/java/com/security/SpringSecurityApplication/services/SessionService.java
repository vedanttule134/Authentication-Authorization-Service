package com.security.SpringSecurityApplication.services;

import com.security.SpringSecurityApplication.entities.Sessions;
import com.security.SpringSecurityApplication.entities.Users;
import com.security.SpringSecurityApplication.repositories.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;
    private final int SESSION_LIMIT = 2;

    public void generateNewSession(Users user, String refreshToken) {
        List<Sessions> userSessions = sessionRepository.findByUser(user);

        if (userSessions.size() == SESSION_LIMIT){
            userSessions.sort(Comparator.comparing(Sessions::getLastUsedAt));
            Sessions oldSession = userSessions.getFirst();
            sessionRepository.delete(oldSession);
        }

        Sessions newSession = Sessions.builder()
                .user(user)
                .refreshToken(refreshToken)
                .build();

        sessionRepository.save(newSession);
    }

    public void validateSession(String refreshToken) {
        Sessions session = sessionRepository.findByRefreshToken(refreshToken);
        session.setLastUsedAt(LocalDateTime.now());
        sessionRepository.save(session);
    }
}
