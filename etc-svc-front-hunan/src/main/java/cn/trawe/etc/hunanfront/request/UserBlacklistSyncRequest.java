package cn.trawe.etc.hunanfront.request;

import lombok.Data;

import java.util.List;

import cn.trawe.etc.hunanfront.entity.BlackUser;

/**
 * @author Kevis
 * @date 2019/5/10
 */
@Data
public class UserBlacklistSyncRequest {
    private List<BlackUser> addUsers;
    private List<BlackUser> removeUsers;
}
