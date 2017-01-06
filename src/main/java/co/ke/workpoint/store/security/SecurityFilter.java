package co.ke.workpoint.store.security;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

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

		boolean isValid = true;

		//url method is not GET or resource is not an api resource.
		logger.info("Security Filter - Request Url = "+httpRequest.getRequestURI());
		boolean securedResource = !httpRequest.getMethod().equalsIgnoreCase("GET") || !httpRequest.getRequestURI().contains("api"); 
		if (securedResource) {
			HttpSession session = httpRequest.getSession(false);
			if (session == null) {
				isValid = false;
				logger.debug("A session does not exist for this request!");
				returnError(request, response,
						"A session does not exist for this request!");
				return;
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
	private void returnError(ServletRequest aRequest, ServletResponse aResponse,
			String errorString) throws ServletException, IOException {
		HttpServletRequest request = (HttpServletRequest)aRequest; 
		HttpServletResponse response = ((HttpServletResponse) aResponse);
		response.setStatus(403);
		response.setContentType("text/html");
		response.getWriter().print(errorString);
		
		String redirect = "?redirect="+request.getRequestURI();
		
		StringBuffer params = new StringBuffer();
		Enumeration<String> names = request.getParameterNames();
		while(names.hasMoreElements()){
			String name = names.nextElement();
			if(params.length()==0){
				params.append("?"+name+"="+request.getParameter(name));
			}else{
				params.append("&"+name+"="+request.getParameter(name));
			}
			
		}
		
		redirect = redirect + params.toString();
		logger.debug("Redirect = "+redirect);
		response.sendRedirect(loginPage+redirect);
	}

	@Override
	public void destroy() {

	}

}
