// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package es.uca.iw.hoteloasis.domain;

import es.uca.iw.hoteloasis.domain.Tarifa;
import es.uca.iw.hoteloasis.domain.TarifaDataOnDemand;
import es.uca.iw.hoteloasis.domain.TarifaIntegrationTest;
import java.util.Iterator;
import java.util.List;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

privileged aspect TarifaIntegrationTest_Roo_IntegrationTest {
    
    declare @type: TarifaIntegrationTest: @RunWith(SpringJUnit4ClassRunner.class);
    
    declare @type: TarifaIntegrationTest: @ContextConfiguration(locations = "classpath*:/META-INF/spring/applicationContext*.xml");
    
    declare @type: TarifaIntegrationTest: @Transactional;
    
    @Autowired
    TarifaDataOnDemand TarifaIntegrationTest.dod;
    
    @Test
    public void TarifaIntegrationTest.testCountTarifas() {
        Assert.assertNotNull("Data on demand for 'Tarifa' failed to initialize correctly", dod.getRandomTarifa());
        long count = Tarifa.countTarifas();
        Assert.assertTrue("Counter for 'Tarifa' incorrectly reported there were no entries", count > 0);
    }
    
    @Test
    public void TarifaIntegrationTest.testFindTarifa() {
        Tarifa obj = dod.getRandomTarifa();
        Assert.assertNotNull("Data on demand for 'Tarifa' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'Tarifa' failed to provide an identifier", id);
        obj = Tarifa.findTarifa(id);
        Assert.assertNotNull("Find method for 'Tarifa' illegally returned null for id '" + id + "'", obj);
        Assert.assertEquals("Find method for 'Tarifa' returned the incorrect identifier", id, obj.getId());
    }
    
    @Test
    public void TarifaIntegrationTest.testFindAllTarifas() {
        Assert.assertNotNull("Data on demand for 'Tarifa' failed to initialize correctly", dod.getRandomTarifa());
        long count = Tarifa.countTarifas();
        Assert.assertTrue("Too expensive to perform a find all test for 'Tarifa', as there are " + count + " entries; set the findAllMaximum to exceed this value or set findAll=false on the integration test annotation to disable the test", count < 250);
        List<Tarifa> result = Tarifa.findAllTarifas();
        Assert.assertNotNull("Find all method for 'Tarifa' illegally returned null", result);
        Assert.assertTrue("Find all method for 'Tarifa' failed to return any data", result.size() > 0);
    }
    
    @Test
    public void TarifaIntegrationTest.testFindTarifaEntries() {
        Assert.assertNotNull("Data on demand for 'Tarifa' failed to initialize correctly", dod.getRandomTarifa());
        long count = Tarifa.countTarifas();
        if (count > 20) count = 20;
        int firstResult = 0;
        int maxResults = (int) count;
        List<Tarifa> result = Tarifa.findTarifaEntries(firstResult, maxResults);
        Assert.assertNotNull("Find entries method for 'Tarifa' illegally returned null", result);
        Assert.assertEquals("Find entries method for 'Tarifa' returned an incorrect number of entries", count, result.size());
    }
    
    @Test
    public void TarifaIntegrationTest.testFlush() {
        Tarifa obj = dod.getRandomTarifa();
        Assert.assertNotNull("Data on demand for 'Tarifa' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'Tarifa' failed to provide an identifier", id);
        obj = Tarifa.findTarifa(id);
        Assert.assertNotNull("Find method for 'Tarifa' illegally returned null for id '" + id + "'", obj);
        boolean modified =  dod.modifyTarifa(obj);
        Integer currentVersion = obj.getVersion();
        obj.flush();
        Assert.assertTrue("Version for 'Tarifa' failed to increment on flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }
    
    @Test
    public void TarifaIntegrationTest.testMergeUpdate() {
        Tarifa obj = dod.getRandomTarifa();
        Assert.assertNotNull("Data on demand for 'Tarifa' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'Tarifa' failed to provide an identifier", id);
        obj = Tarifa.findTarifa(id);
        boolean modified =  dod.modifyTarifa(obj);
        Integer currentVersion = obj.getVersion();
        Tarifa merged = obj.merge();
        obj.flush();
        Assert.assertEquals("Identifier of merged object not the same as identifier of original object", merged.getId(), id);
        Assert.assertTrue("Version for 'Tarifa' failed to increment on merge and flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }
    
    @Test
    public void TarifaIntegrationTest.testPersist() {
        Assert.assertNotNull("Data on demand for 'Tarifa' failed to initialize correctly", dod.getRandomTarifa());
        Tarifa obj = dod.getNewTransientTarifa(Integer.MAX_VALUE);
        Assert.assertNotNull("Data on demand for 'Tarifa' failed to provide a new transient entity", obj);
        Assert.assertNull("Expected 'Tarifa' identifier to be null", obj.getId());
        try {
            obj.persist();
        } catch (final ConstraintViolationException e) {
            final StringBuilder msg = new StringBuilder();
            for (Iterator<ConstraintViolation<?>> iter = e.getConstraintViolations().iterator(); iter.hasNext();) {
                final ConstraintViolation<?> cv = iter.next();
                msg.append("[").append(cv.getRootBean().getClass().getName()).append(".").append(cv.getPropertyPath()).append(": ").append(cv.getMessage()).append(" (invalid value = ").append(cv.getInvalidValue()).append(")").append("]");
            }
            throw new IllegalStateException(msg.toString(), e);
        }
        obj.flush();
        Assert.assertNotNull("Expected 'Tarifa' identifier to no longer be null", obj.getId());
    }
    
    @Test
    public void TarifaIntegrationTest.testRemove() {
        Tarifa obj = dod.getRandomTarifa();
        Assert.assertNotNull("Data on demand for 'Tarifa' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'Tarifa' failed to provide an identifier", id);
        obj = Tarifa.findTarifa(id);
        obj.remove();
        obj.flush();
        Assert.assertNull("Failed to remove 'Tarifa' with identifier '" + id + "'", Tarifa.findTarifa(id));
    }
    
}
