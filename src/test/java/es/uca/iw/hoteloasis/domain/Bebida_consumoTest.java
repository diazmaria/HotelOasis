package es.uca.iw.hoteloasis.domain;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.mock.staticmock.MockStaticEntityMethods;

@RunWith(JUnit4.class)
@MockStaticEntityMethods
public class Bebida_consumoTest {

    @Test
    public void testMethod() {
        int expectedCount = 13;
        Bebida_consumo.countBebida_consumoes();
        org.springframework.mock.staticmock.AnnotationDrivenStaticEntityMockingControl.expectReturn(expectedCount);
        org.springframework.mock.staticmock.AnnotationDrivenStaticEntityMockingControl.playback();
        org.junit.Assert.assertEquals(expectedCount, Bebida_consumo.countBebida_consumoes());
    }
}