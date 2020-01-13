package cn.trawe.etc.hunanfront.request;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import cn.trawe.etc.hunanfront.utils.ValidUtils;
import cn.trawe.utils.ValidateUtil;
import lombok.Data;
@Data
public class TestApplyOrderValidation {
	
	
	    
	        @NotBlank(message = "车牌号不能为空")
	        private String viNumber;
	        @NotBlank(message = "车牌颜色不能为空")
	        @Min(value = 0, message = "车牌颜色不能小于0")
	        private String viPlateColor;
	        @NotBlank(message = "车辆类型不能为空")
	        private String viType;
	        @NotBlank(message = "⻋辆所有人不能为空")
	        private String viOwnerName;
	        private String viOwnerAddress;
	        @NotBlank(message = "⻋辆使用性质不能为空")
	        private String viUseType;
	        @NotBlank(message = "⻋辆品牌型号不能为空")
	        private String viModelName;
	        @NotBlank(message = "⻋辆识别代号不能为空")
	        private String viVin;
	        @NotBlank(message = "⻋辆发动机号码不能为空")
	        private String engineNo;
	        @NotBlank(message = "行驶证注册日期不能为空")
	        //@Pattern(regexp = "^\\d{4}\\-\\d{1,2}\\-\\d{1,2}$")
	        private String viStartTime;
	        @NotBlank(message = "行驶证发证日期不能为空")
	        //@Pattern(regexp = "^\\d{4}\\-\\d{1,2}\\-\\d{1,2}$")
	        private String viGrantTime;
	        private String viLicenseNo;
	        @NotBlank(message = "核定载人数不能为空")
	        @Min(value = 0, message = "核定载人数不能小于0")
	        private String viAc;
	        private String viTotalMass;
	        private String viReadinessMass;
	        private String viLength;
	        private String viHeight;
	        private String viWidth;
	        private String viTractionMass;
	        private String viInspectionRecord;
	        private String viLicenseImgFront;
	        private String viLicenseImgBack;
	        private String viImgAngle;
	    
	        private String check(TestApplyOrderValidation applyOrderSyncReq) {
	            String err = ValidUtils.validateBean(applyOrderSyncReq);
//	            if (!AuditType.canWarp(applyOrderSyncReq.getSellerId()))
//	                err += ",发行方编号不正确";
	            if (applyOrderSyncReq != null) {
	                String viInfoErr = ValidUtils.validateBean(applyOrderSyncReq);
	                if (ValidateUtil.isNotEmpty(viInfoErr))
	                    err += "," + viInfoErr;
	            }
	            
	            if (err.startsWith(","))
	                err = err.substring(1);
	            return err;
	        }

         public static void main(String[] args) {
			
		}
}
