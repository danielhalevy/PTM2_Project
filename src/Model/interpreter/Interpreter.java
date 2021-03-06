package Model.interpreter;

import java.io.IOException;
import Model.interpreter.Command.BindCommand;
import Model.interpreter.Command.ConnectCommand;
import Model.interpreter.Command.DisconnectServerCommand;
import Model.interpreter.Command.EqualCommand;
import Model.interpreter.Command.LoopCommand;
import Model.interpreter.Command.OpenServerCommand;
import Model.interpreter.Command.PrintCommand;
import Model.interpreter.Command.ReturnCommand;
import Model.interpreter.Command.SleepCommand;
import Model.interpreter.Command.VarDeclarationCommand;

public class Interpreter {

	public static int interpret(String[] lines) {
		Lexer lexer = new Lexer();
		Parser parser = new Parser();

		Utilities.setCommand("return", new ReturnCommand());
		Utilities.setCommand("var", new VarDeclarationCommand());
		Utilities.setCommand("equal", new EqualCommand());
		Utilities.setCommand("openDataServer", new OpenServerCommand());
		Utilities.setCommand("connect", new ConnectCommand());
		Utilities.setCommand("disconnect", new DisconnectServerCommand());
		Utilities.setCommand("Loop", new LoopCommand());
		Utilities.setCommand("bind", new BindCommand());
		Utilities.setCommand("Print", new PrintCommand());
		Utilities.setCommand("Sleep", new SleepCommand());

		try 
		{
			parser.parser(lines, lexer);
			//Reset the ValuesTable
			Utilities.resetVariablesTable();
		} catch (IOException e) {e.printStackTrace();}
		return 0;
	}
}
