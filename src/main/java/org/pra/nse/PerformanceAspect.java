//package org.pra.nse;
//
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//import org.springframework.stereotype.Component;
//
//import java.util.concurrent.TimeUnit;
//
//@Aspect
//@Component
//public class PerformanceAspect {
//    @Pointcut("within(@org.springframework.stereotype.Repository *)")
//    public void repositoryClassMethods() {};
//
//    @Around("repositoryClassMethods()")
//    public Object measureMethodExecutionTime(ProceedingJoinPoint joinPoint)
//            throws Throwable {
//        long start = System.nanoTime();
//        Object returnValue = joinPoint.proceed();
//        long end = System.nanoTime();
//        String methodName = joinPoint.getSignature().getName();
//        System.out.println(
//                "Execution of " + methodName + " took " +
//                        TimeUnit.NANOSECONDS.toMillis(end - start) + " ms");
//        return returnValue;
//    }
//}
