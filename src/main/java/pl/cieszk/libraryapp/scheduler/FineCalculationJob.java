package pl.cieszk.libraryapp.scheduler;

import lombok.AllArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;
import pl.cieszk.libraryapp.loans.service.FineService;

@Component
@AllArgsConstructor
public class FineCalculationJob implements Job {

    private final FineService fineService;
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        fineService.calculateDailyFines();
    }
}
