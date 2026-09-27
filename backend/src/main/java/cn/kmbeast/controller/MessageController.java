package cn.kmbeast.controller;

import cn.kmbeast.aop.Protector;
import cn.kmbeast.pojo.em.RoleEnum;
import cn.kmbeast.aop.Pager;
import cn.kmbeast.pojo.api.ApiResult;
import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.dto.query.extend.MessageQueryDto;
import cn.kmbeast.pojo.em.IsReadEnum;
import cn.kmbeast.pojo.em.MessageType;
import cn.kmbeast.pojo.entity.Message;
import cn.kmbeast.pojo.vo.MessageTypeVO;
import cn.kmbeast.pojo.vo.MessageVO;
import cn.kmbeast.service.MessageService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Message Controller
 */
@RestController
@RequestMapping(value = "/message")
public class MessageController {

    @Resource
    private MessageService messageService;

    /**
     * Retrieve all message types
     */
    @GetMapping(value = "/types")
    public Result<List<MessageTypeVO>> all() {
        MessageType[] messageTypes = MessageType.values();
        List<MessageTypeVO> messageTypeVOS = new ArrayList<>();
        for (MessageType messageType : messageTypes) {
            MessageTypeVO messageTypeVO = new MessageTypeVO(messageType.getType(), messageType.getDetail());
            messageTypeVOS.add(messageTypeVO);
        }
        return ApiResult.success(messageTypeVOS);
    }

    /**
     * Broadcast system notifications to all users
     */
    @Protector(roles = RoleEnum.ADMIN)
    @PostMapping(value = "/systemInfoUsersSave")
    public Result<Void> systemInfoUsersSave(@RequestBody Message message) {
        return messageService.systemInfoUsersSave(message);
    }

    /**
     * Send message notifications
     */
    @Protector(roles = RoleEnum.ADMIN)
    @PostMapping(value = "/systemInfoSave")
    public Result<Void> systemInfoSave(@RequestBody List<Message> messages) {
        messages.forEach(message -> {
            message.setMessageType(MessageType.SYSTEM_INFO.getType());
            message.setIsRead(IsReadEnum.READ_NO.getStatus());
            message.setCreateTime(LocalDateTime.now());
        });
        return messageService.systemInfoSave(messages);
    }


    /**
     * Delete messages in batch
     */
    @PostMapping(value = "/batchDelete")
    public Result<Void> batchDelete(@RequestBody List<Long> ids) {
        return messageService.batchDelete(ids);
    }


    /**
     * Mark all messages as read
     */
    @PutMapping(value = "/clearMessage")
    public Result<Void> clearMessage() {
        return messageService.clearMessage();
    }

    /**
     * Query messages
     */
    @Pager
    @PostMapping(value = "/query")
    public Result<List<MessageVO>> query(@RequestBody MessageQueryDto messageQueryDto) {
        return messageService.query(messageQueryDto);
    }

}
