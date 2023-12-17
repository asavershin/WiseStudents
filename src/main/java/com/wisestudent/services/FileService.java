package com.wisestudent.services;

import com.wisestudent.properties.MinioProperties;
import com.wisestudent.dto.FileDto;
import com.wisestudent.dto.UploadFileDto;
import com.wisestudent.exceptions.BucketException;
import com.wisestudent.exceptions.DeleteFileException;
import com.wisestudent.exceptions.GetFilesException;
import com.wisestudent.exceptions.ImageUploadException;
import com.wisestudent.models.File;
import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FileService {
    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    public <T extends File> List<T> saveFiles(List<MultipartFile> files, Class<T> fileType) {
        try {
            createBucket();
        } catch (Exception e) {
            throw new ImageUploadException("Image upload failed: " + e.getMessage());
        }

        List<UploadFileDto> newFiles = new ArrayList<>();
        List<T> fileNameForDB = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file == null || file.getOriginalFilename() == null) {
                throw new ImageUploadException("Image must have name.");
            }

            var fileName = generateFileName(file);
            InputStream inputStream;

            try {
                inputStream = file.getInputStream();
            } catch (Exception e) {
                throw new ImageUploadException("Image upload failed: " + e.getMessage());
            }

            try {
                T fileEntity = fileType.getDeclaredConstructor().newInstance();
                fileEntity.setFile(fileName);
                fileNameForDB.add(fileEntity);
            } catch (Exception e) {
                throw new ImageUploadException("Failed to create file entity: " + e.getMessage());
            }

            newFiles.add(new UploadFileDto(fileName, inputStream));
        }

        for (var file : newFiles) {
            saveFile(file);
        }

        return fileNameForDB;
    }
    @SneakyThrows
    public <T extends File> List<FileDto> getFilesData(Set<T> files) {
        if (files.isEmpty()) {
            return new ArrayList<>();
        }
        return files.stream()
                .map(file -> {
                    var fileName = file.getFile();
                    GetObjectResponse objectResponse = null;
                    String contentType = null;
                    byte[] fileContent = null;
                    try {
                        objectResponse = minioClient.getObject(GetObjectArgs.builder()
                                .bucket(minioProperties.getBucket())
                                .object(fileName)
                                .build());
                        contentType = Files.probeContentType(Path.of(fileName));
                        fileContent = IOUtils.toByteArray(objectResponse);
                    } catch (Exception e) {
                        throw new GetFilesException("Ошибка с получением файлов: " + e.getMessage());
                    }
                    return Optional.of(new FileDto(fileName, contentType, fileContent));
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    public <T extends File> void deleteFiles(Set<T> files) {
        if (files == null || files.size() == 0) {
            return;
        }
        if (!bucketExists(minioProperties.getBucket())) {
            throw new BucketException("Папки не существует.");
        }
        try {
            for (var file : files) {
                minioClient.removeObject(
                        RemoveObjectArgs.builder()
                                .bucket(minioProperties.getBucket())
                                .object(file.getFile())
                                .build());
            }
        } catch (Exception e) {
            throw new DeleteFileException("Ошибка в удалении файла: " + e.getMessage());
        }
    }

    private String generateFileName(final MultipartFile file) {
        String extension = getExtension(file);
        return UUID.randomUUID() + "." + extension;
    }

    private String getExtension(final MultipartFile file) {
        return Objects.requireNonNull(file.getOriginalFilename())
                .substring(file.getOriginalFilename().lastIndexOf(".") + 1);
    }

    @SneakyThrows
    private void saveFile(final UploadFileDto file) {
        minioClient.putObject(PutObjectArgs.builder()
                .stream(file.getInputStream(), file.getInputStream().available(), -1)
                .bucket(minioProperties.getBucket())
                .object(file.getName())
                .build());
    }

    @SneakyThrows
    private void createBucket() {
        boolean found = minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket(minioProperties.getBucket())
                .build());
        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(minioProperties.getBucket())
                    .build());
        }
    }

    @SneakyThrows(Exception.class)
    public boolean bucketExists(String bucketName) {
        return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
    }

}
