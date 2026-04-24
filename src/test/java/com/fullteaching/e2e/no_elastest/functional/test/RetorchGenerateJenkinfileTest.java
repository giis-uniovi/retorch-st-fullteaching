package com.fullteaching.e2e.no_elastest.functional.test;



import giis.retorch.orchestration.classifier.EmptyInputException;
import giis.retorch.orchestration.generator.OrchestrationGenerator;
import giis.retorch.orchestration.model.ExecutionPlan;
import giis.retorch.orchestration.orchestrator.NoFinalActivitiesException;
import giis.retorch.orchestration.scheduler.NoTGroupsInTheSchedulerException;
import giis.retorch.orchestration.scheduler.NotValidSystemException;
import giis.retorch.profiling.main.UsageProfilerToolBox;
import giis.retorch.profiling.profilegeneration.ProfileGenerator;
import giis.retorch.profiling.profilegeneration.ProfilePlotter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;

@Disabled("Exclude to execute this class when pushing the SUT")
class RetorchGenerateJenkinfileTest {
    @Test
    void testGenerateJenkinsfile() throws NoFinalActivitiesException, NoTGroupsInTheSchedulerException, EmptyInputException, IOException, URISyntaxException, NotValidSystemException, ClassNotFoundException {
        OrchestrationGenerator orch= new OrchestrationGenerator();
        orch.generateJenkinsfile("com.fullteaching.e2e.no_elastest.functional.test","FullTeaching", "./");

        UsageProfilerToolBox usageProfiler = new UsageProfilerToolBox();
        usageProfiler.generateAverageDurationCSVFile("executiondata","./averagedurationfile.csv");
        ExecutionPlan plan=orch.getExecutionPlan("com.fullteaching.e2e.no_elastest.functional.test","FullTeaching");

        ProfileGenerator profileGenerator = new ProfileGenerator();
        profileGenerator.generateExecutionPlanCapacitiesUsage(plan,"./averagedurationfile.csv","./output/profile.csv",3600,1);
        ProfilePlotter plotter = new ProfilePlotter("./output/profile.csv");
        plotter.generateTotalTJobUsageProfileCharts("./output","RETORCH","VM");
    }
}


