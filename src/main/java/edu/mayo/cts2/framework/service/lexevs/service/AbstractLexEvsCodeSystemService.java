/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.service.lexevs.service;

import edu.mayo.cts2.framework.model.command.ResolvedReadContext;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import edu.mayo.cts2.framework.service.lexevs.naming.CodingSchemeNameTranslator;
import edu.mayo.cts2.framework.service.lexevs.naming.NameVersionPair;
import edu.mayo.cts2.framework.service.lexevs.naming.VersionNameConverter;
import edu.mayo.cts2.framework.service.lexevs.uri.UriResolver;
import javax.annotation.Resource;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.codingSchemes.CodingScheme;

/**
 * A base service for all services needing to deal with LexEVS CodingSchemes.
 *
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
public abstract class AbstractLexEvsCodeSystemService<T> extends AbstractLexEvsService {
	
	@Resource
	private UriResolver uriResolver;
	
	@Resource
	private VersionNameConverter versionNameConverter;
	
	@Resource
	private CodingSchemeNameTranslator codingSchemeNameTranslator;
	
	@Resource
	private CodeSystemVersionUriResolver codeSystemVersionUriResolver;
	
	protected abstract T transform(CodingScheme codingScheme);

	protected T getByVersionIdOrTag(
			NameOrURI parentIdentifier,
			CodingSchemeVersionOrTag convertTag) {
		String id;
		if(parentIdentifier.getName() != null){
			id = parentIdentifier.getName();
		} else {
			id = parentIdentifier.getUri();
		}
		
		CodingSchemeVersionOrTag csvt;
		if(convertTag.getTag() != null){
			csvt = Constructors.createCodingSchemeVersionOrTagFromTag(convertTag.getTag());
		} else {
			csvt = Constructors.createCodingSchemeVersionOrTagFromVersion(convertTag.getVersion());
		}
		CodingScheme codingScheme = this.resolve(id,csvt);

		if(codingScheme != null && this.isValidCodingScheme(codingScheme)){
			return this.transform(codingScheme);
		} else {
			return null;
		}
	}
	
	/**
	 * Allow subclasses to validate the CodingScheme before transforming.
	 *
	 * @param codingScheme the coding scheme
	 * @return true, if is valid coding scheme
	 */
	protected boolean isValidCodingScheme(CodingScheme codingScheme){
		return true;
	}
	
	/**
	 * Gets the code system by version id or tag.
	 *
	 * @param codeSystem the code system
	 * @param versionIdOrTag the version id or tag
	 * @return the code system by version id or tag
	 */
	protected T getByVersionIdOrTag(
			NameVersionPair namePair){
		if(namePair == null){
			return null;
		}

		CodingScheme codingScheme = this.resolve(namePair.getName(), 
			Constructors.createCodingSchemeVersionOrTagFromVersion(namePair.getVersion()));

		if(codingScheme != null && this.isValidCodingScheme(codingScheme)){
			return this.transform(codingScheme);
		} else {
			return null;
		}
	}
	
	protected CodingScheme resolve(String nameOrUri, CodingSchemeVersionOrTag versionIdOrTag){
		CodingScheme codingScheme;
		
		nameOrUri = this.codingSchemeNameTranslator.translateToLexGrid(nameOrUri);
		try {
			codingScheme = this.getLexBigService().resolveCodingScheme(nameOrUri, versionIdOrTag);
		} catch (LBException e) {
			//this could be just that LexEVS didn't find it. If so, return null.
			log.warn(e);
			return null;
		}
		
		return codingScheme;
	}

	public NameVersionPair getNamePair(
			VersionNameConverter nameConverter, 
			NameOrURI cts2NameOrURI,
			ResolvedReadContext cts2ReadContext) {
		if(cts2NameOrURI == null){
			return null;
		}

		NameVersionPair namePair;
		
		if (cts2NameOrURI.getName() != null) {
			String cts2Name = cts2NameOrURI.getName();
			if (!nameConverter.isValidVersionName(cts2Name)) {
				namePair = null;
			}
			else{
				namePair = nameConverter.fromCts2VersionName(cts2Name);		
			}
		} else {
			String fullUri = cts2NameOrURI.getUri();
			namePair = this.codeSystemVersionUriResolver.resolveUri(fullUri);
		}

		return namePair;
	}
}
