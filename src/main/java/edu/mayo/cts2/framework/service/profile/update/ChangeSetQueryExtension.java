/*
 * Copyright: (c) 2004-2012 Mayo Foundation for Medical Education and 
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
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.mayo.cts2.framework.service.profile.update;

import edu.mayo.cts2.framework.model.command.Page;
import edu.mayo.cts2.framework.model.command.ResolvedFilter;
import edu.mayo.cts2.framework.model.core.SortCriteria;
import edu.mayo.cts2.framework.model.directory.DirectoryResult;
import edu.mayo.cts2.framework.model.service.core.Query;
import edu.mayo.cts2.framework.model.updates.ChangeSetDirectoryEntry;
import edu.mayo.cts2.framework.service.restriction.ChangeSetQueryExtensionRestrictions;
import edu.mayo.cts2.framework.service.profile.BaseQueryService;
import edu.mayo.cts2.framework.service.profile.Cts2Profile;
import java.util.Set;

/**
 * The Interface ChangeSetQueryExtension.
 *
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
public interface ChangeSetQueryExtension extends BaseQueryService, Cts2Profile {

	/**
	 * Gets the resource summaries.
	 *
	 * @param changeSetQuery the change set query
	 * @param sort the sort
	 * @param page the page
	 * @return the resource summaries
	 */
	public DirectoryResult<ChangeSetDirectoryEntry> getResourceSummaries(
			ChangeSetQuery changeSetQuery,
			SortCriteria sort,
			Page page);

	/**
	 * Count.
	 *
	 * @param query the query
	 * @param filterComponent the filter component
	 * @param restrictions the restrictions
	 * @return the int
	 */
	public int count(
			Query query,
			Set<ResolvedFilter> filterComponent,
			ChangeSetQueryExtensionRestrictions restrictions);
}
