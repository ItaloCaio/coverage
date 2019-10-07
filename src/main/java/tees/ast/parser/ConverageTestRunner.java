package tees.ast.parser;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ConverageTestRunner {

	    public static void main(String args[]) {

	    	 Object arquivoFromReflection = null;
	    	 
	        
	    	 try {
	             arquivoFromReflection = Class.forName("tees.ast.parser.TriangleTest").newInstance();
	         } catch (InstantiationException e) {
	             // TODO Auto-generated catch block
	             e.printStackTrace();
	         } catch (IllegalAccessException e) {
	             // TODO Auto-generated catch block
	             e.printStackTrace();
	         } catch (ClassNotFoundException e) {
	             // TODO Auto-generated catch block
	             e.printStackTrace();
	         }
	         
	         System.out.println("Nome da Classe: "+arquivoFromReflection.getClass().getName());
	         
	         System.out.println("");
	         System.out.println("Métodos: ");
	         
	         for(Method m : arquivoFromReflection.getClass().getMethods()){
	             System.out.print(m.getName()+", ");
	         }
	         
	         System.out.println("");
	         
	    	
	    }
}
