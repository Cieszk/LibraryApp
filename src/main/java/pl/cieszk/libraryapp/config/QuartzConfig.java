package pl.cieszk.libraryapp.config;

import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.cieszk.libraryapp.scheduler.FineCalculationJob;

@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail fineCalculationJobDetail() {
        return JobBuilder.newJob(FineCalculationJob.class)
                .withIdentity("fineCalculationJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger fineCalculationTrigger() {
        return TriggerBuilder.newTrigger()
                .forJob(fineCalculationJobDetail())
                .withIdentity("fineCalculationTrigger")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 0 * * ?"))
                .build();
    }
}
