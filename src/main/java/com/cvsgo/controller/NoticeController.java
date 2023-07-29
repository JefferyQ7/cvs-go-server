package com.cvsgo.controller;

import com.cvsgo.dto.SuccessResponse;
import com.cvsgo.dto.notice.ReadNoticeDetailResponseDto;
import com.cvsgo.dto.notice.ReadNoticeResponseDto;
import com.cvsgo.service.NoticeService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notices")
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping
    public SuccessResponse<List<ReadNoticeResponseDto>> readNoticeList() {
        return SuccessResponse.from(noticeService.readNoticeList());
    }

    @GetMapping("/{noticeId}")
    public SuccessResponse<ReadNoticeDetailResponseDto> readNotice(@PathVariable Long noticeId) {
        return SuccessResponse.from(noticeService.readNotice(noticeId));
    }

}
