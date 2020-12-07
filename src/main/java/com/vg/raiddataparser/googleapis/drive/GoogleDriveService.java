package com.vg.raiddataparser.googleapis.drive;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.vg.raiddataparser.googleapis.GoogleServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GoogleDriveService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GoogleDriveService.class.getName());
    private final Drive driveService = GoogleServiceUtil.getDriveService();

    public GoogleDriveService() {
        LOGGER.info("Initializing GoogleDriveService...");
        if (driveService == null) {
            throw new NullPointerException("Error while initializing GoogleDriveService: Drive service is null.");
        }
        LOGGER.info("GoogleDriveService initialized");
    }

    /**
     * Retrieve a list of all File resources in Drive
     *
     * @return List of File resources
     * @throws IOException
     */
    private List<File> getFiles() throws IOException {
        FileList result = Objects.requireNonNull(driveService).files().list().setFields("files(id, name, trashed)").execute();
        return new ArrayList<>(result.getFiles());
    }

    /**
     * Check if file specified by ID exists
     *
     * @param id ID of the file
     * @return true if file exists and is not trashed
     * @throws IOException
     */
    public boolean fileExists(String id) throws IOException {
        List<File> files = getFiles();
        for (File file : files) {
            if (id.equals(file.getId()) && !file.getTrashed()) {
                return true;
            }
        }
        return false;
    }
}
