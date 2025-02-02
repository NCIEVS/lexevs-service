/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/

package edu.mayo.cts2.framework.service.lexevs.service.entity;

import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.command.ResolvedReadContext;
import edu.mayo.cts2.framework.model.core.ComponentReference;
import edu.mayo.cts2.framework.model.core.EntityReferenceList;
import edu.mayo.cts2.framework.model.core.MatchAlgorithmReference;
import edu.mayo.cts2.framework.model.core.PredicateReference;
import edu.mayo.cts2.framework.model.core.ScopedEntityName;
import edu.mayo.cts2.framework.model.core.SortCriteria;
import edu.mayo.cts2.framework.model.core.VersionTagReference;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.model.entity.EntityDirectoryEntry;
import edu.mayo.cts2.framework.model.entity.EntityListEntry;
import edu.mayo.cts2.framework.model.service.core.DocumentedNamespaceReference;
import edu.mayo.cts2.framework.model.service.core.EntityNameOrURI;
import edu.mayo.cts2.framework.model.service.core.EntityNameOrURIList;
import edu.mayo.cts2.framework.service.lexevs.naming.VersionNameConverter;
import edu.mayo.cts2.framework.service.lexevs.service.AbstractLexEvsService;
import edu.mayo.cts2.framework.service.lexevs.service.entity.DelegatingEntityQueryService.QueryType;
import edu.mayo.cts2.framework.service.lexevs.utility.CommonPageUtils;
import edu.mayo.cts2.framework.service.lexevs.utility.CommonResourceUtils;
import edu.mayo.cts2.framework.service.lexevs.utility.CommonSearchFilterUtils;
import edu.mayo.cts2.framework.service.lexevs.utility.CommonUtils;
import edu.mayo.cts2.framework.service.lexevs.utility.Constants;
import edu.mayo.cts2.framework.service.lexevs.utility.QueryData;
import edu.mayo.cts2.framework.service.lexevs.utility.ResolvedConceptReferenceResults;
import edu.mayo.cts2.framework.service.profile.entitydescription.EntityDescriptionQuery;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Resource;
import org.LexGrid.LexBIG.DataModel.Core.ConceptReference;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Exceptions.LBInvocationException;
import org.LexGrid.LexBIG.Exceptions.LBParameterException;
import org.LexGrid.LexBIG.Exceptions.LBResourceUnavailableException;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;
import org.lexgrid.resolvedvalueset.LexEVSResolvedValueSetService;
import org.lexgrid.valuesets.LexEVSValueSetDefinitionServices;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 *  @author <a href="mailto:frutiger.kim@mayo.edu">Kim Frutiger</a>
 *  @author <a href="mailto:hardie.linda@mayo.edu">Linda Hardie</a>
 *
