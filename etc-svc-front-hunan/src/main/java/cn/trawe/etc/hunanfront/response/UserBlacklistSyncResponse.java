package cn.trawe.etc.hunanfront.response;

import java.util.List;

import cn.trawe.etc.hunanfront.entity.BlackUser;
import cn.trawe.etc.hunanfront.expose.v2.BaseResponseData;
import lombok.Data;

/**
 * @author Kevis
 * @date 2019/5/10
 */
@Data
public class UserBlacklistSyncResponse extends BaseResponseData {
    private List<BlackUser> addUsers;
    private List<BlackUser> removeUsers;
}
