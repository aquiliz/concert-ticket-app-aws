package com.aquiliz.ticket.booking;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSBatchResponse;
import com.amazonaws.services.lambda.runtime.events.SQSBatchResponse.BatchItemFailure;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProcessSQSMessageBatch implements RequestHandler<SQSEvent, SQSBatchResponse> {

  private static final ObjectMapper objectMapper = new ObjectMapper();
  private final AmazonS3 s3Client = AmazonS3ClientBuilder.standard().build();
  private static final String DEFAULT_BUCKET_NAME = "concert-tickets-storage";

  @Override
  public SQSBatchResponse handleRequest(SQSEvent sqsEvent, Context context) {
    LambdaLogger log = context.getLogger();
    List<BatchItemFailure> batchItemFailures = new ArrayList<>();
    String messageId = "";
    for (SQSEvent.SQSMessage sqsMessage : sqsEvent.getRecords()) {
      try {
        TicketMessage ticketMessage = objectMapper.readValue(sqsMessage.getBody(),
            TicketMessage.class);
        log.log("Initiating processing of ticketMessage: " + ticketMessage);
        ByteArrayOutputStream outputStream = buildPdfFile(ticketMessage);
        uploadToS3(ticketMessage, outputStream, log);
      } catch (Exception e) {
        log.log("Processing Error: " + e);
        batchItemFailures.add(new SQSBatchResponse.BatchItemFailure(messageId));
      }
    }
    return new SQSBatchResponse(batchItemFailures);
  }

  private void uploadToS3(TicketMessage ticketMessage,
      ByteArrayOutputStream outputStream, LambdaLogger log) {
    byte[] pdfBytes = outputStream.toByteArray();
    ObjectMetadata metadata = new ObjectMetadata();
    metadata.setContentLength(pdfBytes.length);

    InputStream inputStream = new ByteArrayInputStream(pdfBytes);

    String s3BucketName = System.getenv("S3_BUCKET_NAME") == null ? DEFAULT_BUCKET_NAME
        : System.getenv("S3_BUCKET_NAME");
    String fileName =
        "ticket-" + ticketMessage.getTicketPurchaseId() + "-" + UUID.randomUUID() + ".pdf";
    PutObjectRequest request = new PutObjectRequest(s3BucketName, fileName, inputStream, metadata);
    PutObjectResult putObjectResult = s3Client.putObject(request);

    // In a real (prod) scenario this link would be sent by email or just displayed in the UI so that
    // the user can download their ticket
    String preSignedUrl = generatePreSignedUrl(s3BucketName, fileName);
    log.log("Generated pre-signed URL: " + preSignedUrl);

    log.log("Successful putObject into S3. Result file metaData: " + putObjectResult.getMetadata());
  }

  /**
   * By default, all S3 objects are private. Only the object owner has permission to access them.
   * However, the object owner can optionally share objects with others by creating a presigned URL,
   * using their own security credentials, to grant time-limited permission to download the
   * objects.
   */
  private String generatePreSignedUrl(String s3BucketName, String fileName) {
    java.util.Date expiration = new java.util.Date();
    long expTimeMillis = expiration.getTime();
    //download link will expire in 1 hour
    expTimeMillis += 1000 * 60 * 60;
    expiration.setTime(expTimeMillis);
    GeneratePresignedUrlRequest generatePresignedUrlRequest =
        new GeneratePresignedUrlRequest(s3BucketName, fileName)
            .withMethod(HttpMethod.GET)
            .withExpiration(expiration);
    URL url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);
    return url.toString();
  }

  private static ByteArrayOutputStream buildPdfFile(TicketMessage ticketMessage)
      throws DocumentException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    Document document = new Document();
    PdfWriter.getInstance(document, outputStream);

    document.open();
    document.add(
        new Paragraph("Ticket #" + ticketMessage.getTicketPurchaseId() + " for concert "
            + ticketMessage.getConcertName(),
            FontFactory.getFont(FontFactory.HELVETICA, 26, BaseColor.BLACK)));
    document.add(new Paragraph("Ticket issued to: " + ticketMessage.getIssuedTo()));
    document.add(new Paragraph("Number of attendees: " + ticketMessage.getTicketsCount()));
    document.close();
    return outputStream;
  }
}