*/
@Primary
@Component
public class LexEvsEntityQueryService extends AbstractLexEvsService 
		implements DelegateEntityQueryService {

	@Resource
	private VersionNameConverter nameConverter;
	
	@Resource
	private EntityTransform transformer;
			
	// ------ Local methods ----------------------
	public void setCodeSystemVersionNameConverter(
			VersionNameConverter versionNameConverter) {
		this.nameConverter = versionNameConverter;
	}
	
	public void setEntityTransformer(
			EntityTransform entityTransform) {
		this.transformer = entityTransform;
	}
	

	// -------- Implemented methods ----------------
	
	public int count(EntityDescriptionQuery query) {
		int count = 0;
		
		LexBIGService lexBigService = this.getLexBigService();
		
		LexEVSValueSetDefinitionServices vsDefinitionServices = getLexEVSValueSetDefinitionServices();
		LexEVSResolvedValueSetService resolvedVSService = getLexEVSResolvedService();
		QueryData<EntityDescriptionQuery> queryData = new QueryData<EntityDescriptionQuery>(query, nameConverter);
		
		CodedNodeSet codedNodeSet;
		codedNodeSet = CommonResourceUtils.getLexCodedNodeSet(lexBigService, vsDefinitionServices, 
				resolvedVSService, queryData, null, null);
		
		if(codedNodeSet != null){
			ResolvedConceptReferencesIterator iterator = CommonUtils.getLexResolvedConceptIterator(codedNodeSet, null);
			if(iterator != null){
				try {
					count = iterator.numberRemaining();
				} catch (LBResourceUnavailableException e) {
					throw new RuntimeException(e);
				} finally {
					if(iterator != null) {
						try {
							iterator.release();
						}catch (LBException e) {
							throw new RuntimeException(e);
						}
					}
				}
			}
		}
		
		return count;
	}

	
	public boolean isEntityInSet(EntityNameOrURI nameOrUri,
			EntityDescriptionQuery query, ResolvedReadContext readContext) {
		SortCriteria sortCriteria = null;
		
		LexBIGService lexBigService = this.getLexBigService();
		LexEVSResolvedValueSetService resolvedVSService = getLexEVSResolvedService();
		LexEVSValueSetDefinitionServices vsDefinitionServices = getLexEVSValueSetDefinitionServices();
		
		QueryData<EntityDescriptionQuery> queryData = new QueryData<EntityDescriptionQuery>(query, nameConverter);
		
		CodedNodeSet codedNodeSet = CommonResourceUtils.getLexCodedNodeSet(lexBigService, vsDefinitionServices, 
				resolvedVSService, queryData, sortCriteria, null);
		
		// TODO: CodedNodeSet still needs to be filtered by restrictions:
		// restrictions.getEntities  <<--- completed in getCodedNodeSet method
		// restrictions.getTaggedCodeSystem -- *** unclear how to handle this restriction ****
		
		ScopedEntityName entityName = nameOrUri.getEntityName();
		String uri = nameOrUri.getUri();
		
		ConceptReference cts2Code = new ConceptReference();
		try {
			if(entityName != null){
				String code = entityName.getName();
				String nameSpace = entityName.getNamespace();
				
				cts2Code.setCode(code);
				cts2Code.setCodeNamespace(nameSpace);
				return codedNodeSet.isCodeInSet(cts2Code);
			}
			else if(uri != null){
				// TODO need to lookup in LexEVS, turn uri into name LexEvs can understand then update cts2Code object
				return codedNodeSet.isCodeInSet(cts2Code);
			}
		
		} catch (LBInvocationException e) {
			throw new RuntimeException(e);
		} catch (LBParameterException e) {
			throw new RuntimeException(e);
		}
		
		return false;
	}

	
	public DirectoryResult<EntityListEntry> getResourceList(
			EntityDescriptionQuery query, SortCriteria sortCriteria, Page page) {
		
		LexBIGService lexBigService = this.getLexBigService();
		LexEVSResolvedValueSetService resolvedVSService = getLexEVSResolvedService();
		LexEVSValueSetDefinitionServices vsDefinitionServices = getLexEVSValueSetDefinitionServices();
		
		QueryData<EntityDescriptionQuery> queryData = new QueryData<EntityDescriptionQuery>(query, nameConverter);
		
		ResolvedConceptReferenceResults resolvedConceptReferenceResultsPage;
		resolvedConceptReferenceResultsPage = CommonPageUtils.getPage(lexBigService, vsDefinitionServices, 
				resolvedVSService, queryData, sortCriteria, page, null);

		DirectoryResult<EntityListEntry> directoryResult;
		directoryResult = CommonResourceUtils.createDirectoryResults(this.transformer, resolvedConceptReferenceResultsPage, Constants.FULL_DESCRIPTION);

		return directoryResult;
	}

	
	public DirectoryResult<EntityDirectoryEntry> getResourceSummaries(EntityDescriptionQuery query, SortCriteria sortCriteria, Page page) {	
		
		return getResourceSummaries(query, sortCriteria, page, null);
	}
	
	
	public DirectoryResult<EntityDirectoryEntry> getResourceSummaries(EntityDescriptionQuery query, 
			SortCriteria sortCriteria, Page page, String uri) {	
		
		LexBIGService lexBigService = this.getLexBigService();
		LexEVSResolvedValueSetService resolvedVSService = getLexEVSResolvedService();
		LexEVSValueSetDefinitionServices vsDefinitionServices = getLexEVSValueSetDefinitionServices();
		//ValueSetNameTranslator valueSetNameTranslator = getValueSetNameTranslator();
		
		QueryData<EntityDescriptionQuery> queryData = new QueryData<EntityDescriptionQuery>(query, nameConverter);
		
		ResolvedConceptReferenceResults resolvedConceptReferenceResults;
//		resolvedConceptReferenceResults = CommonPageUtils.getPage(lexBigService, vsDefinitionServices, 
//				resolvedVSService, valueSetNameTranslator, queryData, sortCriteria, page, identifier);
		
		resolvedConceptReferenceResults = CommonPageUtils.getPage(lexBigService, vsDefinitionServices, 
				resolvedVSService, queryData, sortCriteria, page, uri);
		
		DirectoryResult<EntityDirectoryEntry> directoryResult;
		directoryResult = CommonResourceUtils.createDirectoryResults(this.transformer, resolvedConceptReferenceResults, Constants.SUMMARY_DESCRIPTION);
			
		return directoryResult;
	}
	
	
	public Set<? extends MatchAlgorithmReference> getSupportedMatchAlgorithms() {
		return CommonSearchFilterUtils.getLexSupportedMatchAlgorithms();
	}
	
	
	public Set<? extends ComponentReference> getSupportedSearchReferences() {
		return CommonSearchFilterUtils.getLexSupportedSearchReferences();
	}

	
	public Set<VersionTagReference> getSupportedTags() {
		return new HashSet<VersionTagReference>(Arrays.asList(Constants.CURRENT_TAG));
	}

	
	// Not going to implement following methods
	// ----------------------------------------
	
	public EntityReferenceList resolveAsEntityReferenceList(
			EntityDescriptionQuery arg0, ResolvedReadContext arg1) {
		throw new UnsupportedOperationException();
	}
	
	
	public EntityNameOrURIList intersectEntityList(
			Set<EntityNameOrURI> entities,
			EntityDescriptionQuery restrictions, 
			ResolvedReadContext readContext) {
		throw new UnsupportedOperationException();
	}

	// Methods returning empty lists or sets
	// -------------------------------------
	
	public Set<? extends ComponentReference> getSupportedSortReferences() {
		return new HashSet<ComponentReference>();
	}

	
	public Set<PredicateReference> getKnownProperties() {
		return new HashSet<PredicateReference>();
	}

	
	public List<DocumentedNamespaceReference> getKnownNamespaceList() {
		return new ArrayList<DocumentedNamespaceReference>();
	}

	
	public boolean canHandle(EntityDescriptionQuery query, QueryType queryType) {
		//this can handle all QueryTypes, so we don't check that.
		return query.getEntitiesFromAssociationsQuery() == null &&
				query.getRestrictions().getHierarchyRestriction() == null &&
				query.getRestrictions().getCodeSystemVersions().size() == 1;
	}

	
	public int getOrder() {
		return 1;
	}
	
}

