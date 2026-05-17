package cn.kmbeast.service.impl;

import cn.kmbeast.context.LocalThreadHolder;
import cn.kmbeast.mapper.EvaluationsMapper;
import cn.kmbeast.mapper.MessageMapper;
import cn.kmbeast.mapper.UserMapper;
import cn.kmbeast.pojo.api.ApiResult;
import cn.kmbeast.pojo.api.PageResult;
import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.dto.query.extend.EvaluationsQueryDto;
import cn.kmbeast.pojo.dto.query.extend.MessageQueryDto;
import cn.kmbeast.pojo.em.IsReadEnum;
import cn.kmbeast.pojo.em.MessageType;
import cn.kmbeast.pojo.entity.Evaluations;
import cn.kmbeast.pojo.entity.Message;
import cn.kmbeast.pojo.entity.User;
import cn.kmbeast.pojo.vo.CommentChildVO;
import cn.kmbeast.pojo.vo.CommentParentVO;
import cn.kmbeast.pojo.vo.EvaluationsVO;
import cn.kmbeast.service.EvaluationsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Implementation of the Comment Service
 */
@Service
public class EvaluationsServiceImpl implements EvaluationsService {

    @Resource
    private EvaluationsMapper evaluationsMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private MessageMapper messageMapper;

    /**
     * Save a comment
     *
     * @param evaluations Comment data
     */
    private void saveEvaluations(Evaluations evaluations) {
        evaluations.setCommenterId(LocalThreadHolder.getUserId());
        evaluations.setCreateTime(LocalDateTime.now());
        evaluationsMapper.save(evaluations);
    }

    /**
     * Retrieve comment data
     *
     * @param parentId Parent comment ID
     * @return List<CommentChildVO>
     */
    private List<CommentChildVO> getCommentChild(Integer parentId) {
        if (parentId == null) {
            return new ArrayList<>();
        }
        EvaluationsQueryDto evaluationsQueryDto = new EvaluationsQueryDto();
        evaluationsQueryDto.setId(parentId);
        return evaluationsMapper.query(evaluationsQueryDto);
    }

    /**
     * Post a comment
     *
     * @return Result<String>
     */
    @Override
    public Result<Object> insert(Evaluations evaluations) {
        User userQueryEntity = User.builder()
                .id(LocalThreadHolder.getUserId())
                .build();
        User user = userMapper.getByActive(userQueryEntity);
        if (user.getIsWord()) {
            return ApiResult.error("The account has been muted.");
        }
        saveEvaluations(evaluations);
        Integer currentUserId = LocalThreadHolder.getUserId();
        if (evaluations.getParentId() != null) {
            deliverReplyMessage(evaluations, currentUserId);
        } else {
            deliverTopLevelCommentMessages(evaluations, currentUserId);
        }
        return ApiResult.success("Comment posted successfully");
    }

    /**
     * Construct a message object
     *
     * @param contentId Content ID
     * @return Message
     */
    private Message createMessage(Integer contentId) {
        Message message = new Message();
        message.setSenderId(LocalThreadHolder.getUserId());
        message.setIsRead(IsReadEnum.READ_NO.getStatus());
        message.setMessageType(MessageType.EVALUATIONS_BY_REPLY.getType());
        message.setCreateTime(LocalDateTime.now());
        message.setContentId(contentId);
        return message;
    }

    /**
     * Notify the parent-comment author (or @user) when someone replies.
     */
    private void deliverReplyMessage(Evaluations evaluations, Integer currentUserId) {
        List<CommentChildVO> commentChildVOS = getCommentChild(evaluations.getParentId());
        if (commentChildVOS.isEmpty()) {
            return;
        }
        CommentChildVO commentChildVO = commentChildVOS.get(0);
        Integer receiverId = evaluations.getReplierId() == null
                ? commentChildVO.getUserId()
                : evaluations.getReplierId();
        if (receiverId == null || Objects.equals(receiverId, currentUserId)) {
            return;
        }
        Message message = createMessage(evaluations.getContentId());
        message.setReceiverId(receiverId);
        message.setContent(evaluations.getParentId() + ";" + evaluations.getContentId() + ";" + evaluations.getContent());
        messageMapper.batchSave(Collections.singletonList(message));
    }

    /**
     * Notify everyone else who commented on the same article when a new top-level comment is posted.
     */
    private void deliverTopLevelCommentMessages(Evaluations evaluations, Integer currentUserId) {
        if (evaluations.getContentId() == null || evaluations.getContentType() == null) {
            return;
        }
        List<Integer> receiverIds = evaluationsMapper.listCommenterIdsByContent(
                evaluations.getContentId(),
                evaluations.getContentType(),
                currentUserId);
        if (receiverIds == null || receiverIds.isEmpty()) {
            return;
        }
        String payload = evaluations.getId() + ";" + evaluations.getContentId() + ";" + evaluations.getContent();
        List<Message> messageList = new ArrayList<>();
        for (Integer receiverId : receiverIds) {
            if (receiverId == null || Objects.equals(receiverId, currentUserId)) {
                continue;
            }
            Message message = createMessage(evaluations.getContentId());
            message.setReceiverId(receiverId);
            message.setContent(payload);
            messageList.add(message);
        }
        if (!messageList.isEmpty()) {
            messageMapper.batchSave(messageList);
        }
    }

    /**
     * Retrieve all comments
     *
     * @return Result<String>
     */
    @Override
    public Result<Object> list(Integer contentId, String contentType) {
        List<CommentParentVO> parentComments = evaluationsMapper.getParentComments(contentId, contentType);
        setUpvoteFlag(parentComments);
        Integer count = evaluationsMapper.totalCount(contentId, contentType);
        return ApiResult.success(new EvaluationsVO(count, parentComments));
    }

