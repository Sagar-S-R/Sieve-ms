package com.sieve.reminder.strategy;

import com.sieve.reminder.annotation.ReminderStrategyType;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ReminderStrategyRegistry implements BeanPostProcessor {

    private final Map<String, ReminderStrategy> strategies = new ConcurrentHashMap<>();

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        ReminderStrategyType annotation = bean.getClass().getAnnotation(ReminderStrategyType.class);
        if (annotation != null && bean instanceof ReminderStrategy strategy) {
            strategies.put(annotation.value(), strategy);
        }
        return bean;
    }

    public ReminderStrategy get(String type) {
        ReminderStrategy strategy = strategies.get(type);
        if (strategy == null) {
            throw new IllegalArgumentException("No reminder strategy registered for: " + type);
        }
        return strategy;
    }
}
