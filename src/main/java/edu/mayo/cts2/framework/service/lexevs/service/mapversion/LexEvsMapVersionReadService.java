/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.service.lexevs.service.mapversion;

import edu.mayo.cts2.framework.model.command.ResolvedReadContext;
import edu.mayo.cts2.framework.model.core.VersionTagReference;
import edu.mayo.cts2.framework.model.mapversion.MapVersion;
import edu.mayo.cts2.framework.model.mapversion.MapVersionListEntry;
import edu.mayo.cts2.framework.model.service.core.DocumentedNamespaceReference;
import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import edu.mayo.cts2.framework.model.util.ModelUtils;
import edu.mayo.cts2.framework.service.lexevs.naming.NameVersionPair;
import edu.mayo.cts2.framework.service.lexevs.naming.VersionNameConverter;
import edu.mayo.cts2.framework.service.lexevs.service.AbstractLexEvsCodeSystemService;
import edu.mayo.cts2.framework.service.lexevs.utility.CommonMapUtils;
import edu.mayo.cts2.framework.service.lexevs.utility.CommonUtils;
import edu.mayo.cts2.framework.service.lexevs.utility.Constants;
import edu.mayo.cts2.framework.service.profile.mapversion.MapVersionReadService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Resource;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.codingSchemes.CodingScheme;
import org.springframework.stereotype.Component;

/**
 *  @author <a href="mailto:frutiger.kim@mayo.edu">Kim Frutiger</a>
 *  @author <a href="mailto:hardie.linda@mayo.edu">Linda Hardie</a>
 *
*/
@Component
public class LexEvsMapVersionReadService
	extends AbstractLexEvsCodeSystemService<MapVersion>
	implements MapVersionReadService{

	@Resource
	private VersionNameConverter nameConverter;
	
	@Resource
	private CodingSchemeToMapVersionTransform transformer;
	
	@Resource
	private MappingExtension mappingExtension;

	// ------ Local methods ----------------------
	
	// -------- Implemented methods ----------------	
	@Override
	protected MapVersion transform(CodingScheme codingScheme) {
		if(! CommonMapUtils.validateMappingCodingScheme(
				codingScheme.getCodingSchemeURI(), 
				codingScheme.getRepresentsVersion(),
				mappingExtension)){
			return null;
		} else {
			MapVersionListEntry listEntry = this.transformer.transformFullDescription(codingScheme);
			
			return listEntry == null ? null : listEntry.getEntry();
		}
	}
	
	@Override
	public MapVersion readByTag(
			NameOrURI parentIdentifier,
			VersionTagReference tag, 
			ResolvedReadContext readContext) {
		
		return this.getByVersionIdOrTag(parentIdentifier, 
				CommonUtils.convertTag(tag));
	}

	@Override
	public boolean existsByTag(
			NameOrURI parentIdentifier,
			VersionTagReference tag, 
			ResolvedReadContext readContext) {
		return this.readByTag(parentIdentifier, tag, readContext) != null;
	}

	@Override
	public List<VersionTagReference> getSupportedTags() {
		return Arrays.asList(Constants.CURRENT_TAG);
	}

	@Override
	public MapVersion read(NameOrURI identifier, ResolvedReadContext readContext) {
		
		String name;
		if(identifier.getName() != null){
			name = identifier.getName();
			if(!this.nameConverter.isValidVersionName(name)){
				return null;
			}
		} else {
			throw new UnsupportedOperationException("Cannot resolve by DocumentURI yet.");
		}
		
		NameVersionPair namePair = this.nameConverter.fromCts2VersionName(name);
		CodingSchemeVersionOrTag version = 
			Constructors.createCodingSchemeVersionOrTagFromVersion(namePair.getVersion());
		
		return this.getByVersionIdOrTag
				(ModelUtils.nameOrUriFromName(
						namePair.getName()), 
						version);
	}

	@Override
	public boolean exists(NameOrURI identifier, ResolvedReadContext readContext) {
		return this.read(identifier, readContext) != null;
	}

	// Methods returning empty lists or sets
	// -------------------------------------
	@Override
	public List<DocumentedNamespaceReference> getKnownNamespaceList() {
		return new ArrayList<DocumentedNamespaceReference>();
	}

}
