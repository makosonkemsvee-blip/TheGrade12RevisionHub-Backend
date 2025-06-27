package com.investhoodit.RevisionHub.dto;

public record ApiResponse<S>(boolean success, Object data, String message) {}
