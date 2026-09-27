package cn.kmbeast.service.impl;

import cn.kmbeast.context.LocalThreadHolder;
import cn.kmbeast.mapper.MessageMapper;
import cn.kmbeast.mapper.UserMapper;
import cn.kmbeast.pojo.api.ApiResult;
import cn.kmbeast.pojo.api.PageResult;
import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.dto.query.extend.MessageQueryDto;
import cn.kmbeast.pojo.dto.query.extend.UserQueryDto;
import cn.kmbeast.pojo.em.IsReadEnum;
import cn.kmbeast.pojo.em.MessageType;
import cn.kmbeast.pojo.em.RoleEnum;
import cn.kmbeast.pojo.entity.Message;
import cn.kmbeast.pojo.entity.User;
import cn.kmbeast.pojo.vo.MessageVO;
import cn.kmbeast.service.MessageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.kmbeast.security.AccessPolicy;
import cn.kmbeast.security.OwnershipGuard;
import cn.kmbeast.security.OwnershipGuard.ResourceType;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Message business logic implementation
 */
@Service
public class MessageServiceImpl implements MessageService {

    @Resource
    private OwnershipGuard ownershipGuard;

    @Resource
    private MessageMapper messageMapper;
    @Resource
    private UserMapper userMapper;

    /**
     * Message addition
     */
    @Override
    public Result<Void> save(List<Message> messages) {
        messageMapper.batchSave(messages);
        return ApiResult.success();
    }

    /**
     * The comment has been replied to by someone else,
     * and a notification message needs to be sent to the person who responded
     */
    @Override
    public Result<Void> evaluationsReplySave(Message message) {
        message.setMessageType(MessageType.EVALUATIONS_BY_REPLY.getType());
        message.setContent("Your comment was replied");
        message.setIsRead(IsReadEnum.READ_NO.getStatus());
        message.setCreateTime(LocalDateTime.now());
        List<Message> entityListSave = new ArrayList<>();
        entityListSave.add(message);
        return save(entityListSave);
    }

    /**
     * When a comment is liked, you need to send a notification to the person who made the comment
     */
    @Override
    public Result<Void> evaluationsUpvoteSave(Message message) {
        message.setMessageType(MessageType.EVALUATIONS_BY_UPVOTE.getType());
        message.setContent("Your comment was liked");
        message.setIsRead(IsReadEnum.READ_NO.getStatus());
        message.setCreateTime(LocalDateTime.now());
        List<Message> entityListSave = new ArrayList<>();
        entityListSave.add(message);
        return save(entityListSave);
    }

    /**
     * system notification
     */
    @Override
    public Result<Void> systemInfoSave(List<Message> messages) {
        return save(messages);
    }

    /**
     * Indicator reminder
     */
    @Override
    public Result<Void> dataWordSave(List<Message> messages) {
        return save(messages);
    }

    /**
     * Message deletion
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> batchDelete(List<Long> ids) {
        ownershipGuard.requireOwned(ResourceType.MESSAGE, ids);
        messageMapper.batchDelete(ids);
        return ApiResult.success();
    }

    /**
     * Message query
     */
    @Override
    public Result<List<MessageVO>> query(MessageQueryDto messageQueryDto) {
        if (!AccessPolicy.isAdmin()) messageQueryDto.setUserId(AccessPolicy.userId());
        List<MessageVO> tagsList = messageMapper.query(messageQueryDto);
        Integer totalCount = messageMapper.queryCount(messageQueryDto);
        return PageResult.success(tagsList, totalCount);
    }

    /**
     * System notifications for the whole station
     */
    @Override
    public Result<Void> systemInfoUsersSave(Message message) {
        String messageContent = message.getContent();
        UserQueryDto userQueryDto = new UserQueryDto();
        List<User> userList = userMapper.query(userQueryDto);
        List<Message> messageList = new ArrayList<>();
        userList.forEach(user -> {
            if (Objects.equals(RoleEnum.USER.getRole(), user.getUserRole())) {
                Message saveEntity = new MessageVO();
                saveEntity.setReceiverId(user.getId());
                saveEntity.setContent(messageContent);
                saveEntity.setMessageType(MessageType.SYSTEM_INFO.getType());
                saveEntity.setIsRead(IsReadEnum.READ_NO.getStatus());
                saveEntity.setCreateTime(LocalDateTime.now());
                messageList.add(saveEntity);
            }
        });
        return save(messageList);
    }

    /**
     * Set all messages to read
     */
    @Override
    public Result<Void> clearMessage() {
        Integer userId = LocalThreadHolder.getUserId();
        messageMapper.update(userId, IsReadEnum.READ_OK.getStatus());
        return ApiResult.success();
    }
}
