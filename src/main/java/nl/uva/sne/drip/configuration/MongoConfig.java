/*
 * Copyright 2017 S. Koulouzis, Wang Junchao, Huan Zhou, Yang Hu 
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
package nl.uva.sne.drip.configuration;

import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 *
 * @author S. Koulouzis
 */
@Configuration
@EnableMongoRepositories(basePackages = "nl.uva.sne.drip.api")
@PropertySources({
    @PropertySource(value = "classpath:application.properties", ignoreResourceNotFound = true)
})
@ComponentScan(basePackages = {"nl.uva.sne.drip", "nl.uva.sne.drip.api", "nl.uva.sne.drip.configuration", "nl.uva.sne.drip.dao", "nl.uva.sne.drip.model", "nl.uva.sne.drip.service"})
public class MongoConfig extends AbstractMongoConfiguration {

    @Value("${db.name}")
    private String dbName;
    @Value("${db.host}")
    private String dbHost;
    @Value("${db.username}")
    private String dbUsername;
    @Value("${db.password}")
    private String dbPass;

//    @Autowired
//    private MongoDbFactory mongoFactory;
//    @Autowired
//    private MongoMappingContext mongoMappingContext;
    @Override
    protected String getDatabaseName() {
        return dbName;
    }

    @Override
    protected String getMappingBasePackage() {
        return "nl.uva.sne.drip";
    }

    @Override
    public MongoClient mongoClient() {
        return new MongoClient(dbHost, 27017);
    }
}
