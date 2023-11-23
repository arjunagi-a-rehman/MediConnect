package com.Arjunagi.DoctorApp.services;

import com.Arjunagi.DoctorApp.models.authTokens.DefaultAuthToken;
import com.Arjunagi.DoctorApp.models.dtos.AuthInpDto;
import com.Arjunagi.DoctorApp.models.users.User;
import com.Arjunagi.DoctorApp.repository.IDefaultAuthTokenRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DefaultAuthTokenServices {
    @Autowired
    IDefaultAuthTokenRepo defaultAuthTokenRepo;

    public String getAuthToken(User user) {
        DefaultAuthToken authToken=new DefaultAuthToken(user);
        defaultAuthTokenRepo.save(authToken);
        return authToken.getValue();
    }
    private DefaultAuthToken getTokenFromValue(String value){
        return defaultAuthTokenRepo.findByValue(value);
    }

    public User getUserForValidToken(String value) {
        DefaultAuthToken  authToken= getTokenFromValue(value);
        if(authToken==null)return null;
        return authToken.getUser();
    }
    boolean verifyRoleAndEmailUserInpDto(User user, AuthInpDto inpDto){
        return user.getEmail().equals(inpDto.getEmail()) && user.getRole().equals(inpDto.getRole());
    }
    public boolean deleteToken(AuthInpDto inpDto) {
        DefaultAuthToken authToken=getTokenFromValue(inpDto.getTokenValue());
        if(verifyRoleAndEmailUserInpDto(authToken.getUser(),inpDto)){
            defaultAuthTokenRepo.delete(authToken);
            return true;
        }
        return false;
    }

    public void deleteTokenForUser(User user) {
        DefaultAuthToken authToken = defaultAuthTokenRepo.findByUser(user);
        if(authToken!=null) defaultAuthTokenRepo.delete(authToken);
    }
}
