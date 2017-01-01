package co.ke.workpoint.store.transaction;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import co.ke.workpoint.store.dao.DB;
import co.ke.workpoint.store.helpers.SessionHelper;

public class TransactionFilter implements Filter{

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest httpRequest = (HttpServletRequest)request;
		HttpServletResponse httpResponse = (HttpServletResponse)response;
		
		SessionHelper.setHttpRequest(httpRequest);
		SessionHelper.setHttpResponse(httpResponse);
		
		try{
			DB.beginTransaction();
			chain.doFilter(request, response);
			DB.commitTransaction();
		}catch(Exception e){
			DB.rollback();
			throw e;
		}finally{
			SessionHelper.setHttpRequest(null);
			SessionHelper.setHttpResponse(null);
			DB.clearSession();
		}
	}
	
	@Override
	public void destroy() {
		
	}
}
