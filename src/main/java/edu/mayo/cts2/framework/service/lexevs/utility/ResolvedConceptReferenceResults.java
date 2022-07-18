/*
* Copyright: (c) Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.com/lexevs-service/LICENSE.txt for details.
*/
package edu.mayo.cts2.framework.service.lexevs.utility;

import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;

public class ResolvedConceptReferenceResults {
	private boolean atEnd;
	private ResolvedConceptReference [] lexResolvedConceptReference;
	
	public ResolvedConceptReferenceResults(ResolvedConceptReference [] lexResolvedConceptReference, boolean atEnd){
		if(lexResolvedConceptReference != null){
			int length = lexResolvedConceptReference.length;
			this.lexResolvedConceptReference = new ResolvedConceptReference[length];
			for(int i=0; i < length; i++){
				// Shallow copy ok?
				this.lexResolvedConceptReference[i] = lexResolvedConceptReference[i];
			}
		}
		else{
			this.lexResolvedConceptReference = new ResolvedConceptReference[0];
		}
		this.atEnd = atEnd;
	}

	public boolean isAtEnd() {
		return atEnd;
	}

	public void setAtEnd(boolean atEnd) {
		this.atEnd = atEnd;
	}

	public ResolvedConceptReference[] getLexResolvedConceptReference() {
		return lexResolvedConceptReference;
	}

	public void setLexResolvedConceptReference(
			ResolvedConceptReference[] resolvedConceptReference) {
		this.lexResolvedConceptReference = resolvedConceptReference.clone();
	}
	
}
	
