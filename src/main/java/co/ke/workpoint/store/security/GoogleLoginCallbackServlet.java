package co.ke.workpoint.store.security;

import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import co.ke.workpoint.store.dao.DBExecute;
import co.ke.workpoint.store.helpers.ServerConstants;
import co.ke.workpoint.store.model.User;

import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

public class GoogleLoginCallbackServlet extends BaseServlet {

	Logger logger = Logger.getLogger(GoogleLoginCallbackServlet.class);
	String app_page = null;
	GoogleClientSecrets clientSecrets = null;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		if (config != null) {
			app_page = config.getInitParameter("app_page");
		}
		
		String CLIENT_SECRET_FILE = System
				.getProperty(GoogleAuthenticationManager.Google_OAuth_Client_Secret_File);

		// Exchange auth code for access token
		try {
			clientSecrets = GoogleClientSecrets.load(
					JacksonFactory.getDefaultInstance(), new InputStreamReader(
							getClass().getClassLoader().getResourceAsStream("client_secret.apps.google.json")));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		initRequest(req, resp);
	}

	@Override
	protected void executeRequest(HttpServletRequest req,
			HttpServletResponse resp) throws ServletException, IOException {
		
		logger.info("Executing GoogleLoginCallback! -- ");
		
		// (Receive authCode via HTTPS POST)
		String authCode = req.getReader().readLine();
		String REDIRECT_URI = "postmessage";

		GoogleTokenResponse tokenResponse = null;

		HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
		JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
		String CLIENT_ID = clientSecrets.getDetails().getClientId();
		String CLIENT_SECRET = clientSecrets.getDetails().getClientSecret();
		
		try {
			tokenResponse = new GoogleAuthorizationCodeTokenRequest(
					HTTP_TRANSPORT, JSON_FACTORY,
					"https://www.googleapis.com/oauth2/v4/token",CLIENT_ID, CLIENT_SECRET, authCode,
					REDIRECT_URI).execute();
		} catch (TokenResponseException e) {
			if (e.getDetails() != null) {
				logger.warn("Error: " + e.getDetails().getError());
				if (e.getDetails().getErrorDescription() != null) {
					logger.warn(e.getDetails().getErrorDescription());
				}
				if (e.getDetails().getErrorUri() != null) {
					logger.warn(e.getDetails().getErrorUri());
				}
			} else {
				logger.warn(e.getMessage());
			}

			throw e;
		}

		String accessToken = tokenResponse.getAccessToken();

		// Get profile info from ID token
		GoogleIdToken idToken = tokenResponse.parseIdToken();
		GoogleIdToken.Payload payload = idToken.getPayload();
		String userId = payload.getSubject(); // Use this value as a key to
												// identify a user.
		
//		Collection<String> SCOPE = Arrays.asList("profile", "email");
//		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
//				HTTP_TRANSPORT, JSON_FACTORY, CLIENT_ID, CLIENT_SECRET, SCOPE).setAccessType("offline")
//				.setApprovalPrompt("force")
//				.build();
//		Credential credential = flow.loadCredential(payload.getEmail());
//		
//		if(credential==null){
//			credential = flow.createAndStoreCredential(tokenResponse, userId);
//		}
		
		registerAndLoginUser(payload, req, resp);
	}

	protected void registerAndLoginUser(Payload payload, HttpServletRequest req,
			HttpServletResponse resp) throws IOException {
		final String email = payload.getEmail();
		boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
		String name = (String) payload.get("name");
		String pictureUrl = (String) payload.get("picture");
		String locale = (String) payload.get("locale");
		String familyName = (String) payload.get("family_name");
		String givenName = (String) payload.get("given_name");
		
		DBExecute<User> exec = new DBExecute<User>() {
			@Override
			protected String getQueryString() {
				
				return "select id,email,lastname,firstname,isactive from user where email=? and isactive=1";
			}
			
			@Override
			protected User processResults(PreparedStatement pStmt,
					boolean hasResults) throws SQLException {
				
				ResultSet rs = getResultSet();
				User user = null;
				if(rs.next()){
					user = new User();
					user.setId(rs.getInt(1));
					user.setEmail(rs.getString(2));
					user.setLastName(rs.getString(3));
					user.setFirstName(rs.getString(4));
					user.setIsActive(rs.getInt(5));
				}
				
				return user;
			}
			
			@Override
			protected void setParameters() throws SQLException {
				setString(1, email);
			}
		};
		
		User user = exec.executeDbCall();
		
		if(user==null){
			log.info("Login failure, redirecting to login - "+app_page);
			//resp.sendRedirect("login.html?failure");
			resp.getWriter().write("Unauthorized account.");
			resp.setStatus(403);
			return;
		}
		
		HttpSession session = req.getSession(true);
		session.setAttribute(ServerConstants.USER, user);
		
		if(!user.isSame(email,familyName,givenName,1)){
			user.setFirstName(givenName);
			user.setLastName(familyName);
			updateUser(user);
		}
		
		//log.info("Login success, redirecting to app - "+app_page);
		//resp.sendRedirect(app_page);
		resp.getWriter().write("Success");
	}

	private void updateUser(final User user) {
		DBExecute<User> exec = new DBExecute<User>() {
			@Override
			protected String getQueryString() {
				return "update user set isactive=1, lastname=?, firstname=? where id=?";
			}
			
			@Override
			protected User processResults(PreparedStatement pStmt,
					boolean hasResults) throws SQLException {
				return user;
			}
			
			@Override
			protected void setParameters() throws SQLException {
				setString(1, user.getLastName());
				setString(2, user.getFirstName());
				setInt(3, user.getId());
			}
		};
		
		exec.executeDbCall();
	}
}
