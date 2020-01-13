/*
 * PingController.java
 * 2018年3月27日
 * ©2015-2018 北京特微智能科技有限公司. All rights reserved.
 */
package cn.trawe.etc.hunanfront.controller;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 基本响应URL
 *
 * @author Jiang Guangxing
 */
@RestController
public class PingController {

    @RequestMapping(path = "/ping")
    public String ping() {
        return "pong";
    }
}
