package com.fullteaching.e2e.no_elastest.functional.test;

import giis.retorch.orchestration.classifier.EmptyInputException;
import giis.retorch.orchestration.generator.OrchestrationGenerator;
import giis.retorch.orchestration.model.ExecutionPlan;
import giis.retorch.orchestration.orchestrator.NoFinalActivitiesException;
import giis.retorch.orchestration.scheduler.NoTGroupsInTheSchedulerException;
import giis.retorch.orchestration.scheduler.NotValidSystemException;
import giis.retorch.profiling.main.UsageProfilerToolBox;
import giis.retorch.profiling.profilegeneration.ProfileGenerator;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;

@Disabled("Exclude to execute this class when pushing the SUT")
class RetorchGenerateJenkinfileTest {
    @Test
    void testGenerateJenkinsfile() throws NoFinalActivitiesException, NoTGroupsInTheSchedulerException, EmptyInputException, IOException, URISyntaxException, NotValidSystemException, ClassNotFoundException {
        OrchestrationGenerator orch = new OrchestrationGenerator();
        orch.generateJenkinsfile("com.fullteaching.e2e.no_elastest.functional.test", "FullTeaching", "./");

        // Step 1: compute average lifecycle durations from Jenkins execution CSV files
        UsageProfilerToolBox usageProfiler = new UsageProfilerToolBox();
        usageProfiler.generateAverageDurationCSVFile("executiondata", "./output/averagedurationfile.csv");

        ExecutionPlan plan = orch.getExecutionPlan("com.fullteaching.e2e.no_elastest.functional.test", "FullTeaching");

        usageProfiler.generateProfiles(plan,"FullTeaching","./output/averagedurationfile.csv","./output/",3600,1);

    }
}
