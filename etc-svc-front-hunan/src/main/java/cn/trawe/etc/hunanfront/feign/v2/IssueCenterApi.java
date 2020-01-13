package cn.trawe.etc.hunanfront.feign.v2;

import org.springframework.cloud.openfeign.FeignClient;


@FeignClient(name = "etc-core-publish" ,fallback = IssueApiV2Fallback.class )
public interface IssueCenterApi extends IssueApiV2 {
	
	
	   

}
