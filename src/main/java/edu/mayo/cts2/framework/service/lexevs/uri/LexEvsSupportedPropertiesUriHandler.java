/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.service.lexevs.uri;

import jakarta.annotation.Resource;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeSummary;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedCodedNodeReference;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.codingSchemes.CodingScheme;
import org.LexGrid.naming.SupportedAssociation;
import org.LexGrid.naming.SupportedNamespace;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

/**
 * Returns a URI based on the LexEVS SupportedProperties, if it can find it.
 *
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
@Component
public class LexEvsSupportedPropertiesUriHandler implements DelegateUriHandler {

	@Resource
	private LexBIGService lexBigService;
	
	/* 
	 * This constructs an Entity URI based on the SupportedNamespace
	 * of LexEVS.
	 * 
	 * (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.lexevs.uri.UriHandler#getEntityUri(org.LexGrid.LexBIG.DataModel.Core.ResolvedCodedNodeReference)
	 */
	@Override
	public String getEntityUri(ResolvedCodedNodeReference reference) {
		String codingSchemeName = reference.getCodingSchemeName();
		String version = reference.getCodingSchemeVersion();
		
		String name = reference.getCode();
		String namespace = reference.getCodeNamespace();

		CodingScheme codingScheme;
		try {
			codingScheme = this.lexBigService.resolveCodingScheme(
					codingSchemeName, 
					Constructors.createCodingSchemeVersionOrTagFromVersion(version));
		} catch (LBException e) {
			throw new RuntimeException(e);
		}
		
		SupportedNamespace sns = 
			this.findSupportedNamespace(
				namespace,
				codingScheme.getMappings().getSupportedNamespace());
		
		if(sns != null && StringUtils.isNotEmpty(sns.getUri())){
			return UriUtils.combine(sns.getUri(), name);
		} else {
			return null;
		}
	}
	
	private SupportedNamespace findSupportedNamespace(String namespace, SupportedNamespace[] namespaces){
		for(SupportedNamespace sns : namespaces){
			if(sns.getLocalId().equals(namespace)){
				return sns;
			}
		}
		return null;
	}
	
	private SupportedAssociation findSupportedAssociation(String association, SupportedAssociation[] associations){
		for(SupportedAssociation sa : associations){
			if(sa.getLocalId().equals(association)){
				return sa;
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.lexevs.uri.UriHandler#getCodeSystemUri(org.LexGrid.codingSchemes.CodingScheme)
	 */
	@Override
	public String getCodeSystemUri(CodingScheme codingScheme) {
		return codingScheme.getCodingSchemeURI();
	}
	
	@Override
	public String getCodeSystemUri(
			CodingSchemeSummary codingSchemeSummary) {
		return codingSchemeSummary.getCodingSchemeURI();
	}

	@Override
	public String getPredicateUri(
			String codingSchemeUri,
			String codingSchemeVersion, 
			String associationName) {
		CodingScheme codingScheme;
		try {
			codingScheme = this.lexBigService.resolveCodingScheme(
					codingSchemeUri, 
					Constructors.createCodingSchemeVersionOrTagFromVersion(codingSchemeVersion));
		} catch (LBException e) {
			throw new RuntimeException(e);
		}
		
		SupportedAssociation supportedAssociation =  
			this.findSupportedAssociation(
				associationName, 
				codingScheme.getMappings().getSupportedAssociation());
		
		String uri = null;
		if(supportedAssociation != null){
			uri = supportedAssociation.getUri();
		} 
		
		return uri;
	}

	/* (non-Javadoc)
	 * @see edu.mayo.cts2.framework.service.lexevs.uri.UriHandler#getCodeSystemVersionUri(org.LexGrid.codingSchemes.CodingScheme)
	 */
	@Override
	public String getCodeSystemVersionUri(CodingScheme codingScheme) {
		return null;
	}
	
	@Override
	public String getCodeSystemVersionUri(
			CodingSchemeSummary codingSchemeSummary) {
		return null;
	}

	@Override
	public int getOrder() {
		return 1;
	}

}
