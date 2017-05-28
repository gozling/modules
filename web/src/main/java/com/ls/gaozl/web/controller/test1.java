package com.ls.gaozl.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ls.gaozl.web.testUtils;


/**
 * Created by fr on 2017/4/01.
 */
@Controller
public class test1 {

    @RequestMapping(value = "/test1", method = RequestMethod.GET)
    @ResponseBody
    public String test11() {
        return "spring boot multiple modules test";
    }

    @RequestMapping(value = "/test2/{nu1}/{nu2}", method = RequestMethod.GET)
    @ResponseBody
    public String test2(@PathVariable int nu1, @PathVariable int nu2) {
        return  nu1 + "+" + nu2 + "=" + testUtils.add(nu1,nu2);
    }
}
