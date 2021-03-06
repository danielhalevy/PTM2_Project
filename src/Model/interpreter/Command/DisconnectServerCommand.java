package Model.interpreter.Command;

import Model.interpreter.DataReaderServer;

public class DisconnectServerCommand implements Command {

	@Override
	public int doCommand(String[] args) throws Exception {

		DataReaderServer.close();
		ConnectCommand.close();
		System.out.println("The server listener and the Connection to the simulator server closed");
		return 0;
	}

}
