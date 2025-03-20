package com.momenty.notice.controller;

import com.momenty.global.annotation.UserId;
import com.momenty.notice.domain.Notice;
import com.momenty.notice.dto.NoticesResponse;
import com.momenty.notice.service.NoticeService;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping
    public ResponseEntity<NoticesResponse> getNotices(
            @Parameter(hidden = true) @UserId Integer userId
    ) {
        List<Notice> notices = noticeService.getNotices();
        return ResponseEntity.status(HttpStatus.OK)
                .body(NoticesResponse.of(notices));
    }
}
