package cn.abyss4393.dto.controller;

import cn.abyss4393.annotation.AuthAccess;
import cn.abyss4393.entity.ResultFul;
import cn.abyss4393.service.IMessageService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author abyss
 * @version 1.0-SNAPSHOT
 * @className ChatController
 * @description TODO
 * @date 2023/11/14
 * @completion false
 */
@RestController
@RequestMapping("/api/private/v1/community/chat")
public class ChatController {

    @Resource
    private IMessageService messageService;
    @AuthAccess(desc = "获取聊天记录")
    @GetMapping("/record/{senderId}/{receiverId}")
    public ResultFul<?> getChatRecord(@PathVariable Integer senderId, @PathVariable Integer receiverId) {
        return messageService.getChatRecord(senderId, receiverId);
    }

}
