package my.e345.ipcounter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class IpCounterApplication {

    private static final Logger LOG = LoggerFactory.getLogger(IpCounterApplication.class);

    public static void main(String[] args) {
        LOG.info("start main()");
        SpringApplication.run(IpCounterApplication.class, args);
    }
}
