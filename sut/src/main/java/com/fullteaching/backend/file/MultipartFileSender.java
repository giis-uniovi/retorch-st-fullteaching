package com.fullteaching.backend.file;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;

public class MultipartFileSender {

    private static final int DEFAULT_BUFFER_SIZE = 20480; // ..bytes = 20KB.
    private static final long DEFAULT_EXPIRE_TIME = 604800000L; // ..ms = 1 week.
    private static final String MULTIPART_BOUNDARY = "MULTIPART_BYTERANGES";
    private static final String CONTENT_RANGE_HEADER = "Content-Range";
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    Path filepath;
    HttpServletRequest request;
    HttpServletResponse response;

    public MultipartFileSender() {
        // Use factory methods: fromPath, fromFile, or fromURIString
    }

    public static MultipartFileSender fromPath(Path path) {
        return new MultipartFileSender().setFilepath(path);
    }

    public static MultipartFileSender fromFile(java.io.File file) {
        return new MultipartFileSender().setFilepath(file.toPath());
    }

    public static MultipartFileSender fromURIString(String uri) {
        return new MultipartFileSender().setFilepath(Paths.get(uri));
    }

    private MultipartFileSender setFilepath(Path filepath) {
        this.filepath = filepath;
        return this;
    }

    public MultipartFileSender with(HttpServletRequest httpRequest) {
        request = httpRequest;
        return this;
    }

    public MultipartFileSender with(HttpServletResponse httpResponse) {
        response = httpResponse;
        return this;
    }