    /**
     * Set upvote status
     *
     * @param parentComments List of parent comments
     */
    private void setUpvoteFlag(List<CommentParentVO> parentComments) {
        String userId = LocalThreadHolder.getUserId().toString();
        parentComments.forEach(parentComment -> {
            parentComment.setUpvoteFlag(isUserUpvote(parentComment.getUpvoteList(), userId));
            parentComment.setUpvoteCount(countVotes(parentComment.getUpvoteList()));
            Optional.ofNullable(parentComment.getCommentChildVOS())
                    .orElse(Collections.emptyList())
                    .forEach(child -> {
                        child.setUpvoteFlag(isUserUpvote(child.getUpvoteList(), userId));
                        child.setUpvoteCount(countVotes(child.getUpvoteList()));
                    });
        });
    }

    /**
     * Determine if a user has upVoted
     *
     * @param voteStr Comma-separated upvote list
     * @param userId  User ID
     * @return Boolean indicating if the user has upVoted
     */
    private boolean isUserUpvote(String voteStr, String userId) {
        return Optional.ofNullable(voteStr)
                .map(s -> Arrays.asList(s.split(",")))
                .orElse(Collections.emptyList())
                .contains(userId);
    }

    /**
     * Count the number of upvotes
     *
     * @param voteStr Comma-separated upvote list
     * @return The number of upvotes
     */
    private int countVotes(String voteStr) {
        return Optional.ofNullable(voteStr)
                .map(s -> s.split(",").length)
                .orElse(0);
    }

    /**
     * Paginated query for comments
     *
     * @return Result<String>
     */
    @Override
    public Result<Object> query(EvaluationsQueryDto evaluationsQueryDto) {
        List<CommentChildVO> list = evaluationsMapper.query(evaluationsQueryDto);
        Integer totalPage = evaluationsMapper.queryCount(evaluationsQueryDto);
        return PageResult.success(list, totalPage);
    }

    /**
     * Bulk delete comment data
     *
     * @return Result<String>
     */
    @Override
    public Result<Object> batchDelete(List<Integer> ids) {
        evaluationsMapper.batchDelete(ids);
        return ApiResult.success();
    }

    /**
     * Delete a comment
     *
     * @return Result<String>
     */
    @Override
    public Result<String> delete(Integer id) {
        ArrayList<Integer> ids = new ArrayList<>();
        ids.add(id);
        evaluationsMapper.batchDelete(ids);
        return ApiResult.success();
    }

    /**
     * Modify a comment
     *
     * @return Result<Map < String, Object>>
     */
    @Override
    public Result<Map<String, Object>> update(Evaluations evaluations) {
        EvaluationsQueryDto evaluationsQueryDto = new EvaluationsQueryDto();
        evaluationsQueryDto.setId(evaluations.getId());
        List<CommentChildVO> commentChildVOS = evaluationsMapper.query(evaluationsQueryDto);
        if (commentChildVOS.isEmpty()) {
            return ApiResult.success();
        }
        CommentChildVO commentChildVO = commentChildVOS.get(0);
        String upvoteList = commentChildVO.getUpvoteList();
        String userId = String.valueOf(LocalThreadHolder.getUserId());
        upvoteList = (upvoteList == null || upvoteList.isEmpty()) ? userId : toggleUpvote(upvoteList, userId);
        Evaluations evaluationsUpdate = new Evaluations();
        evaluationsUpdate.setId(evaluations.getId());
        if (upvoteList.contains(userId)){
            evaluationsUpdate.setUpvoteList(upvoteList);
        }
        evaluationsMapper.update(evaluations);
        upvoteMessageDeliver(commentChildVO);
        Map<String, Object> result = new HashMap<>();
        result.put("num", upvoteList.split(",").length);
        result.put("flag", upvoteList.contains(userId));
        return ApiResult.success(result);
    }

    /**
     * Handle upvote notifications
     *
     * @param commentChildVO Comment data
     */
    private void upvoteMessageDeliver(CommentChildVO commentChildVO) {
        String userId = String.valueOf(LocalThreadHolder.getUserId());
        if (userId.equals(String.valueOf(commentChildVO.getUserId()))) {
            return;
        }
        MessageQueryDto messageQueryDto = new MessageQueryDto();
        messageQueryDto.setMessageType(MessageType.EVALUATIONS_BY_UPVOTE.getType());
        messageQueryDto.setContentId(commentChildVO.getContentId());
        messageQueryDto.setUserId(commentChildVO.getUserId());
        messageQueryDto.setSenderId(LocalThreadHolder.getUserId());
        Integer queryCount = messageMapper.queryCount(messageQueryDto);
        if (queryCount != 0) {
            return;
        }
        Message message = new Message();
        message.setMessageType(MessageType.EVALUATIONS_BY_UPVOTE.getType());
        message.setIsRead(IsReadEnum.READ_NO.getStatus());
        message.setReceiverId(commentChildVO.getUserId());
        message.setCreateTime(LocalDateTime.now());
        message.setContentId(commentChildVO.getId());
        message.setSenderId(LocalThreadHolder.getUserId());
        message.setContent("Liked your comment【" + commentChildVO.getContent() + "】");
        messageMapper.batchSave(Collections.singletonList(message));
    }

    /**
     * Like string manipulation
     */
    public String toggleUpvote(String upvoteList, String userId) {
        Set<String> upvoteSet = new HashSet<>();
        if (!upvoteList.isEmpty()) {
            upvoteSet.addAll(Arrays.asList(upvoteList.split(",")));
        }
        if (upvoteSet.contains(userId)) {
            upvoteSet.remove(userId);
        } else {
            upvoteSet.add(userId);
        }
        return String.join(",", upvoteSet);
    }

}
