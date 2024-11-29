package com.example.controller;

import com.example.helper.MyHelper;
import java.lang.String;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/api/v1")
class Demo {
  @GetMapping("/ping")
  void ping(@RequestParam("id") String id) {
    MyHelper helper = MyHelper.getInstance();
    ResponseData resData = helper.ping();
  }

  MyHelper getHelper() {
    MyHelper helper = MyHelper.getInstance();
    return helper;
  }

  @PostMapping("/ping2")
  void ping2(@RequestBody String id) {
    MyHelper helper = MyHelper.getInstance();
    ResponseData resData = helper.ping2();
  }

  MyHelper getHelper() {
    MyHelper helper = MyHelper.getInstance();
    return helper;
  }
}