    public void serveResource() throws IOException {
        if (response == null || request == null) {
            return;
        }

        if (!Files.exists(filepath)) {
            logger.error("File doesn't exist at URI : {}", filepath.toAbsolutePath());
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        long length = Files.size(filepath);
        String fileName = filepath.getFileName().toString();
        FileTime lastModifiedObj = Files.getLastModifiedTime(filepath);

        if (fileName.isEmpty()) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
        long lastModified = LocalDateTime
                .ofInstant(lastModifiedObj.toInstant(), ZoneId.systemDefault())
                .toEpochSecond(ZoneOffset.UTC);
        String contentType = MimeTypes.MIME_APPLICATION_OCTET_STREAM;

        if (validateCachingHeaders(fileName, lastModified)) return;
        if (validateConditionalHeaders(fileName, lastModified)) return;

        Range full = new Range(0, length - 1, length);
        List<Range> ranges = new ArrayList<>();
        String range = request.getHeader("Range");
        if (range != null) {
            Optional<List<Range>> resolved = resolveRanges(range, fileName, length, full);
            if (resolved.isEmpty()) return;
            ranges = resolved.get();
        }

        String accept = request.getHeader("Accept");
        String disposition = accept != null && HttpUtils.accepts(accept, contentType) ? "inline" : "attachment";
        logger.debug("Content-Type : {}", contentType);
        response.reset();
        response.setBufferSize(DEFAULT_BUFFER_SIZE);
        response.setHeader("Content-Type", contentType);
        response.setHeader("Content-Disposition", disposition + ";filename=\"" + fileName + "\"");
        logger.debug("Content-Disposition : {}", disposition);
        response.setHeader("Accept-Ranges", "bytes");
        response.setHeader("ETag", fileName);
        response.setDateHeader("Last-Modified", lastModified);
        response.setDateHeader("Expires", System.currentTimeMillis() + DEFAULT_EXPIRE_TIME);

        try (InputStream input = new BufferedInputStream(Files.newInputStream(filepath));
             ServletOutputStream output = response.getOutputStream()) {
            sendContent(input, output, ranges, full, length, contentType);
        }
    }

    // Returns true if the response was already sent (caller should abort).
    private boolean validateCachingHeaders(String fileName, long lastModified) throws IOException {
        String ifNoneMatch = request.getHeader("If-None-Match");
        if (ifNoneMatch != null && HttpUtils.matches(ifNoneMatch, fileName)) {
            response.setHeader("ETag", fileName);
            response.sendError(HttpServletResponse.SC_NOT_MODIFIED);
            return true;
        }
        long ifModifiedSince = request.getDateHeader("If-Modified-Since");
        if (ifNoneMatch == null && ifModifiedSince != -1 && ifModifiedSince + 1000 > lastModified) {
            response.setHeader("ETag", fileName);
            response.sendError(HttpServletResponse.SC_NOT_MODIFIED);
            return true;
        }
        return false;
    }

    // Returns true if the response was already sent (caller should abort).
    private boolean validateConditionalHeaders(String fileName, long lastModified) throws IOException {
        String ifMatch = request.getHeader("If-Match");
        if (ifMatch != null && !HttpUtils.matches(ifMatch, fileName)) {
            response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
            return true;
        }
        long ifUnmodifiedSince = request.getDateHeader("If-Unmodified-Since");
        if (ifUnmodifiedSince != -1 && ifUnmodifiedSince + 1000 <= lastModified) {
            response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
            return true;
        }
        return false;
    }

    // Returns the resolved range list, or empty if a 416 was already sent.
    private Optional<List<Range>> resolveRanges(String range, String fileName, long length, Range full) throws IOException {
        if (!range.matches("^bytes=\\d*+-\\d*+(,\\d*+-\\d*+)*+$")) {
            response.setHeader(CONTENT_RANGE_HEADER, "bytes */" + length);
            response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
            return Optional.empty();
        }

        List<Range> ranges = new ArrayList<>();
        String ifRange = request.getHeader("If-Range");
        if (ifRange != null && !ifRange.equals(fileName)) {
            try {
                long ifRangeTime = request.getDateHeader("If-Range");
                if (ifRangeTime != -1) {
                    ranges.add(full);
                }
            } catch (IllegalArgumentException ignore) {
                ranges.add(full);
            }
        }

        if (ranges.isEmpty()) {
            Optional<List<Range>> parsed = parseRangeParts(range, length);
            if (parsed.isEmpty()) return Optional.empty();
            ranges = parsed.get();
        }
        return Optional.of(ranges);
    }

    // Returns the parsed ranges, or empty if a 416 was already sent.
    private Optional<List<Range>> parseRangeParts(String range, long length) throws IOException {
        List<Range> ranges = new ArrayList<>();
        for (String part : range.substring(6).split(",")) {
            // Assuming a file with length of 100, the following examples returns bytes at:
            // 50-80 (50 to 80), 40- (40 to length=100), -20 (length-20=80 to length=100).
            long start = Range.sublong(part, 0, part.indexOf("-"));
            long end = Range.sublong(part, part.indexOf("-") + 1, part.length());

            if (start == -1) {
                start = length - end;
                end = length - 1;
            } else if (end == -1 || end > length - 1) {
                end = length - 1;
            }

            if (start > end) {
                response.setHeader(CONTENT_RANGE_HEADER, "bytes */" + length);
                response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
                return Optional.empty();
            }
            ranges.add(new Range(start, end, length));
        }
        return Optional.of(ranges);
    }

    private void sendContent(InputStream input, ServletOutputStream output, List<Range> ranges, Range full,
                             long length, String contentType) throws IOException {
        if (ranges.isEmpty() || ranges.getFirst() == full) {
            // Return full file.
            logger.info("Return full file");
            response.setContentType(contentType);
            response.setHeader(CONTENT_RANGE_HEADER, "bytes " + full.start + "-" + full.end + "/" + full.total);
            response.setHeader("Content-Length", String.valueOf(full.length));
            Range.copy(input, output, length, full.start, full.length);

        } else if (ranges.size() == 1) {
            // Return single part of file.
            Range r = ranges.getFirst();
            logger.info("Return 1 part of file : from ({}) to ({})", r.start, r.end);
            response.setContentType(contentType);
            response.setHeader(CONTENT_RANGE_HEADER, "bytes " + r.start + "-" + r.end + "/" + r.total);
            response.setHeader("Content-Length", String.valueOf(r.length));
            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
            Range.copy(input, output, length, r.start, r.length);

        } else {
            // Return multiple parts of file.
            response.setContentType("multipart/byteranges; boundary=" + MULTIPART_BOUNDARY);
            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
            for (Range r : ranges) {
                logger.info("Return multi part of file : from ({}) to ({})", r.start, r.end);
                output.println();
                output.println("--" + MULTIPART_BOUNDARY);
                output.println("Content-Type: " + contentType);
                output.println("Content-Range: bytes " + r.start + "-" + r.end + "/" + r.total);
                Range.copy(input, output, length, r.start, r.length);
            }
            output.println();
            output.println("--" + MULTIPART_BOUNDARY + "--");
        }
    }

    private static class Range {
        final long start;
        final long end;
        final long length;
        final long total;

        /**
         * Construct a byte range.
         *
         * @param start Start of the byte range.
         * @param end   End of the byte range.
         * @param total Total length of the byte source.
         */
        public Range(long start, long end, long total) {
            this.start = start;
            this.end = end;
            this.length = end - start + 1;
            this.total = total;
        }

        public static long sublong(String value, int beginIndex, int endIndex) {
            String substring = value.substring(beginIndex, endIndex);
            return (!substring.isEmpty()) ? Long.parseLong(substring) : -1;
        }

        private static void copy(InputStream input, OutputStream output, long inputSize, long start, long length)
                throws IOException {
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int read;

            if (inputSize == length) {
                // Write full range.
                read = input.read(buffer);
                while (read > 0) {
                    output.write(buffer, 0, read);
                    output.flush();
                    read = input.read(buffer);
                }
            } else {
                long remaining = start;
                while (remaining > 0) {
                    long skipped = input.skip(remaining);
                    if (skipped <= 0) break;
                    remaining -= skipped;
                }
                long toRead = length;

                read = input.read(buffer);
                while (read > 0) {
                    toRead -= read;
                    if (toRead > 0) {
                        output.write(buffer, 0, read);
                        output.flush();
                    } else {
                        output.write(buffer, 0, (int) toRead + read);
                        output.flush();
                        break;
                    }
                    read = input.read(buffer);
                }
            }
        }
    }

    private static class HttpUtils {

        /**
         * Returns true if the given accept header accepts the given value.
         *
         * @param acceptHeader The accept header.
         * @param toAccept     The value to be accepted.
         * @return True if the given accept header accepts the given value.
         */
        public static boolean accepts(String acceptHeader, String toAccept) {
            if (acceptHeader == null || toAccept == null) return false;

            // 1. Split using a non-backtracking regex or simple split
            // Using \s*+ to be possessive and prevent ReDoS
            String[] parts = acceptHeader.split("\\s*+[,;]\\s*+");

            // 2. Use a Set for O(1) lookups instead of sorting/binary search
            Set<String> acceptValues = new HashSet<>(Arrays.asList(parts));

            // 3. Check specific conditions
            return acceptValues.contains(toAccept)
                    || acceptValues.contains(toAccept.replaceAll("/.*$", "/*"))
                    || acceptValues.contains("*/*");
        }

        /**
         * Returns true if the given match header matches the given value.
         *
         * @param matchHeader The match header.
         * @param toMatch     The value to be matched.
         * @return True if the given match header matches the given value.
         */
        public static boolean matches(String matchHeader, String toMatch) {
            String[] matchValues = matchHeader.split("\\s*,\\s*");
            Arrays.sort(matchValues);
            return Arrays.binarySearch(matchValues, toMatch) > -1 || Arrays.binarySearch(matchValues, "*") > -1;
        }
    }
}
