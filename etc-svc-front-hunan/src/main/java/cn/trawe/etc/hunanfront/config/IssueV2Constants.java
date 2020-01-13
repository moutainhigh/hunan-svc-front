package cn.trawe.etc.hunanfront.config;

public class IssueV2Constants {
	
	//发货状态
	
	public static final String FRONT_CARD_DEVICE_STATUS_INIT ="0";
	
	public static final String FRONT_CARD_DEVICE_STATUS_FINISH ="1";
	
	public static final String FRONT_CARD_DEVICE_STATUS_CONFIRM ="2";
	
	//前置现场办理状态
	public static final String FRONT_INSTALL_TYPE_PICK_UP ="1";
	
	//前置邮寄状态
	public static final String FRONT_INSTALL_TYPE_POST ="2";
	
	//中台现场办理状态
	public static final int CENTER_INSTALL_TYPE_PICK_UP =5;
	
	//中台邮寄
	public static final int CENTER_INSTALL_TYPE_POST =6;
	
	public static final String FRONT_ORDER_STATUS_INIT ="0";
	
	public static final String FRONT_ORDER_STATUS_WAIT_AUDIT ="1";
	
	public static final String FRONT_ORDER_STATUS_REJECT= "2";
	
	public static final String FRONT_ORDER_STATUS_VEHICLE_SUBMIT= "3";
	
	public static final String FRONT_ORDER_STATUS_PASS_AUDIT= "4";
	
	public static final String FRONT_ORDER_STATUS_CANCELED= "5";
	
	public static final String FRONT_ORDER_STATUS_AUDIT_ING= "6";
	
	public static final String FRONT_ORDER_STATUS_FINISH= "7";
	
	public static final String SYSTEM_ERROR_INFO= "系统异常,请稍后重试";
	
	
	
	public  static String  orderStatusConvert(int orderStatus) {
		
		
		
		switch(orderStatus) {
		case 0:{
			return FRONT_ORDER_STATUS_INIT;
		}
		case 1:{
			return FRONT_ORDER_STATUS_WAIT_AUDIT;
		}
		case 3:{
			return FRONT_ORDER_STATUS_WAIT_AUDIT;
		}
		case 5:{
			return FRONT_ORDER_STATUS_REJECT;
		}
		case 6:{
			return FRONT_ORDER_STATUS_VEHICLE_SUBMIT;
		}
		case 9:{
			return FRONT_ORDER_STATUS_VEHICLE_SUBMIT;
		}
		case 10:{
			return FRONT_ORDER_STATUS_PASS_AUDIT;
		}
		case 11:{
			return FRONT_ORDER_STATUS_FINISH;
		}
		case 14:{
			return "8";    //拓展订单处理中
		}
		case 15:{
			return "9";   //拓展订单审核成功
		}
		case 16:{
			return "10";   //拓展订单提交成功
		}
		default:{
			throw new RuntimeException("订单类型转换错误");
		}
		
		}
		
		
		
	}

}
