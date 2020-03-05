package com.github.cstettler.dddttc.support.infrastructure.web;

import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

public class ModelAndViewBuilder {

    private final Map<String, Object> modelProperties;
    private final String viewName;

    private ModelAndViewBuilder(String viewName) {
        this.viewName = viewName;
        this.modelProperties = new HashMap<>();
    }

    public ModelAndViewBuilder property(String name, Object value) {
        this.modelProperties.put(name, value);

        return this;
    }

    public ModelAndViewBuilder error(Exception e) {
        this.modelProperties.put("error", e.getMessage());

        return this;
    }

    public ModelAndView build() {
        return new ModelAndView(this.viewName, this.modelProperties);
    }

    public static ModelAndViewBuilder modelAndView(String viewName) {
        return new ModelAndViewBuilder(viewName);
    }

    public static ModelAndView redirectTo(String url) {
        return modelAndView("redirect:" + url).build();
    }

}
