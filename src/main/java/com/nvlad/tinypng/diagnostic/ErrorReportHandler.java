package com.nvlad.tinypng.diagnostic;

import com.intellij.diagnostic.AbstractMessage;
import com.intellij.openapi.diagnostic.ErrorReportSubmitter;
import com.intellij.openapi.diagnostic.IdeaLoggingEvent;
import com.intellij.openapi.diagnostic.SubmittedReportInfo;
import com.intellij.util.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class ErrorReportHandler extends ErrorReportSubmitter {
    @NotNull
    @Override
    public String getReportActionText() {
        return "Report to TinyPNG Optimizer";
    }

    @Override
    public boolean submit(@NotNull IdeaLoggingEvent[] events, @Nullable String additionalInfo, @NotNull Component parentComponent, @NotNull Consumer<? super SubmittedReportInfo> consumer) {
        super.submit(events, additionalInfo, parentComponent, consumer);
        for (IdeaLoggingEvent event : events) {
            Throwable throwable = event.getThrowable();
            if (event.getData() instanceof AbstractMessage) {
                throwable = ((AbstractMessage) event.getData()).getThrowable();
            }

            SentryErrorReporter.submitErrorReport(throwable, additionalInfo, consumer);
        }

        return true;
    }
}
