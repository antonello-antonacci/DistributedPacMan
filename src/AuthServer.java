import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.Naming;
import java.util.Properties;

public class AuthServer {
    public static void main(String[] args) {
        try {
            Properties props = System.getProperties();
            System.out.println(getIpAddress());
            props.setProperty("Djava.rmi.server.hostname", getIpAddress());
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        int nPacs;
        int nGhosts;

        
        if(args.length == 0){nPacs = 1; nGhosts = 1;}
        else if(args.length == 1){nPacs=Integer.parseInt(args[0]); nGhosts = 1;}
        else{nPacs = Integer.parseInt(args[0]); nGhosts = Integer.parseInt(args[1]);}
        

        try {
        	System.out.println("nPacs :"+ nPacs + "nGhosts :"+ nGhosts+ ".");
            AuthServerImpl register = new AuthServerImpl(nPacs,nGhosts);
            
            Naming.rebind(AuthServerInterface.LOOKUPNAME, register);

            System.out.println("Registration Server ready");
        } catch (Exception e) {
            System.err.println(e);
            System.exit(1);
        }
    }

    public static String getIpAddress() throws MalformedURLException, IOException {
        URL url = new URL("http://checkip.amazonaws.com/");
        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        return br.readLine();
    }
}