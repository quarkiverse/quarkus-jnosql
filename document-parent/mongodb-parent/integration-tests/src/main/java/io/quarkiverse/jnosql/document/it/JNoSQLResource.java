/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
 */
package io.quarkiverse.jnosql.document.it;

import java.util.Arrays;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.nosql.document.DocumentTemplate;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/jnosql")
@ApplicationScoped
public class JNoSQLResource {

    @Inject
    private DocumentTemplate template;

    @GET
    public String hello() {
        Person person = new Person();
        person.setName(UUID.randomUUID().toString());
        person.setPhones(
                Arrays.asList(
                        UUID.randomUUID().toString(),
                        UUID.randomUUID().toString(),
                        UUID.randomUUID().toString()));
        Person insert = template.insert(person);
        return insert.getId();
    }
}
