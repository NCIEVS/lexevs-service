/*
 * Copyright: (c) 2004-2011 Mayo Foundation for Medical Education and 
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
package edu.mayo.cts2.framework.service.util;


import edu.mayo.cts2.framework.service.core.url.UrlConstructor;
import edu.mayo.cts2.framework.service.core.util.ConfigManager;
import javax.annotation.Resource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.FactoryBean;

/**
 * A factory for creating UrlConstructor objects.
 *
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
public class UrlConstructorSpringFactory implements FactoryBean<UrlConstructor> {
	
	protected Log log = LogFactory.getLog(getClass().getName());
	
	@Resource
	private ConfigManager pluginConfigManager;
	
	public UrlConstructor getObject() throws Exception {
		return new UrlConstructor(this.pluginConfigManager.getServerContext());
	}

	public Class<?> getObjectType() {
		return UrlConstructor.class;
	}

	public boolean isSingleton() {
		return true;
	}
}
