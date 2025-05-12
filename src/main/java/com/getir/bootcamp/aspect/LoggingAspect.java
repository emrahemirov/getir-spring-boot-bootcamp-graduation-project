package com.getir.bootcamp.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String RED = "\u001B[31m";
    private static final String BLUE = "\u001B[34m";
    private static final String CYAN = "\u001B[36m";
    private static final String YELLOW = "\u001B[33m";

    private static final int MAX_LINE_WIDTH = 100;

    @Around("execution(* com.getir.bootcamp.service..*(..)) || execution(* com.getir.bootcamp.controller..*(..))")
    public Object logExecutionDetails(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();

        log.info(BLUE + "\n==========[ METHOD ENTRY ]==========\n" + RESET +
                YELLOW + "Method   : " + RESET + methodName + "\n" +
                YELLOW + "Arguments: " + RESET + Arrays.toString(args) + "\n" +
                BLUE + "====================================\n" + RESET);

        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - startTime;

            log.info(GREEN + "\n==========[ METHOD EXIT ]===========\n" + RESET +
                    YELLOW + "Method   : " + RESET + methodName + "\n" +
                    YELLOW + "Duration : " + RESET + duration + " ms\n" +
                    YELLOW + "Result   : \n" + RESET + formatMultiline(String.valueOf(result)) +
                    GREEN + "====================================\n" + RESET);

            return result;
        } catch (Throwable ex) {
            long duration = System.currentTimeMillis() - startTime;

            log.error(RED + "\n==========[ EXCEPTION ]=============\n" + RESET +
                    YELLOW + "Method   : " + RESET + methodName + "\n" +
                    YELLOW + "Duration : " + RESET + duration + " ms\n" +
                    YELLOW + "Error    : " + RESET + ex.getMessage() + "\n" +
                    RED + "====================================\n" + RESET);

            throw ex;
        }
    }

    private String formatMultiline(String text) {
        if (text == null) return "null\n";

        StringBuilder sb = new StringBuilder();
        int length = text.length();
        int offset = 0;

        while (offset < length) {
            int end = Math.min(offset + MAX_LINE_WIDTH, length);
            sb.append(CYAN).append("           ").append(text, offset, end).append("\n").append(RESET);
            offset = end;
        }

        return sb.toString();
    }
}

