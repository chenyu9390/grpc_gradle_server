package net.gichain.rechargeChannel;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * recharge_interface
 * 2020/6/13 15:27
 * 启动类
 *
 * @author ck
 * @since
 **/
@ComponentScan({"net.pay","net.gichain.rechargeChannel"})
@SpringBootApplication
@EnableScheduling
public class RechargeChannelApplication implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(RechargeChannelApplication.class);
        springApplication.run(args);
    }

    /**
     * Callback used to run the bean.
     *
     * @param args incoming main method arguments
     * @throws Exception on error
     */
    @Override
    public void run(String... args) throws Exception {

    }
}
