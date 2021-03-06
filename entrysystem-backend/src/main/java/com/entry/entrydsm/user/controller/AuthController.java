package com.entry.entrydsm.user.controller;

import com.entry.entrydsm.common.config.AuthRequired;
import com.entry.entrydsm.common.response.JwtToken;
import com.entry.entrydsm.common.response.RestResponse;
import com.entry.entrydsm.user.domain.User;
import com.entry.entrydsm.user.domain.tempuser.TempUser;
import com.entry.entrydsm.user.dto.SigninDTO;
import com.entry.entrydsm.user.dto.SignupDTO;
import com.entry.entrydsm.user.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.mail.SendFailedException;
import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    @Qualifier("auth-service")
    private AuthService authService;

    @PostMapping("/signup")
    public RestResponse<TempUser> signup(@Valid @RequestBody SignupDTO dto) throws SendFailedException {
        return RestResponse.success(authService.signup(dto));
    }

    @PostMapping("/signup/confirm/{code}")
    @ResponseStatus(HttpStatus.CREATED)
    public RestResponse<JwtToken> confirm(@PathVariable String code) throws Exception {
        return RestResponse.success(authService.confirm(code));
    }

    @PostMapping("/signin")
    public RestResponse<JwtToken> signin(@Valid @RequestBody SigninDTO dto) throws Exception {
        return RestResponse.success(authService.signin(dto));
    }

    @GetMapping("/auth-required")
    @AuthRequired(allowSubmitted = true)
    public RestResponse<User> authTest(User user) {
        return RestResponse.success(user);
    }
}
