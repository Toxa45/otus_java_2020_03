package proxy;

import annotations.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.stream.Collectors;

public class Ioc {

    private Ioc() {
    }

    public static TestLogging create() {
        InvocationHandler handler = new DemoInvocationHandler(new TestLoggingImpl());
        return (TestLogging) Proxy.newProxyInstance(Ioc.class.getClassLoader(),
                new Class<?>[]{TestLogging.class}, handler);
    }

    static class DemoInvocationHandler implements InvocationHandler {
        private final TestLogging testLog;
        private final Set<Method> needTestingMethods;

        DemoInvocationHandler(TestLogging testLog) {
            this.testLog = testLog;
            needTestingMethods = Arrays.stream(testLog.getClass().getMethods())
                    .filter(method -> method.isAnnotationPresent(Log.class))
                    .collect(Collectors.toSet());
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            for(Method curMethod : needTestingMethods){
                if(curMethod.getName().equals(method.getName())&& Arrays.equals(curMethod.getParameterTypes(), method.getParameterTypes())){
                    System.out.println("executed method: " + method.getName() + ", param: " + Arrays.toString(args).replaceAll("[\\[\\]]",""));
                    break;
                }
            }
            return method.invoke(testLog, args);
        }

        @Override
        public String toString() {
            return "DemoInvocationHandler{" +
                    "TestLogging=" + testLog +
                    '}';
        }
    }
}
