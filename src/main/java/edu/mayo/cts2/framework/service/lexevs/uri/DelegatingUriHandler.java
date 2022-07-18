/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.service.lexevs.uri;

import java.util.Collections;
import java.util.List;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeSummary;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedCodedNodeReference;
import org.LexGrid.codingSchemes.CodingScheme;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.core.OrderComparator;
import org.springframework.stereotype.Component;

/**
 * The Class DelegatingUriHandler.
 *
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
@Component
@Primary
public class DelegatingUriHandler implements UriHandler, InitializingBean {

	private List<DelegateUriHandler> delegateUriHandlers;
	
	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */

	public void afterPropertiesSet() throws Exception {
		Collections.sort(this.delegateUriHandlers, OrderComparator.INSTANCE);
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.lexevs.uri.UriHandler#getEntityUri(org.LexGrid.LexBIG.DataModel.Core.ResolvedCodedNodeReference)
	 */

	public String getEntityUri(final ResolvedCodedNodeReference reference) {
		return this.doIn(new DoInDelegates(){

			public String f(UriHandler uriHandler) {
				return uriHandler.getEntityUri(reference);
			}	
		});
	}
	
	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.lexevs.uri.UriHandler#getCodeSystemUri(org.LexGrid.codingSchemes.CodingScheme)
	 */

	public String getCodeSystemUri(final CodingScheme codingScheme) {
		return this.doIn(new DoInDelegates(){

			public String f(UriHandler uriHandler) {
				return uriHandler.getCodeSystemUri(codingScheme);
			}	
		});
	}
	

	public String getCodeSystemUri(final CodingSchemeSummary codingScheme) {
		return this.doIn(new DoInDelegates(){

			public String f(UriHandler uriHandler) {
				return uriHandler.getCodeSystemUri(codingScheme);
			}	
		});
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.lexevs.uri.UriHandler#getCodeSystemVersionUri(org.LexGrid.codingSchemes.CodingScheme)
	 */

	public String getCodeSystemVersionUri(final CodingScheme codingScheme) {
		return this.doIn(new DoInDelegates(){

			public String f(UriHandler uriHandler) {
				return uriHandler.getCodeSystemVersionUri(codingScheme);
			}			
		});
	}


	public String getCodeSystemVersionUri(final CodingSchemeSummary codingSchemeSummary) {
		return this.doIn(new DoInDelegates(){

			public String f(UriHandler uriHandler) {
				return uriHandler.getCodeSystemVersionUri(codingSchemeSummary);
			}			
		});
	}


	public String getPredicateUri(
			final String codingSchemeUri,
			final String codingSchemeVersion, 
			final String associationName) {
		return this.doIn(new DoInDelegates(){

			public String f(UriHandler uriHandler) {
				return uriHandler.getPredicateUri(
						codingSchemeUri, 
						codingSchemeVersion, 
						associationName);
			}			
		});
	}
	
	private interface DoInDelegates {
		public String f(UriHandler uriHandler);
	}
	
	protected String doIn(DoInDelegates doIn){
		for(UriHandler handler : this.delegateUriHandlers){
			String uri = doIn.f(handler);
			if(StringUtils.isNotBlank(uri)){
				return uri;
			}
		}
		
		throw new IllegalStateException("Uri not found - please implement a Fallback Handler.");
	}
	
	public List<DelegateUriHandler> getDelegateUriHandlers() {
		return delegateUriHandlers;
	}

	@Autowired
	public void setDelegateUriHandlers(List<DelegateUriHandler> delegateUriHandlers) {
		this.delegateUriHandlers = delegateUriHandlers;
	}


}
