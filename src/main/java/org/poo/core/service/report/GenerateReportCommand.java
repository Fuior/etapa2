package org.poo.core.service.report;

import lombok.Getter;
import org.poo.core.service.Command;
import org.poo.fileio.CommandInput;

@Getter
public final class GenerateReportCommand implements Command {

    private final ReportingService reportingService;
    private final CommandInput reportDetails;
    private Report report;

    public GenerateReportCommand(final ReportingService reportingService,
                                 final CommandInput reportDetails) {

        this.reportingService = reportingService;
        this.reportDetails = reportDetails;
    }

    @Override
    public void execute() {
        report = reportingService.generateReport(reportDetails);
    }
}
