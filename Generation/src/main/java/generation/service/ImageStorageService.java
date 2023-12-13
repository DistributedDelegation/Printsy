package generation.service;

import com.ibm.cloud.objectstorage.ClientConfiguration;
import com.ibm.cloud.objectstorage.HttpMethod;
import com.ibm.cloud.objectstorage.SdkClientException;
import com.ibm.cloud.objectstorage.auth.AWSStaticCredentialsProvider;
import com.ibm.cloud.objectstorage.auth.BasicAWSCredentials;
import com.ibm.cloud.objectstorage.oauth.TokenManager;
import com.ibm.cloud.objectstorage.services.s3.AmazonS3;
import com.ibm.cloud.objectstorage.services.s3.AmazonS3ClientBuilder;
import com.ibm.cloud.objectstorage.services.s3.model.ObjectMetadata;
import com.ibm.cloud.objectstorage.services.s3.model.PutObjectRequest;
import com.ibm.cloud.objectstorage.oauth.BasicIBMOAuthCredentials;
import com.ibm.cloud.objectstorage.auth.AWSCredentials;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.core.io.Resource;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.time.Duration;
import java.net.URL;


@Service
public class ImageStorageService {

    private AmazonS3 s3Client;

    @Value("${IBM_CLOUD_ENDPOINTS}")
    private String endpointUrl;

    @Value("${IBM_CLOUD_BUCKET_NAME}")
    private String bucketName;

    @Value("${IBM_CLOUD_ACCESS_KEY_ID}")
    private String accessKeyId;

    @Value("${IBM_CLOUD_SECRET_ACCESS_KEY}")
    private String secretAccessKey;

    @Value("${IBM_CLOUD_API_KEY}")
    private String apiKey;

    @Value("${IBM_CLOUD_RESOURCE_INSTANCE_ID}")
    private String serviceInstanceId;
    
    @PostConstruct
    public void init() {
        AWSCredentials credentials = new BasicIBMOAuthCredentials(apiKey, serviceInstanceId);
        //BasicAWSCredentials credentials = new BasicAWSCredentials(accessKeyId, secretAccessKey);
        ClientConfiguration clientConfig = new ClientConfiguration()
                .withRequestTimeout((int) Duration.ofMinutes(1).toMillis());
        clientConfig.setUseTcpKeepAlive(true);

        try {
            this.s3Client = AmazonS3ClientBuilder
                    .standard()
                    .withCredentials(new AWSStaticCredentialsProvider(credentials))
                    .withEndpointConfiguration(new AmazonS3ClientBuilder.EndpointConfiguration(endpointUrl, "eu"))
                    .withPathStyleAccessEnabled(true)
                    .withClientConfiguration(clientConfig)
                    .build();
        }  catch (SdkClientException sdke) {
            System.out.printf("SDK Error: %s\n", sdke.getMessage());
        } catch (Exception e) {
            System.out.printf("Error: %s\n", e.getMessage());
        }
    }

    public String uploadImage(InputStream imageStream, String fileName, String contentType) {
        System.out.println("Uploading image to IBM Cloud Object Storage");
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(contentType);
        PutObjectRequest request = new PutObjectRequest(bucketName, fileName, imageStream, metadata);

        s3Client.putObject(request);

        // Generate the public URL
        return s3Client.getUrl(bucketName, fileName).toString();
    }
    
    public String processAndUploadImage(String imageUrl, String id) {
        System.out.println("Start: Processing and uploading image");
        System.out.println(imageUrl);

        try {
            InputStream imageStream = new URL(imageUrl).openStream();

            // Generate a unique file name for the image
            String fileName = id + ".png";
            String contentType = "image/png";


            try {
                // Upload the image and return the URL
                return uploadImage(imageStream, fileName, contentType);
            } catch (Exception e) {
                throw new RuntimeException("Error uploading image", e);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error processing image", e);
        }   
    }

    public String deleteImage(String id) {
        String imageName = id + ".png";
        try {
            s3Client.deleteObject(bucketName, imageName);
            return "Successfully deleted image!";
        } catch (Exception e) {
            throw new RuntimeException("Error deleting image", e);
        }
    }
}
