/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codcoverage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.apache.commons.io.FileSystemUtils;
import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

/**
 * Deve receber o source e reescrever em target
 * 
 * OBS: O método deve buscar na suite de testes as linhas específicas a serem
 * buscadas, até o momento tenta inserir um log em cada método, mas por conta do
 * erro na biblioteca não foi prosseguido para buscar os pontos especificos;
 * 
 * @author jason
 * 
 **/
public class CoverageTransformer {
	String pathSource = "src/runner/";
	File pathTarget = new File("target/");
	File pathTargetold = pathTarget;
	File pathCopy;
	Collection<File> files;
//	String testSuite = "src/codcoverage/source/src/test/TriangleTest.java";
	static Logger log = null;
	static FileHandler filetxt = null;
	int currentLine = 1;
	List<Integer> linhas = new ArrayList<Integer>();
	private static CoverageTransformer instance;

	private CoverageTransformer() {

	}

	public static synchronized CoverageTransformer getInstance() {
		if (instance == null)
			instance = new CoverageTransformer();

		return instance;
	}

	/**
	 * realiza a leitura de um arquivo
	 *
	 * @return String do arquivo
	 */
	public String insertFile(String path) throws NullPointerException {
		String source = "";
		try {
			source = FileUtils.readFileToString(new File(path), "UTF-8");
		} catch (Exception e) {
			source = null;
		}

		return source;
	}

	public void copyFile(String path, String name) {
		pathTarget = pathTargetold;
		pathTarget = new File(pathTarget.getPath() + "/" + name);
		pathCopy = new File(path);
		try {
			FileUtils.copyFile(pathCopy, pathTarget);
			System.out.println("Arquivo copiado :" + pathTarget);
		} catch (IOException e) {

		}

	}

