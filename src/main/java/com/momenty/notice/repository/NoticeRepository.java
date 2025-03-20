package com.momenty.notice.repository;

import com.momenty.notice.domain.Notice;
import java.util.List;
import org.springframework.data.repository.Repository;

public interface NoticeRepository extends Repository<Notice, Integer> {

    List<Notice> findAll();
}
