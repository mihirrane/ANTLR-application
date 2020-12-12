
import java.io.IOException;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStreamRewriter;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.io.File;
import java.io.FileWriter;

public class PrintBoolean extends Java8BaseListener{
	
	static TokenStreamRewriter rewriter;
	int offset = 0;

	public static void main(String[] args) throws IOException {
//		if(args.length<1)
//		{
//			System.err.println("java JavaParserTest input-filename\n"
//					+"Example: java JavaParserTest Test.java");
//			return;
//		}
		/* place Test.java in the same directory */
		String inputFile = "./src/Test.java";
    	CharStream input = CharStreams.fromFileName(inputFile);
    	Java8Lexer lexer = new Java8Lexer(input);
    	CommonTokenStream tokens = new CommonTokenStream(lexer);
    	Java8Parser parser = new Java8Parser(tokens); //create parser
    	rewriter = new TokenStreamRewriter(tokens);
    	ParseTree tree = parser.compilationUnit();
    	ParseTreeWalker walker = new ParseTreeWalker(); // create standard walker
    	PrintBoolean listener = new PrintBoolean(); // create a parse tree listener
    	walker.walk(listener, tree); // traverse parse tree with listener
    	
    	String outputFileName = "./src/BooleanOutputPrinted.java";
    	File fileObj = new File(outputFileName);
    	fileObj.createNewFile();
    	FileWriter fileWriter = new FileWriter(outputFileName);
    	fileWriter.write(rewriter.getText());
    	fileWriter.close();
    	System.out.println("File created: " + fileObj.getName());
}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void enterStatement(Java8Parser.StatementContext ctx){

//      part 3
		if(ctx.ifThenElseStatement()!=null || ctx.ifThenStatement()!= null) {
			String original = ctx.getChild(0).getChild(2).getText();
			String candidate = original.toLowerCase();
			int line_number = ctx.start.getLine();
			
			
			if(!candidate.startsWith("!") && (candidate.length()>3) && candidate.matches("[a-z]+[0-9]*")) {
				Interval if_body_interval = ctx.getChild(0).getChild(4).getSourceInterval();
				int start = if_body_interval.a;
				int end = if_body_interval.b;
				boolean	noBrackets = true;
				String spaces = "";
				
				for(int i = 0; i <= ctx.start.getCharPositionInLine(); i++)	
					/* append tabs to maintain indentation*/
					spaces += "\t";
					
				/* check if there are brackets in if condition*/
				noBrackets = (ctx.getChild(0).getChild(4).getText().charAt(0) != '{');
				
				/* print boolean variable */
				System.out.println(candidate);
				
				if(noBrackets){
					/* when there are no brackets to the if condition*/
					offset++;
					rewriter.insertBefore(start, "{\n" + spaces + "System.out.println(\"" +original+" "+(line_number + offset)+"\");\n" + spaces);
					spaces = spaces.substring(0, spaces.length() - 1);
					rewriter.insertAfter(end, "\n" + spaces + "}");
					offset+=2;
				}
				else {
					/* when the if condition already has brackets*/
					offset++;
					rewriter.insertAfter(start, "\n" + spaces + "System.out.println(\""+original+" "+(line_number + offset)+"\");\n");
					offset++;
				}
			}
		}
	}
}
