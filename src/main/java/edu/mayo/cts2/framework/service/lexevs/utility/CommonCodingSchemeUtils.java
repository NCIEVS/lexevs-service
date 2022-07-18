/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.service.lexevs.utility;

import edu.mayo.cts2.framework.model.service.core.NameOrURI;
import java.util.Iterator;
import java.util.Set;
import org.LexGrid.codingSchemes.CodingScheme;
import org.LexGrid.relations.Relations;

public final class CommonCodingSchemeUtils {

	private CommonCodingSchemeUtils(){
		super();
	}

	public static boolean lexCodingSchemeExistsInCts2CodeSystemSet(String codeSystemNameOne, String codeSystemNameTwo, Set<NameOrURI> cts2CodeSystemSet) {

		boolean codeSystemFound = false;
		Iterator<NameOrURI> cts2CodeSystemIterator = cts2CodeSystemSet.iterator();
		while (cts2CodeSystemIterator.hasNext() && codeSystemFound == false) {
			NameOrURI cts2CodeSystem = cts2CodeSystemIterator.next();
			
			if(CommonStringUtils.compareStrings(cts2CodeSystem.getName(), codeSystemNameOne, codeSystemNameTwo)){
				codeSystemFound = true;
			}
			else if(CommonStringUtils.compareStrings(cts2CodeSystem.getUri(), codeSystemNameOne, codeSystemNameTwo)){
				codeSystemFound = true;
			}
			
		}
		return codeSystemFound;
	}
	
	public static boolean checkIfCts2MapExists(
			CodingScheme lexCodingScheme, 
			Set<NameOrURI> cts2RestrictedCodeSystemSet, 
			String cts2MapRoleValue) {

		boolean codingSchemeExistsInSet = false;

		// Assuming format of Map has only has 1 relations section/1 relations element in xml file
		if (lexCodingScheme.getRelationsCount() != 1) {
			throw new UnsupportedOperationException("Invalid format for Map. Expecting only one metadata section for Relations.");
		}
		Relations lexRelations = lexCodingScheme.getRelations(0);
		String lexSourceCodingScheme = lexRelations.getSourceCodingScheme();
		String lexTargetCodingScheme = lexRelations.getTargetCodingScheme();
		
		if (cts2MapRoleValue.equals(Constants.MAP_TO_ROLE) && CommonCodingSchemeUtils.lexCodingSchemeExistsInCts2CodeSystemSet(lexTargetCodingScheme, null, cts2RestrictedCodeSystemSet)) {
			codingSchemeExistsInSet = true;
		}
		
		if (cts2MapRoleValue.equals(Constants.MAP_FROM_ROLE) && CommonCodingSchemeUtils.lexCodingSchemeExistsInCts2CodeSystemSet(lexSourceCodingScheme, null, cts2RestrictedCodeSystemSet)) { 
			codingSchemeExistsInSet = true;
		}
		
		if (cts2MapRoleValue.equals(Constants.BOTH_MAP_ROLES) && 
				CommonCodingSchemeUtils.lexCodingSchemeExistsInCts2CodeSystemSet(lexTargetCodingScheme, lexSourceCodingScheme, cts2RestrictedCodeSystemSet)) {
			codingSchemeExistsInSet = true;
		}
		
		return codingSchemeExistsInSet;
	}


}