	public void saveFile(File path, String data, ASTNode methodInvocation) {
		try {
			String[] list = data.split("\n");
			List<String> lista = new ArrayList<String>();
			for (int i = 0; i < list.length; i++) {
				lista.add(i, list[i]);
			}
			int position = currentLine + linhas.indexOf(currentLine);
			if (currentLine > 0) {
				if (position < lista.size() && position != 1) {
					lista.add(position, methodInvocation.toString() + ";");
				}
//                if (position ==1) {
//                    lista.add(position, importLog.toString()+";");
//                }
			} else {
				lista.add(currentLine, methodInvocation.toString());
			}

			FileUtils.writeLines(path, lista);

			System.out.println("arquivo salvo em " + path.getPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void saveFile(File path, String data) {
		try {
			FileUtils.write(path, data);
			System.out.println("arquivo salvo em " + path.getPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean analise() {
		if (files == null) {
			String source = "src/runner/";
			String[] extensions = { "java" };
			File directory = new File(source);

			files = FileUtils.listFiles(directory, extensions, false);
			// pathSource = pathTarget.toString();
			for (File file : files) {

				copyFile(file.toString(), file.getName());

				System.out.println("Obtido: " + pathCopy.getName());
				pathCopy = pathTarget;
				insertLog(pathCopy.toString());
			}
		} else {
			for (File file : files) {
				System.out.println("Obtido: " + pathCopy.getName());
				pathCopy = pathTarget;
				insertLog(pathCopy.toString());
			}
		}

		return true;
	}

	public boolean insertLog(String source) {
		// Document document = new Document(source);

		// Logger log = logIn(); //criando arquivo de log

		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(insertFile(source).toCharArray()); // adicionar o//
															// source ao parser

		parser.setCompilerOptions(JavaCore.getOptions());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);

		CompilationUnit unit = (CompilationUnit) parser.createAST(null);
		// criar AST do tipo CompilationUnit (a raiz contem o arquivo completo)

		AST ast = unit.getAST();

		List<ASTNode> astnodes = ASTnodeFinder.getASTnodes(unit);
		for (ASTNode astnode : astnodes) {

			if (currentLine != unit.getLineNumber(astnode.getStartPosition())) {
				if (linhas.indexOf(currentLine) != -1)
					currentLine = unit.getLineNumber(astnode.getStartPosition());
				linhas.add(currentLine);
			}

		}

		for (Integer linha : linhas) {
			currentLine = linha;
			insertLog(ast, unit);

		}
		insertPackage(pathCopy.getPath());

		return true;
	}

	public void insertLog(AST ast, CompilationUnit unit) {
		MethodInvocation methodInvocation = ast.newMethodInvocation();
		MethodInvocation methodInvocation2 = ast.newMethodInvocation();// logIn().info(msg);
		MethodInvocation methodInvocation3 = ast.newMethodInvocation();
		SimpleName qName = ast.newSimpleName("CoverageTransformer");

		methodInvocation.setExpression(qName);
		methodInvocation.setName(ast.newSimpleName("getInstance"));

		methodInvocation3.setName(ast.newSimpleName("logIn"));
		methodInvocation3.setExpression(methodInvocation);

		methodInvocation2.setExpression(methodInvocation3);
		methodInvocation2.setName(ast.newSimpleName("info"));

		StringLiteral sl = ast.newStringLiteral();
		sl.setLiteralValue("loggin");
		methodInvocation3.arguments().add(sl);
		StringLiteral sl2 = ast.newStringLiteral();
		sl2.setLiteralValue("*" + currentLine + "*");
		methodInvocation2.arguments().add(sl2);

		saveFile(pathCopy, insertFile(pathCopy.toString()), methodInvocation2);

	}

	public boolean insertPackage(String source) {

		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(insertFile(source).toCharArray()); // adicionar o//
															// source ao parser
		parser.setCompilerOptions(JavaCore.getOptions());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);

		CompilationUnit unit = (CompilationUnit) parser.createAST(null);
		// criar AST do tipo CompilationUnit (a raiz contem o arquivo completo)

		AST ast = unit.getAST();
		List<ASTNode> astnodes = ASTnodeFinder.getASTnodes(unit);
		for (ASTNode astnode : astnodes) {

			if (astnode instanceof PackageDeclaration) {
				unit.getPackage().delete();
				PackageDeclaration pd = ast.newPackageDeclaration();
				pd.setName(ast.newName("target"));// this.pathTargetold.toString()));
				unit.setPackage(pd);

				ImportDeclaration id = ast.newImportDeclaration();
				id.setName(ast.newQualifiedName(ast.newSimpleName("codcoverage"),
						ast.newSimpleName("CoverageTransformer")));
				unit.imports().add(id);
			}

			saveFile(pathCopy, unit.getRoot().toString());
			return true;
		}

		return true;
	}

	public static Logger logIn(String logName) {

		log = Logger.getLogger("Log");

		try {
			if (filetxt == null) {
				filetxt = new FileHandler(logName + ".txt");
			}

		} catch (IOException e) {

		}
		filetxt.setFormatter(new SimpleFormatter());

		log.addHandler(filetxt);

		return log;

	}
	//Printa o log na funcao
	public static MethodInvocation logStatement(AST ast, String string) {
		
		  MethodInvocation methodInvocation = ast.newMethodInvocation();
		  
	        QualifiedName qName =
	                   ast.newQualifiedName(
	                            ast.newSimpleName("System"),
	                            ast.newSimpleName("out"));
	        methodInvocation.setExpression(qName);		
	        methodInvocation.setName(ast.newSimpleName("println"));

	        StringLiteral literal = ast.newStringLiteral();
	        literal.setLiteralValue(string);
	        System.out.println(methodInvocation.arguments().toString());	
	        methodInvocation.arguments().add(literal);
	        
	        return methodInvocation;
	}
	
	public void instrumentation() throws IOException {
//		String source = String.join("\n",
//		        "public class HelloWorld {",
//		        "    public static void main(String[] args) {",
//		                  // Insert the following statement.
//		                  // System.out.println("Hello, World");
//		        "    }",
//		        "}");
		
		
		
		String source2 = "src/runner/";
		String[] extensions = { "java" };
		File directory = new File(source2);
		String source = "";

		files = FileUtils.listFiles(directory, extensions, false);
		// pathSource = pathTarget.toString();
		for (File file : files) {

			System.out.println("Obtido: " + file.getAbsoluteFile());
			source = new String ( Files.readAllBytes( Paths.get(file.getAbsolutePath()) ) );
			System.out.println("Obtido: " + source);
		}
		
		//

		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setSource(source.toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);		

		CompilationUnit unit = (CompilationUnit) parser.createAST(new NullProgressMonitor());
		unit.accept(new ASTVisitor() {
			
		    @SuppressWarnings("unchecked")
		    public boolean visit(MethodDeclaration node) {
		        AST ast = node.getAST();
		      		
//		        node.getBody().statements().add(ast.newExpressionStatement(logStatement(ast, "teste")));
		        List aux = new ArrayList(node.getBody().statements());
		        aux = Collections.unmodifiableList(aux);
		        
		        for(Object block: aux) {
		   
		        	if(block != "else") {
		        		 node.getBody().statements().add(ast.newExpressionStatement(logStatement(ast, "novo")));
		        	}
		        }
		        
		        //System.out.println(node.toString());		
		        System.out.println(unit.toString());		
		        return super.visit(node);
		    }
		});
	}
	

	public static void main(String[] args) {

		CoverageTransformer cc = CoverageTransformer.getInstance();
//		cc.analise();
		
		try {
			cc.instrumentation();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}

}
