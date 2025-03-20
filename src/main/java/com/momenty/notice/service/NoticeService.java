package com.momenty.notice.service;

import com.momenty.notice.domain.Notice;
import com.momenty.notice.repository.NoticeRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeService {

    private final NoticeRepository noticeRepository;

    public List<Notice> getNotices() {
        return noticeRepository.findAll();
    }
}
