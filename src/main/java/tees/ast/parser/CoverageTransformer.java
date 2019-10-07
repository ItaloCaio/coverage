package tees.ast.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import javax.swing.text.BadLocationException;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ImportRewrite;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

public class CoverageTransformer {

	private static String path = "./target/";

	public static void salvarArquivo(String path, String conteudo, String className) {

		File file = new File(path + className + ".java");
		FileWriter arquivo;
		try {
			if (file.createNewFile()) {
//				System.out.println("File is created!");
//				FileUtils.write(file, conteudo);
				
				arquivo = new FileWriter(file.getAbsolutePath());
				PrintWriter gravador = new PrintWriter(arquivo);
				gravador.printf(conteudo);
				arquivo.close();
			} else {
				System.out.println("File already exists.");
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	// use ASTParse to parse string
	public static void parse(String str) {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(str.toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);

		final CompilationUnit cu = (CompilationUnit) parser.createAST(null);

		cu.accept(new ASTVisitor() {

			Set varNames = new HashSet();
			Set methodNames = new HashSet();

			public boolean visit(MethodInvocation node) {
				SimpleName name = node.getName();
				/*
				 * System.out.println("Invocando do metodo " + name + "' na linha "+
				 * cu.getLineNumber(name.getStartPosition()));
				 */
				return false;
			}

			@SuppressWarnings("unchecked")
			public boolean visit(MethodDeclaration node) {

				SimpleName name = node.getName();
				this.methodNames.add(name.getIdentifier());
				// System.out.println("Declarando do metodo '" + name + "' na linha "
				// + cu.getLineNumber(name.getStartPosition()));
				AST ast = node.getAST();
				MethodInvocation methodInvocation = ast.newMethodInvocation();

				QualifiedName qName = ast.newQualifiedName(ast.newSimpleName("System"), ast.newSimpleName("out"));

				methodInvocation.setExpression(qName);
				methodInvocation.setName(ast.newSimpleName("println"));

				StringLiteral literalStart = ast.newStringLiteral();
				literalStart.setLiteralValue("Executando do metodo: " + name);

				methodInvocation.arguments().add(literalStart);

				if (Character.isUpperCase(name.toString().charAt(0))) {
					salvarArquivo(path, cu.toString(), name.toString());
				}

				// Append the statement
				node.getBody().statements().add(0, ast.newExpressionStatement(methodInvocation));
				// System.out.println(node.getBody().toString());
				
				return super.visit(node);
			}

			public boolean visit(VariableDeclarationFragment node) {

				SimpleName name = node.getName();
				this.varNames.add(name.getIdentifier());
				return false; // do not continue
			}

			public boolean visit(SimpleName node) {

				if (this.varNames.contains(node.getIdentifier())) {
					// System.out.println("Uso da variavel '" + node + "' na linha "
					// + cu.getLineNumber(node.getStartPosition()));
				}
				return true;
			}
		});

	}

	// read file content into a string
	public static String readFileToString(String filePath) throws IOException {
		StringBuilder fileData = new StringBuilder(1000);
		BufferedReader reader = new BufferedReader(new FileReader(filePath));

		char[] buf = new char[10];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			// System.out.println(numRead);
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}

		reader.close();

		return fileData.toString();
	}

	// loop directory to get file list
	public static void ParseFilesInDir() throws IOException {
		File dirs = new File(".");
		String dirPath = dirs.getCanonicalPath() + File.separator + "src/main/java/tees/ast/parser/triangle"
				+ File.separator;

		File root = new File(dirPath);
		// System.out.println(root.listFiles());
		File[] files = root.listFiles();
		String filePath = null;

		for (File f : files) {
			filePath = f.getAbsolutePath();

			if (f.isFile()) {
				// path = f.getAbsolutePath();
				parse(readFileToString(filePath));
			}
		}
	}

	protected void saveChanges(ICompilationUnit cu, IProgressMonitor monitor, final ASTRewrite rewriter,
			ImportRewrite importRewrite) throws CoreException, JavaModelException, BadLocationException,
			MalformedTreeException, org.eclipse.jface.text.BadLocationException {
		TextEdit importEdits = importRewrite.rewriteImports(monitor);
		TextEdit edits = rewriter.rewriteAST();
		importEdits.addChild(edits);

		// apply the text edits to the compilation unit
		Document document = new Document(cu.getSource());
		importEdits.apply(document);

		// save the compilation unit
		cu.getBuffer().setContents(document.get());
		cu.save(monitor, true);
	}

	public static void main(String[] args) throws IOException {
		ParseFilesInDir();
	}

}
