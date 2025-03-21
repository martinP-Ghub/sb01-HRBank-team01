package com.project.hrbank.util.converter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import org.springframework.web.multipart.MultipartFile;

public class FileConverter {
	public static byte[] processMultipartFile(MultipartFile file) throws IOException {
		return processFileData(file.getBytes());
	}

	public static byte[] processFileData(byte[] fileData) throws IOException {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(fileData), StandardCharsets.UTF_8));
			 ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8))) {
			String line;
			while ((line = reader.readLine()) != null) {
				writer.write(line);
				writer.newLine();
			}
			writer.flush();
			return outputStream.toByteArray();
		}
	}

}
