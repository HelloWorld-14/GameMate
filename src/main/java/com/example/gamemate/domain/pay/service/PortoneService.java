package com.example.gamemate.domain.pay.service;

import com.example.gamemate.global.constant.ErrorCode;
import com.example.gamemate.global.exception.ApiException;
import com.example.gamemate.domain.pay.dto.PortoneTokenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PortoneService {
    private  final WebClient webClient;

    @Value("${portone.api.secret.v2}")

    private String secret;

    public String portoneToken() {
        Map<String, String> requestBody = new HashMap<>();

        requestBody.put("apiSecret", secret);

        Mono<PortoneTokenDto> postToken = webClient.post()
                .uri("/login/api-secret")
                .body(Mono.just(requestBody), Map.class)
                .retrieve()
                .bodyToMono(PortoneTokenDto.class);

        PortoneTokenDto portoneToken = postToken.block();

        if (Objects.requireNonNull(portoneToken).getAccessToken() != null) {
            return portoneToken.getAccessToken();
        }

      //  throw new UnAuthorizedException(ErrorCode.FAILED_GET_TOKEN);
        throw new ApiException(ErrorCode.GAME_NOT_FOUND);// 수정 필요
    }
}

