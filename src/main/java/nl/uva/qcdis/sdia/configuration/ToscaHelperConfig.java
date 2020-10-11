/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.uva.qcdis.sdia.configuration;

import nl.uva.qcdis.sdia.commons.utils.ToscaHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

/**
 *
 * @author S. Koulouzis
 */
@Configuration
@PropertySources({
    @PropertySource(value = "classpath:application.properties", ignoreResourceNotFound = true)
})
@ComponentScan(basePackages = {"nl.uva.qcdis.sdia", "nl.uva.qcdis.sdia.api", "nl.uva.qcdis.sdia.configuration", "nl.uva.qcdis.sdia.dao", "nl.uva.qcdis.sdia.model", "nl.uva.qcdis.sdia.service", "nl.uva.qcdis.sdia.commons.utils"})
public class ToscaHelperConfig {

    @Value("${sure-tosca.base.path}")
    private String sureToscaBasePath;

    @Bean
    public ToscaHelper toscaHelper() {
        return new ToscaHelper(sureToscaBasePath);
    }

}
