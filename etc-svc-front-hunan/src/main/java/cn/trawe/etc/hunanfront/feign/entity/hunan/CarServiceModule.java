package cn.trawe.etc.hunanfront.feign.entity.hunan;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CarServiceModule {

    //轴数
    private String zs;

    //车外廓长(单位毫米)
    private String cwkc;

    //车外廓宽(单位毫米)
    private String cwkk;

    //车外廓高(单位毫米)
    private String cwkg;

    //核定载客
    private String hdzk;

    //车辆类型(未定)
    private String cllx;

    //总质量(单位KG)
    private String zzl;

    //核定载质量(单位KG)
    private String hdzzl;

    //整备质量(单位KG)
    private String zbzl;

    //使用性质(未定)
    private String syxz;

    //身份证明号码
    private String sfzmhm;

    //身份证明名称(未定)
    private String sfzmmc;

    //车辆识别代号
    private String clsbdh;

    //车辆型号(未定)
    private String clxh;

    //车身颜色(未定)
    private String csys;

    //中文品牌
    private String clpp1;

    //英文品牌
    private String clpp2;

    //机动车所有人
    private String syr;

    //号牌号码
    private String hphm;

    //初次登记日期
    private String ccdjrq;

    //轮胎数
    private String lts;

    //轴距(单位毫米)
    private String zj;

    //检验有效期止
    private String yxqz;

    //发动机号
    private String fdjh;

    //号牌种类
    private String hpzl;

    //准牵引总质量(单位KG)
    private String zqyzl;

    //车辆类型中文名称
    private String mycllx;

    //1-客车 2-货车
    private String mykhlb;

    //0-非营运 1-营运
    private String mysyxz;


    
}
