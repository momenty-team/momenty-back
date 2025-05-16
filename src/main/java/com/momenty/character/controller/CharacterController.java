package com.momenty.character.controller;

import com.momenty.character.dto.CharacterStatusRequest;
import com.momenty.character.dto.CharacterStatusResponse;
import com.momenty.character.service.CharacterService;
import com.momenty.global.annotation.UserId;
import com.momenty.record.domain.UserRecord;
import com.momenty.record.dto.RecordDetailUpdateRequest;
import com.momenty.record.dto.RecordsResponse;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/characters")
public class CharacterController {

    private final CharacterService characterService;

    @GetMapping("/status")
    public ResponseEntity<CharacterStatusResponse> getCharacterStatus(
            @RequestBody CharacterStatusRequest characterStatusRequest,
            @Parameter(hidden = true) @UserId Integer userId,
            @RequestParam(required = true) Integer year,
            @RequestParam(required = true) Integer month,
            @RequestParam(required = true) Integer day
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        CharacterStatusResponse.from(
                                characterService.getCharacterStatus(characterStatusRequest, userId, year, month, day)
                        )
                );
    }
}
