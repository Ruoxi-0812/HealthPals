package cn.kmbeast.service.impl;

import cn.kmbeast.context.LocalThreadHolder;
import cn.kmbeast.mapper.HealthModelConfigMapper;
import cn.kmbeast.mapper.UserHealthMapper;
import cn.kmbeast.pojo.api.ApiResult;
import cn.kmbeast.pojo.api.PageResult;
import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.dto.query.base.QueryDto;
import cn.kmbeast.pojo.dto.query.extend.HealthModelConfigQueryDto;
import cn.kmbeast.pojo.dto.query.extend.UserHealthQueryDto;
import cn.kmbeast.pojo.em.IsReadEnum;
import cn.kmbeast.pojo.em.MessageType;
import cn.kmbeast.pojo.entity.HealthModelConfig;
import cn.kmbeast.pojo.entity.Message;
import cn.kmbeast.pojo.entity.UserHealth;
import cn.kmbeast.pojo.vo.ChartVO;
import cn.kmbeast.pojo.vo.HealthModelConfigVO;
import cn.kmbeast.pojo.vo.UserHealthVO;
import cn.kmbeast.service.MessageService;
import cn.kmbeast.service.UserHealthService;
import cn.kmbeast.utils.DateUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User health service implementation
 */
@Service
public class UserHealthServiceImpl implements UserHealthService {

    @Resource
    private UserHealthMapper userHealthMapper;
    @Resource
    private HealthModelConfigMapper healthModelConfigMapper;
    @Resource
    private MessageService messageService;

    /**
     * Added the user health record
     */
    @Override
    public Result<Void> save(List<UserHealth> userHealths) {
        dealMessage(userHealths);
        dealRole(userHealths);
        userHealthMapper.batchSave(userHealths);
        return ApiResult.success();
    }

    public void dealRole(List<UserHealth> userHealths) {
        LocalDateTime nowTime = LocalDateTime.now();
        Integer userId = LocalThreadHolder.getUserId();
        userHealths.forEach(userHealth -> {
            userHealth.setUserId(userId);
            userHealth.setCreateTime(nowTime);
        });
    }

    /**
     * If there are abnormal indicators, this method forwards the notification
     */
    private void dealMessage(List<UserHealth> userHealths) {
        List<Message> messageList = new ArrayList<>();
        userHealths.forEach(userHealth -> {
            userHealth.setCreateTime(LocalDateTime.now());
            Integer healthModelConfigId = userHealth.getHealthModelConfigId();
            HealthModelConfigQueryDto queryDto = new HealthModelConfigQueryDto();
            queryDto.setId(healthModelConfigId);
            List<HealthModelConfigVO> healthModelConfigs = healthModelConfigMapper.query(queryDto);
            if (!CollectionUtils.isEmpty(healthModelConfigs)) {
                HealthModelConfig healthModelConfig = healthModelConfigs.get(0);
                String valueRange = healthModelConfig.getValueRange();
                String[] values = valueRange.split(",");
                int mixValue = Integer.parseInt(values[0]);
                int maxValue = Integer.parseInt(values[1]);
                double value = Double.parseDouble(String.valueOf(userHealth.getValue()));
                if (value < mixValue || value > maxValue) {
                    Message message = sendMessage(healthModelConfig, userHealth);
                    messageList.add(message);
                }
            }
        });
        if (!CollectionUtils.isEmpty(messageList)) {
            messageService.dataWordSave(messageList);
        }
    }

    /**
     * Process user health records that match message notifications
     */
    private Message sendMessage(HealthModelConfig healthModelConfig, UserHealth userHealth) {
        Message message = new Message();
        message.setMessageType(MessageType.DATA_MESSAGE.getType());
        message.setCreateTime(LocalDateTime.now());
        message.setIsRead(IsReadEnum.READ_NO.getStatus());
        message.setReceiverId(LocalThreadHolder.getUserId());
        message.setContent(buildOutOfRangeMessage(healthModelConfig, userHealth));
        return message;
    }

    private String buildOutOfRangeMessage(HealthModelConfig config, UserHealth record) {
        String name = config.getName() != null ? config.getName() : "metric";
        String range = formatValueRange(config.getValueRange(), config.getUnit());
        String recorded = record.getValue() != null ? record.getValue().trim() : "—";
        String unitSuffix = unitSuffix(config.getUnit());
        return String.format(
                "Your %s reading (%s%s) is outside the expected range (%s). "
                        + "Please rest and keep tracking how you feel. "
                        + "Contact a healthcare professional if you have concerns.",
                name,
                recorded,
                unitSuffix,
                range);
    }

    private String formatValueRange(String valueRange, String unit) {
        if (valueRange == null || valueRange.trim().isEmpty()) {
            return "not set";
        }
        String[] parts = valueRange.split(",");
        if (parts.length >= 2) {
            String min = parts[0].trim();
            String max = parts[1].trim();
            String unitLabel = unitLabel(unit);
            return min + "–" + max + unitLabel;
        }
        return valueRange.trim() + unitLabel(unit);
    }

    private String unitLabel(String unit) {
        if (unit == null || unit.trim().isEmpty() || "none".equalsIgnoreCase(unit.trim())) {
            return "";
        }
        return " " + unit.trim();
    }

    private String unitSuffix(String unit) {
        String label = unitLabel(unit);
        return label.isEmpty() ? "" : label;
    }

    /**
     * The user health record is deleted.
     */
    @Override
    public Result<Void> batchDelete(List<Long> ids) {
        userHealthMapper.batchDelete(ids);
        return ApiResult.success();
    }

    /**
     * Modifying a user health record
     */
    @Override
    public Result<Void> update(UserHealth userHealth) {
        userHealthMapper.update(userHealth);
        return ApiResult.success();
    }

    /**
     * Querying user health records
     */
    @Override
    public Result<List<UserHealthVO>> query(UserHealthQueryDto userHealthQueryDto) {
        List<UserHealthVO> userHealthVOS = userHealthMapper.query(userHealthQueryDto);
        Integer totalCount = userHealthMapper.queryCount(userHealthQueryDto);
        return PageResult.success(userHealthVOS, totalCount);
    }

    /**
     * Statistical model stock data
     */
    @Override
    public Result<List<ChartVO>> daysQuery(Integer day) {
        QueryDto queryDto = DateUtil.startAndEndTime(day);
        UserHealthQueryDto userHealthQueryDto = new UserHealthQueryDto();
        userHealthQueryDto.setStartTime(queryDto.getStartTime());
        userHealthQueryDto.setEndTime(queryDto.getEndTime());
        List<UserHealthVO> userHealthVOS = userHealthMapper.query(userHealthQueryDto);
        List<LocalDateTime> localDateTimes = userHealthVOS.stream().map(UserHealthVO::getCreateTime).collect(Collectors.toList());
        List<ChartVO> chartVOS = DateUtil.countDatesWithinRange(day, localDateTimes);
        return ApiResult.success(chartVOS);
    }


}
