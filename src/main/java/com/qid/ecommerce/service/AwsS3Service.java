package com.qid.ecommerce.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
@Slf4j
public class AwsS3Service {

    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    @Value("${aws.region}")
    private String awsRegion;

    @Value("${aws.s3.accessKeyId}")
    private String awsS3AccessKeyId;

    @Value("${aws.s3.secretAccessKey}")
    private String awsS3SecretAccessKey;

    public String saveImageToS3(MultipartFile photo) {
        try {
            String s3FileName = photo.getOriginalFilename();

            // create aws credentials using access key ID and access secret access key
            BasicAWSCredentials awsCredentials = new BasicAWSCredentials(awsS3AccessKeyId, awsS3SecretAccessKey);

            // create a s3 client with config credentials and region
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                    .withRegion(awsRegion)
                    .build();

            // get input stream from photo
            InputStream inputStream = photo.getInputStream();

            // set metadata for the object
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("image/jpg");

            // create a pull request to upload the image to s3
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, s3FileName, inputStream, metadata);
            s3Client.putObject(putObjectRequest);

            String s3Url = "https://" + bucketName + ".s3." + awsRegion + ".amazonaws.com" + s3FileName;

            return s3Url;


        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to upload image to S3: " + e.getMessage());
        }
    }
}
