package cn.trawe.etc.hunanfront.feign.v2;

import cn.trawe.pay.api.IssueEtcCardApi;

//@FeignClient(name = "etc-core-publish",fallback = IssueCardCenterApiFallback.class)
public interface IssueCardCenterApi  extends IssueEtcCardApi{

}
