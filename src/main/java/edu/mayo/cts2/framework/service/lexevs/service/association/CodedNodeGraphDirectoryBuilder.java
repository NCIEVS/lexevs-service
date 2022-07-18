/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.service.lexevs.service.association;

import edu.mayo.cts2.framework.filter.directory.AbstractStateBuildingDirectoryBuilder;
import edu.mayo.cts2.framework.filter.match.StateAdjustingComponentReference;
import edu.mayo.cts2.framework.model.association.AssociationDirectoryEntry;
import edu.mayo.cts2.framework.model.core.MatchAlgorithmReference;
import edu.mayo.cts2.framework.service.restriction.AssociationQueryServiceRestrictions;
import java.util.Set;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeGraph;
import org.LexGrid.LexBIG.Utility.Constructors;

public class CodedNodeGraphDirectoryBuilder 
	extends 
	AbstractStateBuildingDirectoryBuilder<CodedNodeGraph,AssociationDirectoryEntry>{

	public CodedNodeGraphDirectoryBuilder(
			CodedNodeGraph initialState,
			Callback<CodedNodeGraph, AssociationDirectoryEntry> callback,
			Set<MatchAlgorithmReference> matchAlgorithmReferences,
			Set<StateAdjustingComponentReference<CodedNodeGraph>> stateAdjustingPropertyReferences) {
		super(initialState, 
				callback, 
				matchAlgorithmReferences,
				stateAdjustingPropertyReferences);
	}
	
	public CodedNodeGraphDirectoryBuilder restrict(AssociationQueryServiceRestrictions restrictions){
		if(restrictions != null && 
				restrictions.getPredicate() != null &&
				restrictions.getPredicate().getEntityName() != null &&
				restrictions.getPredicate().getEntityName().getName() != null){
			String predicateName = restrictions.getPredicate().getEntityName().getName();
			
			try {
				this.updateState(
						this.getState().restrictToAssociations(Constructors.createNameAndValueList(predicateName), null));
			} catch (LBException e) {
				throw new RuntimeException(e);
			}
		}
		
		return this;
	}

}
