package server;

import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;
import java.sql.*;

public class Server extends AbstractServer {
	//Class variables ***************************************************
	  
	  /**
	   * The default port to listen on.
	   */
	  final public static int DEFAULT_PORT = 5555;
	  private static Connection conn;
	  //Constructors ****************************************************
	  
	  /**
	   * Constructs an instance of the echo server.
	   *
	   * @param port The port number to connect on.
	   */
	  public Server(int port) 
	  {
	    super(port);
	  }

	  
	  //Instance methods ************************************************
	  
	  /**
	   * This method handles any messages received from the client.
	   *
	   * @param msg The message received from the client.
	   * @param client The connection from which the message originated.
	   */
	  
	  
	  public void handleMessageFromClient
	    (Object msg, ConnectionToClient client)
	  {
		  Statement stmt;
		  try 
		  {
			  ResultSet rs;
			  stmt = conn.createStatement();
			  switch(Integer.parseInt((String) msg)){
			  case 1:
				  rs =stmt.executeQuery("Select * from test where id=1;");
				  while(rs.next())
				  {
					  this.sendToAllClients(rs.getString(1)+"  " +rs.getString(2));
				  } 
				  rs.close();
				  break;
			  case 2:
				  stmt.executeUpdate("Insert into test values (2,'B');");
				  this.sendToAllClients("Successful");
				  break;
			  }
		  } catch (SQLException e) 
		  {
			  this.sendToAllClients("Faild!");
		  }
		  
	  }

	    
	  /**
	   * This method overrides the one in the superclass.  Called
	   * when the server starts listening for connections.
	   */
	  protected void serverStarted()
	  {
		  System.out.println
		    ("Server listening for connections on port " + getPort());
	  }
	  
	  /**
	   * This method overrides the one in the superclass.  Called
	   * when the server stops listening for connections.
	   */
	  protected void serverStopped()
	  {
		  System.out.println
	       ("Server has stopped listening for connections.");
	  }
	  
	  //Class methods ***************************************************
	  
	  /**
	   * This method is responsible for the creation of 
	   * the server instance (there is no UI in this phase).
	   *
	   * @param args[0] The port number to listen on.  Defaults to 5555 
	   *          if no argument is entered.
	   */
	  public static void main(String[] args) 
	  {
		  int port = 0; //Port to listen on
		  try 
		  {
			  Class.forName("com.mysql.jdbc.Driver").newInstance();
		  } catch (Exception ex) {/* handle the error*/}
	    
		  try 
		  {
			  conn = DriverManager.getConnection("jdbc:mysql://localhost/ass_2",args[1],args[2]);
			  System.out.println("SQL connection succeed");
		  } catch (SQLException ex) 
		  {/* handle any errors*/
			  System.out.println("SQLException: " + ex.getMessage());
			  System.out.println("SQLState: " + ex.getSQLState());
			  System.out.println("VendorError: " + ex.getErrorCode());
		  }
		  catch (ArrayIndexOutOfBoundsException ex){
			  System.out.println("Command line parametres: [listening port] [DB user] [DB password]");
			  System.exit(1);
		  }

		  try
		  {
			  port = Integer.parseInt(args[0]); //Get port from command line
		  }
		  catch(Throwable t)
		  {
			  port = DEFAULT_PORT; //Set port to 5555
		  }
		
		  Server sv = new Server(port);
	    
		  try 
		  {
			  sv.listen(); //Start listening for connections
		  } 
		  catch (Exception ex) 
		  {
			  System.out.println("ERROR - Could not listen for clients!");
		  }
	  }

}
