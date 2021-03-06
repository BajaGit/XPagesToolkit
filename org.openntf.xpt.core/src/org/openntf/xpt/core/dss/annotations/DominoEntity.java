/*
 * � Copyright WebGate Consulting AG, 2012
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing 
 * permissions and limitations under the License.
 */
package org.openntf.xpt.core.dss.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)

public @interface DominoEntity {
	String FieldName();
	boolean readOnly() default false;
	boolean writeOnly() default false;
	boolean isFormula() default false;
	boolean isNames() default false;
	boolean isAuthor() default false;
	boolean isReader() default false;
	String showNameAs() default ""; //CN and ABBREVIATE are implemented in NamesProcessor.java
	boolean dateOnly() default false;
	boolean changeLog() default false;
	boolean encrypt() default false;
	String[] encRoles() default {};
	boolean embedded() default false;
}
