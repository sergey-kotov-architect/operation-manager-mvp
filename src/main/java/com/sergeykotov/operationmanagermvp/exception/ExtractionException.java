package com.sergeykotov.operationmanagermvp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR, reason = "failed to extract data from database")
public class ExtractionException extends RuntimeException {
}