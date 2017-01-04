package co.ke.workpoint.store.security;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import co.ke.workpoint.store.helpers.ServerConstants;

public class SecurityFilter implements Filter {

	String loginPage;

	Logger logger = Logger.getLogger(SecurityFilter.class);

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		if (filterConfig != null) {
			loginPage = filterConfig.getInitParameter("login_page");
		}
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		if (loginPage == null) {
			logger.fatal("AuthorizationFilter not "
					+ "properly configured! Contact Administrator.");
			returnError(request, response, "AuthorizationFilter not "
					+ "properly configured! Contact Administrator.");
		}

		HttpServletRequest httpRequest = (HttpServletRequest) request;

		boolean isValid = false;

		if (!httpRequest.getMethod().equalsIgnoreCase("GET")) {
			HttpSession session = httpRequest.getSession(false);
			if (session == null) {
				logger.debug("A session does not exist for this request!");
				returnError(request, response,
						"A session does not exist for this request!");
				return;
			}else{
				isValid = true;
			}
		}

		if (isValid) {
			logger.debug("#Security Filter - Authentication success! - forwarding request");
			chain.doFilter(request, response);
		}
	}

	private String getContextPath(HttpServletRequest request) {
		// String contextPath = request.getServletContext().getContextPath();
		String contextPath = request.getContextPath();

		if (!contextPath.isEmpty() && !contextPath.equals("/")) {
			contextPath = (contextPath.startsWith("/") ? "" : "/")
					+ contextPath + "/";
		} else {
			contextPath = "/";
		}
		return contextPath;
	}

	/** Accepts error string, forwards to error page with error. */
	private void returnError(ServletRequest request, ServletResponse aResponse,
			String errorString) throws ServletException, IOException {
		HttpServletResponse response = ((HttpServletResponse) aResponse);
		response.setStatus(403);
		response.setContentType("text/html");
		response.getWriter().print(errorString);
		response.sendRedirect(loginPage);
	}

	@Override
	public void destroy() {

	}

}
