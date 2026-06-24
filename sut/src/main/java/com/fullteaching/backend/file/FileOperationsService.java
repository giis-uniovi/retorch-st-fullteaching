package com.fullteaching.backend.file;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import com.fullteaching.backend.filegroup.FileGroup;
import com.fullteaching.backend.util.LogSanitizer;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.List;

@Service
public class FileOperationsService {

    private static final Logger log = LoggerFactory.getLogger(FileOperationsService.class);

    private static final String LOG_ERROR_MESSAGE    = "Error Message:    {}";
    private static final String LOG_HTTP_STATUS_CODE = "HTTP Status Code: {}";
    private static final String LOG_AWS_ERROR_CODE   = "AWS Error Code:   {}";
    private static final String LOG_ERROR_TYPE       = "Error Type:       {}";
    private static final String LOG_REQUEST_ID       = "Request ID:       {}";
    private static final String LOG_CLIENT_ERROR_MSG = "Error Message: {}";

    private final AmazonS3 amazonS3;

    @Value("${aws.namecard.bucket}")
    private String bucketAWS;

    @Autowired(required = false)
    public FileOperationsService(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    public String getBucketAWS() {
        return bucketAWS;
    }

    public void deleteLocalFile(String fileName, Path folder) {
        Path path = Paths.get(folder.toString(), fileName);
        if (log.isInfoEnabled()) {
            log.info("Deleting local temp file '{}'", LogSanitizer.sanitize(path));
        }
        // Deleting stored file...

        Path o1 = Paths.get(folder.toString());
        try {
            Files.delete(path);
            if (log.isInfoEnabled()) {
                log.info("Local temp file '{}' successfully deleted", LogSanitizer.sanitize(path));
            }
        } catch (NoSuchFileException x) {
            log.error("No such file '{}' or directory '{}'", fileName, o1);
        } catch (DirectoryNotEmptyException x) {
            log.error("Directory '{}' not empty", o1);
        } catch (IOException x) {
            // File permission problems are caught here
            log.error("Permission error: {}", x.toString());
        }
    }

    public void deleteRemoteFile(String fileName, String s3Folder) {
        log.info("Deleting remote file in S3: folder '{}', file '{}'", s3Folder, fileName);
        String bucketName = bucketAWS + s3Folder;
        try {
            amazonS3.deleteObject(new DeleteObjectRequest(bucketName, fileName));
            log.info("S3 DELETION: File '{}' successfully deleted", fileName);
        } catch (AmazonServiceException ase) {
            log.info("Caught an AmazonServiceException.");
            log.info(LOG_ERROR_MESSAGE, ase.getMessage());
            log.info(LOG_HTTP_STATUS_CODE, ase.getStatusCode());
            log.info(LOG_AWS_ERROR_CODE, ase.getErrorCode());
            log.info(LOG_ERROR_TYPE, ase.getErrorType());
            log.info(LOG_REQUEST_ID, ase.getRequestId());
        } catch (AmazonClientException ace) {
            log.info("Caught an AmazonClientException.");
            log.info(LOG_CLIENT_ERROR_MSG, ace.getMessage());
        }
    }

    // Deletes all the real locally stored files given a list of FileGroups
    public void recursiveLocallyStoredFileDeletion(List<FileGroup> fileGroup) {
        log.info("Recursive deletion of all files in children filegroups");
        if (fileGroup != null) {
            for (FileGroup fg : fileGroup) {
                for (com.fullteaching.backend.file.File f : fg.getFiles()) {
                    this.deleteLocalFile(f.getNameIdent(), FileController.FILES_FOLDER);
                }
                this.recursiveLocallyStoredFileDeletion(fg.getFileGroups());
            }
        }
    }

    // Deletes all the real S3 stored files given a list of FileGroups
    public void recursiveS3StoredFileDeletion(List<FileGroup> fileGroup) {
        if (fileGroup != null) {
            for (FileGroup fg : fileGroup) {
                for (com.fullteaching.backend.file.File f : fg.getFiles()) {
                    this.deleteRemoteFile(f.getNameIdent(), "/files");
                }
                this.recursiveS3StoredFileDeletion(fg.getFileGroups());
            }
        }
    }

    public void productionFileSaver(String keyName, String folderName, File f) throws InterruptedException {

        log.info("Uploading an object to S3");

        String bucketName = bucketAWS + "/" + folderName;
        TransferManager tm = TransferManagerBuilder.standard().withS3Client(amazonS3).build();
        // TransferManager processes all transfers asynchronously, so this call will return immediately
        Upload upload = tm.upload(bucketName, keyName, f);
        try {
            // Or you can block and wait for the upload to finish
            upload.waitForCompletion();
            log.info("Upload completed");
        } catch (AmazonClientException amazonClientException) {
            log.error("Unable to upload file, upload was aborted.", amazonClientException);
        }
    }

    public void productionFileDownloader(String fileName, HttpServletResponse response) throws IOException {
        String bucketName = bucketAWS + "/files";
        try {
            log.info("Downloading an object from S3");
            S3Object s3object = amazonS3.getObject(new GetObjectRequest(bucketName, fileName));
            log.info("Content-Type: {}", s3object.getObjectMetadata().getContentType());

            String fileExt = this.getFileExtension(fileName);
            response.setContentType(MimeTypes.getMimeType(fileExt));
            InputStream objectData = s3object.getObjectContent();
            StreamUtils.copy(objectData, response.getOutputStream());
            response.flushBuffer();
            objectData.close();

        } catch (AmazonServiceException ase) {
            log.error("Caught an AmazonServiceException, which means your request made it to Amazon S3, but was rejected with an error response for some reason.");
            log.error(LOG_ERROR_MESSAGE, ase.getMessage());
            log.error(LOG_HTTP_STATUS_CODE, ase.getStatusCode());
            log.error(LOG_AWS_ERROR_CODE, ase.getErrorCode());
            log.error(LOG_ERROR_TYPE, ase.getErrorType());
            log.error(LOG_REQUEST_ID, ase.getRequestId());
        } catch (AmazonClientException ace) {
            log.error("Caught an AmazonClientException, which means the client encountered an internal error while trying to communicate with S3, such as not being able to access the network.");
            log.error(LOG_CLIENT_ERROR_MSG, ace.getMessage());
        } catch (IOException ex) {
            throw new IOException("IOError writing file to output stream");
        }
    }

    public void productionFileDeletion(String fileName, String s3Folder) {
        String bucketName = bucketAWS + s3Folder;
        try {
            amazonS3.deleteObject(new DeleteObjectRequest(bucketName, fileName));
            log.info("S3 DELETION: File '{}' deleted successfully", fileName);
        } catch (AmazonServiceException ase) {
            log.error("Caught an AmazonServiceException.");
            log.error(LOG_ERROR_MESSAGE, ase.getMessage());
            log.error(LOG_HTTP_STATUS_CODE, ase.getStatusCode());
            log.error(LOG_AWS_ERROR_CODE, ase.getErrorCode());
            log.error(LOG_ERROR_TYPE, ase.getErrorType());
            log.error(LOG_REQUEST_ID, ase.getRequestId());
        } catch (AmazonClientException ace) {
            log.error("Caught an AmazonClientException.");
            log.error(LOG_CLIENT_ERROR_MSG, ace.getMessage());
        }
    }

    public String getFileNameFromURL(String url) {
        return (url.substring(url.lastIndexOf('/') + 1));
    }

    private String getFileExtension(String name) {
        return name.substring(name.lastIndexOf('.') + 1);
    }

    public String getEncodedPictureName(String originalFileName) {
        if (originalFileName == null) {
            originalFileName = "";
        }
        // Getting the image extension, discarding any path-like characters it may contain
        String picExtension = this.getFileExtension(originalFileName).replaceAll("[^A-Za-z0-9]", "");
        // Appending a random integer to the name
        originalFileName += (Math.random() * (Integer.MIN_VALUE - Integer.MAX_VALUE));
        // Encoding original file name + random integer
        originalFileName = new BCryptPasswordEncoder().encode(originalFileName);
        if (originalFileName == null) {
            originalFileName = "";
        }
        // Deleting all non-alphanumeric characters
        originalFileName = originalFileName.replaceAll("[^A-Za-z0-9\\$]", "");
        // Adding the extension
        originalFileName += "." + picExtension;
        return originalFileName;
    }

}
