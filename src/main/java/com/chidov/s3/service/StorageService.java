package com.chidov.s3.service;

import java.io.File;
import java.io.FileOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;

@Service
public class StorageService {
	
	@Value("${spring.data.couchbase.bucket-name}")
	private String bucketName;
	
	
	@Autowired
	public AmazonS3 s3Client;
	
	
	
	public String uploadFile(MultipartFile file) {
		
		File fileObj=convertMultipartFileToFile(file);
		
		String fileName=System.currentTimeMillis()+"_"+file.getOriginalFilename();
		
		s3Client.putObject(new PutObjectRequest(bucketName, fileName, fileObj));
		
		fileObj.delete();
		
		return "file uploaded "+ fileName;
	}
	
	
	
	
	
	
	public byte[] downloadFile(String fileName) {
		S3Object s3Object=s3Client.getObject(bucketName, fileName);
		S3ObjectInputStream inputStream=s3Object.getObjectContent();
		
		byte[] content = null;
		try {
			 content=IOUtils.toByteArray(inputStream);
		} catch (Exception e) {
		e.printStackTrace();
		}
		return content;
	}
	
	
	public String deleteFile(String fileName) {
		s3Client.deleteObject(bucketName, fileName);
		return "file deleted"+fileName;
	}
	
    private File convertMultipartFileToFile(MultipartFile file) {
    	File convertedFile=new File(file.getOriginalFilename());
    	try (FileOutputStream fileOutputStream=new FileOutputStream(convertedFile)) {
    		fileOutputStream.write(file.getBytes());
			
		} catch (Exception e) {
		//Log.error("error converting multipart file",e);
		
		}
    	return convertedFile;
    }
	
}
