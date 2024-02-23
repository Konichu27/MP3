package models;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Edrine Frances
 * @author Leonne Matthew Dayao
 * @author Rayna Gulifardo
 * 2CSC - CICS - University of Santo Tomas
 */

public class UserDatabase
{
    public Account loginRequest(Connection con, String uname, String pword) throws AuthenticationException, NullValueException, ServerAuthenticationException
            {
                if (uname == null || pword == null || uname.equals("") || pword.equals(""))
                    // ERROR: One of the input strings are empty.
                    // Controller catches this, redirects to an error jsp.
                    throw new NullValueException("Input fields must not be empty.");
                try (PreparedStatement psAcc = con.prepareStatement("SELECT * FROM USERS WHERE Email = ?"))
                {
                    // Verify W/ Server
                    psAcc.setString(1, uname);
                    try (ResultSet rsAcc = psAcc.executeQuery()) {
                        if (rsAcc.next())
                        { // next() method returns false if no corresp. entry is found.
                            if (rsAcc.getString("Password").equals(pword))
                            {
                                Account acc = new Account(uname, pword, rsAcc.getString("UserRole"));
                                rsAcc.close();
                                psAcc.close();
                                return acc;
                            }
                        }
                    }
                    // ERROR: Credentials do not match.
                    // Controller catches this, redirects to an error jsp.
                    throw new AuthenticationException("Username or password does not match.");
                } catch (SQLException sqle)
                {
                    // ERROR: There was a problem with the server.
                    // Controller catches this, redirects to an error jsp.
                    throw new ServerAuthenticationException("There was a problem during the login. Please try again.");
                }
            }
}

class Account {
    String uname, pword, urole;
    Account(String uname, String pword, String urole) {
        this.uname = uname;
        this.pword = pword;
        this.urole = urole;
    }
    public String getUname() {
        return uname;
    }
    public String getPword() {
        return pword;
    }
    public String getUrole() {
        return urole;
    }
    public void setUname(String uname) {
        this.uname = uname;
    }
    public void setPword(String pword) {
        this.pword = pword;
    }
    public void setUrole(String urole) {
        this.urole = urole;
    }
}