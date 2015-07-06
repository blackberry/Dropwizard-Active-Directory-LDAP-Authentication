/** Copyright 2015 BlackBerry, Limited.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License. 
 */

package com.blackberry.bdp.dwauth.ldap;

import java.util.Set;

public class User {

    private final String name;
    private final Set<String> memberships;

    public User(String name, Set<String> memberships) {
        this.name = name;
        this.memberships = memberships;
    }

    public String getName() {
        return name;
    }

    public Set<String> getMemberships() {
        return memberships;
    }
}