package codcoverage;

import java.lang.reflect.Method;

import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

public class CoverageTestRunner extends Runner{
	
	private Class testClass;
	
    public CoverageTestRunner(Class testClass) {
        super();
        this.testClass = testClass;
    }
 
    @Override
    public Description getDescription() {
        return Description
          .createTestDescription(testClass, "My runner description");
    }
 
    @Override
    public void run(RunNotifier notifier) {
        System.out.println("running the tests from MyRunner: " + testClass);
        try {
            Object testObject = testClass.newInstance();
            for (Method method : testClass.getMethods()) {
                if (method.isAnnotationPresent(Test.class)) {
                	testJSJVMBackendMethod(method, notifier, testObject);
                }
            }
        } catch (Exception e) {
        	throw new RuntimeException(e);
        }
      
    }
    
    private void testJSJVMBackendMethod(Method method, RunNotifier aRunNotifier, Object theInstance) {
    	 aRunNotifier.fireTestStarted(Description
                 .createTestDescription(testClass, method.getName()));
         try {
          
             aRunNotifier.fireTestStarted(Description
                   .createTestDescription(testClass, method.getName()));
             method.invoke(theInstance);
             System.out.println("Teste passou " + method.getName());

             aRunNotifier.fireTestFinished(Description
                     .createTestDescription(testClass, method.getName()));
         } catch (Exception e) {
         	System.out.println("Teste falhou "+ method.getName());
             aRunNotifier.fireTestFailure(new Failure(Description
                     .createTestDescription(testClass, method.getName()), e));
         }
    }

}
