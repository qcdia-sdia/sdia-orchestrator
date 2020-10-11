/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.uva.qcdis.sdia.service;

import java.util.ArrayList;
import java.util.List;
import static nl.uva.qcdis.sdia.commons.utils.Constants.CLOUD_STORM_INTERFACE;
import org.springframework.stereotype.Service;
import nl.uva.qcdis.sdia.model.Provisioner;

/**
 *
 * @author S. Koulouzis
 */
@Service
public class ProvisionerService {

    public List<Provisioner> findAll() {
        List<Provisioner> all = new ArrayList<>();
        Provisioner provisioner = new Provisioner();
        provisioner.setName("CloudsStorm");
        provisioner.setDescription("Interface for VM topology management with CloudsStorm. More at https://cloudsstorm.github.io/");
        provisioner.setToscaInterfaceType(CLOUD_STORM_INTERFACE);
        all.add(provisioner);
        return all;
    }

}
