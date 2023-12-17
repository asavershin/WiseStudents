package com.wisestudent.dto;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.InputStream;
@RequiredArgsConstructor
@Getter
public class UploadFileDto {
    private final String name;
    private final InputStream inputStream;
}
