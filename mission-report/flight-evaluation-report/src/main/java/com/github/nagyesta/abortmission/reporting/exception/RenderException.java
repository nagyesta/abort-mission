package com.github.nagyesta.abortmission.reporting.exception;

import org.springframework.boot.ExitCodeGenerator;

public class RenderException extends RuntimeException implements ExitCodeGenerator {
    @Override
    public int getExitCode() {
        return 1;
    }
}
