package cn.kmbeast.security;

import cn.kmbeast.mapper.OwnershipMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.server.ResponseStatusException;
import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class OwnershipGuard {
    public enum ResourceType { HEALTH, BOOKMARK, MESSAGE, MODEL, COMMENT }
    @Resource private OwnershipMapper ownershipMapper;

    public void lockCommentForVoting(Integer id) {
        AccessPolicy.userId();
        if (id == null || id <= 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Valid comment ID required");
        if (!TransactionSynchronizationManager.isActualTransactionActive()) throw new IllegalStateException("Transaction required");
        ownershipMapper.lockOwners(ResourceType.COMMENT.name(), java.util.Collections.singletonList(id.longValue()));
    }

    /** Lock rows in the same transaction as the write; reject mixed-owner batches in full. */
    public void requireOwned(ResourceType resource, List<? extends Number> requestedIds) {
        Integer caller = AccessPolicy.userId();
        if (requestedIds == null || requestedIds.isEmpty() || requestedIds.size() > 1000
                || requestedIds.stream().anyMatch(id -> id == null || id.longValue() <= 0)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Supply between 1 and 1000 valid IDs");
        }
        if (AccessPolicy.isAdmin()) return;
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            throw new IllegalStateException("Ownership checks require a write transaction");
        }
        List<Long> ids = requestedIds.stream().map(Number::longValue).distinct().sorted().collect(Collectors.toList());
        List<Integer> owners = ownershipMapper.lockOwners(resource.name(), ids);
        if (owners.size() != ids.size() || owners.stream().anyMatch(owner -> !Objects.equals(owner, caller))) {
            AccessPolicy.forbidden();
        }
    }
}
