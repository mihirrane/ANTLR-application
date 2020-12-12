import java.io.IOException;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;

public class JavaParserTest extends Java8BaseListener{

	public static void main(String[] args) throws IOException {
//		if(args.length<1)
//		{
//			System.err.println("java JavaParserTest input-filename\n"
//					+"Example: java JavaParserTest Test.java");
//			return;
//		}
		String inputFile = "./src/Test.java";
    	CharStream input = new ANTLRFileStream(inputFile);
    	Java8Lexer lexer = new Java8Lexer(input);
    	CommonTokenStream tokens = new CommonTokenStream(lexer);
    	Java8Parser parser = new Java8Parser(tokens); //create parser

    	ParseTree tree = parser.compilationUnit();
    	ParseTreeWalker walker = new ParseTreeWalker(); // create standard walker
    	JavaParserTest listener = new JavaParserTest(); // create a parse tree listener
    	walker.walk(listener, tree); // traverse parse tree with listener
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void enterStatement(Java8Parser.StatementContext ctx){
		if(ctx.getStart().getText().equals("if")) {
			String original = ctx.getChild(0).getChild(2).getText();
			String candidate = original.toLowerCase();
			int line_number = ctx.getStart().getLine();
			
			if(!candidate.startsWith("!") && (candidate.length()>3) && candidate.matches("[a-z]+[0-9]*"))
				System.out.println(original +" " + line_number);
		}
	}
}
