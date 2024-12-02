package pl.cieszk.libraryapp.core.scheduler;

import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
