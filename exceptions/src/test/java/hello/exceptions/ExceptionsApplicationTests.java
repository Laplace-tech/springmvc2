package hello.exceptions;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
class ExceptionsApplicationTests {

	@Autowired
	private ApplicationContext applicationContext;

	@Test
    void printMyBeans() {
        String[] beanNames = applicationContext.getBeanDefinitionNames();

        System.out.println("=== My Project Beans ===");
        for (String beanName : beanNames) {
            Object bean = applicationContext.getBean(beanName);
            String packageName = bean.getClass().getPackageName();

            // 내 프로젝트 최상위 패키지명으로 필터 (예: hello.exceptions)
            if (packageName.startsWith("hello.exceptions")) {
                System.out.println(beanName + " : " + bean.getClass().getName());
            }
        }
        System.out.println("=========================");
    }


}
