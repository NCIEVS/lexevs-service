/*
* Copyright: (c) 2004-2013 Mayo Foundation for Medical Education and
* Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
* triple-shield Mayo logo are trademarks and service marks of MFMER.
*
* Except as contained in the copyright notice above, or as used to identify
* MFMER as the author of this software, the trade names, trademarks, service
* marks, or product names of the copyright holder shall not be used in
* advertising, promotion or otherwise in connection with this software without
* prior written authorization of the copyright holder.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package edu.mayo.cts2.framework.plugin.service.lexevs.utility;

import java.util.List;

import org.LexGrid.LexBIG.DataModel.Collections.ResolvedConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.Exceptions.LBInvocationException;
import org.LexGrid.LexBIG.Exceptions.LBParameterException;
import org.LexGrid.LexBIG.Exceptions.LBResourceUnavailableException;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;

/**
 *  @author <a href="mailto:frutiger.kim@mayo.edu">Kim Frutiger</a>
 *  @author <a href="mailto:hardie.linda@mayo.edu">Linda Hardie</a>
 *
 */
public class FakeResolvedConceptReferencesIteratorImpl implements ResolvedConceptReferencesIterator {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int position = 0;
	List<FakeCodedNode> codedNodeList = null;
	
	public FakeResolvedConceptReferencesIteratorImpl(List<FakeCodedNode> nodeList){
		this.codedNodeList = nodeList;
	}
	
	@Override
	public void release() throws LBResourceUnavailableException {
	}
	
	@Override
	public int numberRemaining() throws LBResourceUnavailableException {
		if(codedNodeList != null){
			return codedNodeList.size() - position;
		}
		return 0;
	}
	
	@Override
	public boolean hasNext() throws LBResourceUnavailableException {
		
		return ((this.codedNodeList.size() - position) > 0);
	}
	
	@Override
	public ResolvedConceptReferencesIterator scroll(int maxToReturn)
			throws LBResourceUnavailableException, LBInvocationException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public ResolvedConceptReferenceList next(int maxToReturn)
			throws LBResourceUnavailableException, LBInvocationException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public ResolvedConceptReference next()
			throws LBResourceUnavailableException, LBInvocationException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public ResolvedConceptReferenceList getNext() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public ResolvedConceptReferenceList get(int start, int end)
			throws LBResourceUnavailableException, LBInvocationException,
			LBParameterException {
		
		ResolvedConceptReferenceList list = new ResolvedConceptReferenceList();
		if(start > 0 && start <= this.codedNodeList.size()){
			if(end > 0 && end <= this.codedNodeList.size()){
				for(int i=start; i < end; i++){	
					ResolvedConceptReference resolvedConceptReference = new ResolvedConceptReference();							
					String codingSchemeName = this.codedNodeList.get(i).getCodingScheme();
					String codingSchemeVersion = this.codedNodeList.get(i).getVersion();
					
					resolvedConceptReference.setCodingSchemeName(codingSchemeName);
					resolvedConceptReference.setCodingSchemeVersion(codingSchemeVersion);
					list.addResolvedConceptReference(resolvedConceptReference );
				}
			}
		}
		
		return list;
	}
}

